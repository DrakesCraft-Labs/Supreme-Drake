package com.github.relativobr.supreme.machine.multiblock;

import com.github.relativobr.supreme.generic.recipe.InventoryRecipe;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;

/**
 * Logica pura de como el transporte automatico entra los materiales en el Electric Core
 * Fabricator.
 *
 * La rejilla 3x3 de entrada (InventoryRecipe.MEDIUM_INPUT) repite cada material en un rol: el
 * principal ocupa las dos filas exteriores y el secundario la del medio. Esta clase calcula que
 * huecos le corresponden a cada material y decide, segun el estado actual del inventario, que
 * huecos siguen aceptando ese material.
 *
 * Se mantiene sin dependencias de Supreme ni Slimefun (solo Material e ints) para que los tests
 * puedan ejecutarla sin levantar servidor ni plugin.
 */
public final class CoreFabricatorSlots {

  private CoreFabricatorSlots() {}

  /**
   * Une todas las rejillas de receta y devuelve, por material, los huecos del MEDIUM_INPUT donde
   * aparece.
   *
   * El orden de los conjuntos de huecos no afecta al matching (agrega por totales), pero se
   * conserva el de aparicion para que el comportamiento sea estable.
   *
   * @param recipeGrids rejillas planas de 9 materiales, tipicamente {@code
   *     CustomCoreRecipe.gridMaterials(recipe)}
   */
  public static Map<Material, int[]> computeRoleSlots(List<Material[]> recipeGrids) {
    Map<Material, List<Integer>> roles = new LinkedHashMap<>();
    for (Material[] grid : recipeGrids) {
      if (grid == null) {
        continue;
      }
      for (int i = 0; i < grid.length && i < InventoryRecipe.MEDIUM_INPUT.length; i++) {
        Material material = grid[i];
        if (material == null) {
          continue;
        }
        List<Integer> slots = roles.computeIfAbsent(material, ignored -> new ArrayList<>());
        int slot = InventoryRecipe.MEDIUM_INPUT[i];
        if (!slots.contains(slot)) {
          slots.add(slot);
        }
      }
    }
    Map<Material, int[]> result = new LinkedHashMap<>();
    for (Map.Entry<Material, List<Integer>> entry : roles.entrySet()) {
      List<Integer> slots = entry.getValue();
      int[] array = new int[slots.size()];
      for (int i = 0; i < slots.size(); i++) {
        array[i] = slots.get(i);
      }
      result.put(entry.getKey(), array);
    }
    return result;
  }

  /**
   * Huecos de un rol que admiten el item que llega, alineados con los arrays del estado.
   *
   * Quedan fuera los huecos llenos y los ocupados por otro material: asi el smart-fill del cargo,
   * que abandona cuando la primera ranura del mismo material esta llena, salta a la siguiente del
   * rol en vez de quedarse atascado. Quedan dentro las parciales -- primero las mas vacias, para
   * no fragmentar la entrada -- y las vacias.
   *
   * @param roleSlots huecos del rol, en el mismo orden que {@code amounts} y {@code maxStacks}
   * @param amounts cantidad actual de cada hueco; 0 = vacio, negativo = lo ocupa otro material
   * @param maxStacks capacidad maxima de cada hueco
   */
  public static int[] eligibleSlots(int[] roleSlots, int[] amounts, int[] maxStacks) {
    if (roleSlots == null || roleSlots.length == 0) {
      return new int[0];
    }
    List<Integer> withRoom = new ArrayList<>();
    List<Integer> empty = new ArrayList<>();
    for (int i = 0; i < roleSlots.length; i++) {
      int amount = i < amounts.length ? amounts[i] : 0;
      if (amount < 0) {
        // Lo ocupa otro material: no admite el item entrante.
        continue;
      }
      if (amount == 0) {
        empty.add(i);
      } else if (i < maxStacks.length && amount < maxStacks[i]) {
        withRoom.add(i);
      }
    }
    withRoom.sort(Comparator.comparingInt(i -> amounts[i]));
    List<Integer> order = new ArrayList<>(withRoom);
    order.addAll(empty);
    int[] result = new int[order.size()];
    for (int i = 0; i < order.size(); i++) {
      result[i] = roleSlots[order.get(i)];
    }
    return result;
  }
}