package com.github.relativobr.supreme.machine.multiblock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.relativobr.supreme.generic.recipe.CustomCoreRecipe;
import com.github.relativobr.supreme.generic.recipe.InventoryRecipe;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Simula el transporte automatico del cargo contra los huecos del Electric Core Fabricator.
 *
 * Reproduce la semantica de CargoUtils.insert de Slimefun: deposita en la primera ranura util; si
 * la encuentra del mismo material y llena, con smart-fill abandona y con el modo normal salta a la
 * siguiente; al completar una parcial devuelve el sobrante. La diferencia entre el comportamiento
 * viejo y el nuevo esta en las ranuras que ofrece la maquina: antes las nueve, ahora solo las del
 * rol del material que no esten llenas ni ocupadas por otro material.
 */
class ElectricCoreAutomationTest {

  private static final Material HONEY = Material.HONEY_BOTTLE;
  private static final Material POTATO = Material.POTATO;
  private static final int HONEY_STACK = 16;
  private static final int OTHER_STACK = 64;
  private static final int HONEY_REQUIRED = 6 * HONEY_STACK;
  private static final int POTATO_REQUIRED = 3 * CustomCoreRecipe.slotAmount(OTHER_STACK, 16);

  private static final int[] ALL_INPUT_SLOTS = InventoryRecipe.MEDIUM_INPUT.clone();
  private static Map<Material, int[]> roleSlotsByMaterial;

  @BeforeAll
  static void buildRoleSlotsFromRecipes() {
    List<Material[]> grids = new ArrayList<>();
    for (Material crop : new Material[]{
        Material.POTATO, Material.APPLE, Material.BEETROOT, Material.WHEAT, Material.SUGAR_CANE,
        Material.SWEET_BERRIES, Material.MELON, Material.CARROT, Material.PUMPKIN}) {
      grids.add(CustomCoreRecipe.gridMaterials(HONEY, crop, HONEY));
    }
    grids.add(CustomCoreRecipe.gridMaterials(Material.OAK_LOG, Material.OAK_LEAVES, Material.OAK_LOG));
    grids.add(CustomCoreRecipe.gridMaterials(Material.COBBLESTONE, Material.COBBLESTONE, Material.COBBLESTONE));
    grids.add(CustomCoreRecipe.gridMaterials(Material.PORKCHOP, Material.BLAZE_ROD, Material.PORKCHOP));
    roleSlotsByMaterial = CoreFabricatorSlots.computeRoleSlots(grids);
  }

  @Test
  void smartFillSaturatesHoneyAndCropAcrossTicks() {
    Sim sim = new Sim(roleSlotsByMaterial);
    for (int tick = 0; tick < 30; tick++) {
      if (tick % 2 == 0) {
        smartFillDelivery(sim, HONEY, HONEY_STACK);
      } else {
        smartFillDelivery(sim, POTATO, OTHER_STACK);
      }
    }
    assertEquals(HONEY_REQUIRED, sim.total(HONEY));
    assertEquals(3 * OTHER_STACK, sim.total(POTATO));
    assertTrue(sim.hasEnough(HONEY, HONEY_REQUIRED));
    assertTrue(sim.hasEnough(POTATO, POTATO_REQUIRED));
  }

  @Test
  void smartFillWithoutRoleFilterSticksAtOneHoneyStack() {
    Sim sim = new Sim(null);
    for (int tick = 0; tick < 10; tick++) {
      insert(sim, ALL_INPUT_SLOTS, HONEY, HONEY_STACK, true);
    }
    assertEquals(HONEY_STACK, sim.total(HONEY));
    assertFalse(sim.hasEnough(HONEY, HONEY_REQUIRED));
  }

  @Test
  void normalModeWithoutRoleFilterStarvesHoneyWhenPotatoArrivesFirst() {
    Sim sim = new Sim(null);
    for (int i = 0; i < 4; i++) {
      insert(sim, ALL_INPUT_SLOTS, POTATO, OTHER_STACK, false);
    }
    for (int i = 0; i < 6; i++) {
      insert(sim, ALL_INPUT_SLOTS, HONEY, HONEY_STACK, false);
    }
    assertFalse(sim.hasEnough(HONEY, HONEY_REQUIRED));
  }

  @Test
  void normalCargoWithRoleFilterKeepsEachMaterialInItsOwnRoles() {
    Sim sim = new Sim(roleSlotsByMaterial);
    for (int i = 0; i < 4; i++) {
      insert(sim, machineSlots(sim, POTATO), POTATO, OTHER_STACK, false);
    }
    for (int i = 0; i < 6; i++) {
      insert(sim, machineSlots(sim, HONEY), HONEY, HONEY_STACK, false);
    }
    assertEquals(3 * OTHER_STACK, sim.total(POTATO));
    assertEquals(HONEY_REQUIRED, sim.total(HONEY));
    assertTrue(sim.hasEnough(HONEY, HONEY_REQUIRED));
    assertTrue(sim.hasEnough(POTATO, POTATO_REQUIRED));
  }

  @Test
  void smartFillKeepsDeliveryStableOnceTheRecipeIsComplete() {
    Sim sim = new Sim(roleSlotsByMaterial);
    for (int i = 0; i < 20; i++) {
      smartFillDelivery(sim, HONEY, HONEY_STACK);
      smartFillDelivery(sim, POTATO, OTHER_STACK);
    }
    assertEquals(HONEY_REQUIRED, sim.total(HONEY));
    assertEquals(3 * OTHER_STACK, sim.total(POTATO));
    int honeyBefore = sim.total(HONEY);
    int potatoBefore = sim.total(POTATO);
    for (int i = 0; i < 10; i++) {
      smartFillDelivery(sim, HONEY, HONEY_STACK);
      smartFillDelivery(sim, POTATO, OTHER_STACK);
    }
    assertEquals(honeyBefore, sim.total(HONEY));
    assertEquals(potatoBefore, sim.total(POTATO));
  }

  @Test
  void machineCanRunDifferentRecipesWithoutClogging() {
    Sim sim = new Sim(roleSlotsByMaterial);
    runSmartFill(sim, HONEY, HONEY_STACK, HONEY_REQUIRED / HONEY_STACK);
    runSmartFill(sim, POTATO, OTHER_STACK, 3);
    assertEquals(HONEY_REQUIRED, sim.total(HONEY));
    assertEquals(3 * OTHER_STACK, sim.total(POTATO));

    sim.empty();
    runSmartFill(sim, Material.COBBLESTONE, OTHER_STACK, 9);
    assertEquals(9 * OTHER_STACK, sim.total(Material.COBBLESTONE));
    assertTrue(sim.hasEnough(Material.COBBLESTONE, 9 * CustomCoreRecipe.slotAmount(OTHER_STACK, 16)));

    sim.empty();
    runSmartFill(sim, Material.PORKCHOP, OTHER_STACK, 6);
    runSmartFill(sim, Material.BLAZE_ROD, OTHER_STACK, 3);
    assertEquals(6 * OTHER_STACK, sim.total(Material.PORKCHOP));
    assertEquals(3 * OTHER_STACK, sim.total(Material.BLAZE_ROD));
  }

  @Test
  void deliveriesOfAnotherRecipeNeverOverwriteExistingContent() {
    Sim sim = new Sim(roleSlotsByMaterial);
    runSmartFill(sim, HONEY, HONEY_STACK, 3);
    runSmartFill(sim, POTATO, OTHER_STACK, 1);
    int honeyBefore = sim.total(HONEY);
    int potatoBefore = sim.total(POTATO);
    for (int i = 0; i < 10; i++) {
      smartFillDelivery(sim, Material.OAK_LOG, OTHER_STACK);
      smartFillDelivery(sim, Material.OAK_LEAVES, OTHER_STACK);
    }
    assertEquals(honeyBefore, sim.total(HONEY));
    assertEquals(potatoBefore, sim.total(POTATO));
  }

  @Test
  void materialsWithoutRecipeGetNoSlotsAndCannotClogTheMachine() {
    Sim sim = new Sim(roleSlotsByMaterial);
    for (int i = 0; i < 10; i++) {
      assertEquals(0, machineSlots(sim, Material.DIRT).length);
      insert(sim, machineSlots(sim, Material.DIRT), Material.DIRT, 64, false);
    }
    assertEquals(0, sim.total(Material.DIRT));
    runSmartFill(sim, HONEY, HONEY_STACK, 6);
    assertEquals(HONEY_REQUIRED, sim.total(HONEY));
  }

  private static void runSmartFill(Sim sim, Material material, int stack, int deliveries) {
    for (int i = 0; i < deliveries; i++) {
      smartFillDelivery(sim, material, stack);
    }
  }

  private static void smartFillDelivery(Sim sim, Material incoming, int amount) {
    insert(sim, machineSlots(sim, incoming), incoming, amount, true);
  }

  /** Las ranuras que ofrece la maquina al cargo: solo las del rol, sin llenas ni ajenas. */
  private static int[] machineSlots(Sim sim, Material incoming) {
    int[] role = sim.roles.get(incoming);
    if (role == null) {
      return new int[0];
    }
    int[] amounts = new int[role.length];
    int[] maxStacks = new int[role.length];
    for (int i = 0; i < role.length; i++) {
      int slot = role[i];
      int current = sim.state[slot];
      if (current == 0) {
        amounts[i] = 0;
      } else if (sim.material[slot] == incoming) {
        amounts[i] = current;
      } else {
        amounts[i] = -1;
      }
      maxStacks[i] = maxStack(incoming);
    }
    return CoreFabricatorSlots.eligibleSlots(role, amounts, maxStacks);
  }

  /** Reproduce CargoUtils.insert: devuelve lo que no se ha podido depositar. */
  private static int insert(Sim sim, int[] slots, Material incoming, int amount, boolean smartFill) {
    int max = maxStack(incoming);
    for (int slot : slots) {
      int current = sim.state[slot];
      if (current == 0) {
        sim.state[slot] = amount;
        sim.material[slot] = incoming;
        return 0;
      }
      if (sim.material[slot] != incoming) {
        continue;
      }
      if (current >= max) {
        if (smartFill) {
          return amount;
        }
        continue;
      }
      int deposited = Math.min(amount, max - current);
      sim.state[slot] = current + deposited;
      return amount - deposited;
    }
    return amount;
  }

  private static int maxStack(Material material) {
    return material == HONEY ? HONEY_STACK : OTHER_STACK;
  }

  private static final class Sim {
    private final Map<Material, int[]> roles;
    private final int[] state = new int[54];
    private final Material[] material = new Material[54];

    private Sim(Map<Material, int[]> roles) {
      this.roles = roles;
    }

    private int total(Material wanted) {
      int sum = 0;
      for (int i = 0; i < state.length; i++) {
        if (material[i] == wanted) {
          sum += state[i];
        }
      }
      return sum;
    }

    private boolean hasEnough(Material wanted, int required) {
      return total(wanted) >= required;
    }

    private void empty() {
      for (int i = 0; i < state.length; i++) {
        state[i] = 0;
        material[i] = null;
      }
    }
  }
}