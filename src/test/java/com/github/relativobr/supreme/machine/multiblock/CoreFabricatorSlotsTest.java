package com.github.relativobr.supreme.machine.multiblock;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.relativobr.supreme.generic.recipe.CustomCoreRecipe;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CoreFabricatorSlotsTest {

  private static final Material HONEY = Material.HONEY_BOTTLE;

  /** Replica las 57 recetas del fabricador para todas las categorias de core. */
  private static Map<Material, int[]> allRoleSlots;

  @BeforeAll
  static void buildAllRecipes() {
    List<Material[]> grids = new ArrayList<>();
    // Block: un solo material por receta.
    for (Material m : new Material[]{Material.COBBLESTONE, Material.GRANITE, Material.DIORITE,
        Material.ANDESITE, Material.GRAVEL, Material.SAND, Material.END_STONE, Material.CLAY,
        Material.SNOW_BLOCK}) {
      grids.add(single(m));
    }
    // Color: mezclas de hueco y de dos tintes.
    grids.add(dual(Material.RED_DYE, Material.BROWN_DYE));
    grids.add(dual(Material.YELLOW_DYE, Material.ORANGE_DYE));
    grids.add(single(Material.PURPLE_DYE));
    grids.add(dual(Material.BLUE_DYE, Material.LIGHT_BLUE_DYE));
    grids.add(single(Material.BLACK_DYE));
    grids.add(dual(Material.GREEN_DYE, Material.LIME_DYE));
    grids.add(dual(Material.PINK_DYE, Material.MAGENTA_DYE));
    grids.add(dual(Material.GRAY_DYE, Material.LIGHT_GRAY_DYE));
    grids.add(dual(Material.CYAN_DYE, Material.WHITE_DYE));
    // Death: mob drop + catalizador.
    grids.add(dual(Material.PORKCHOP, Material.BLAZE_ROD));
    grids.add(dual(Material.BEEF, Material.BLAZE_ROD));
    grids.add(dual(Material.MUTTON, Material.BLAZE_ROD));
    grids.add(dual(Material.CHICKEN, Material.BLAZE_ROD));
    grids.add(dual(Material.SALMON, Material.NETHER_STAR));
    grids.add(dual(Material.COD, Material.NETHER_STAR));
    grids.add(dual(Material.STRING, Material.ROTTEN_FLESH));
    grids.add(dual(Material.SPIDER_EYE, Material.BONE));
    grids.add(dual(Material.GHAST_TEAR, Material.SLIME_BALL));
    // Life: miel arriba y abajo, cosecha en el medio.
    for (Material crop : new Material[]{Material.POTATO, Material.APPLE, Material.BEETROOT,
        Material.WHEAT, Material.SUGAR_CANE, Material.SWEET_BERRIES, Material.MELON,
        Material.CARROT, Material.PUMPKIN}) {
      grids.add(dual(HONEY, crop));
    }
    // Nature: tronco como principal, hojas como secundario.
    grids.add(dual(Material.OAK_LOG, Material.OAK_LEAVES));
    grids.add(dual(Material.SPRUCE_LOG, Material.SPRUCE_LEAVES));
    grids.add(dual(Material.BIRCH_LOG, Material.BIRCH_LEAVES));
    grids.add(dual(Material.JUNGLE_LOG, Material.JUNGLE_LEAVES));
    grids.add(dual(Material.ACACIA_LOG, Material.ACACIA_LEAVES));
    grids.add(dual(Material.DARK_OAK_LOG, Material.DARK_OAK_LEAVES));
    grids.add(single(Material.CRIMSON_STEM));
    grids.add(single(Material.WARPED_STEM));
    grids.add(single(Material.WITHER_ROSE));
    // Alloy: lingotes y bloques sin mezcla.
    for (Material m : new Material[]{Material.COAL, Material.IRON_INGOT, Material.GOLD_INGOT,
        Material.REDSTONE, Material.LAPIS_LAZULI, Material.QUARTZ_BLOCK, Material.DIAMOND,
        Material.EMERALD, Material.NETHERITE_INGOT}) {
      grids.add(single(m));
    }
    allRoleSlots = CoreFabricatorSlots.computeRoleSlots(grids);
  }

  private static Material[] single(Material m) {
    return CustomCoreRecipe.gridMaterials(m, m, m);
  }

  private static Material[] dual(Material main, Material second) {
    return CustomCoreRecipe.gridMaterials(main, second, main);
  }

  @Test
  void singleMaterialRecipesFillAllNineInputSlots() {
    assertArrayEquals(new int[]{10, 11, 12, 19, 20, 21, 28, 29, 30},
        allRoleSlots.get(Material.COBBLESTONE));
    assertArrayEquals(new int[]{10, 11, 12, 19, 20, 21, 28, 29, 30},
        allRoleSlots.get(Material.NETHERITE_INGOT));
    assertArrayEquals(new int[]{10, 11, 12, 19, 20, 21, 28, 29, 30},
        allRoleSlots.get(Material.PURPLE_DYE));
  }

  @Test
  void twoMaterialRecipesLeaveSixSlotsForTheMainMaterial() {
    assertArrayEquals(new int[]{10, 11, 12, 28, 29, 30},
        allRoleSlots.get(Material.OAK_LOG));
    assertArrayEquals(new int[]{10, 11, 12, 28, 29, 30},
        allRoleSlots.get(Material.PORKCHOP));
    assertArrayEquals(new int[]{10, 11, 12, 28, 29, 30},
        allRoleSlots.get(HONEY));
  }

  @Test
  void twoMaterialRecipesLeaveThreeMiddleSlotsForTheSecondMaterial() {
    assertArrayEquals(new int[]{19, 20, 21}, allRoleSlots.get(Material.OAK_LEAVES));
    assertArrayEquals(new int[]{19, 20, 21}, allRoleSlots.get(Material.BLAZE_ROD));
    assertArrayEquals(new int[]{19, 20, 21}, allRoleSlots.get(Material.POTATO));
  }

  @Test
  void everyKnownMaterialGetsSlotsEvenAcrossManyRecipes() {
    assertFalse(allRoleSlots.containsKey(Material.DIRT));
    assertTrue(allRoleSlots.containsKey(Material.GHAST_TEAR));
    assertTrue(allRoleSlots.containsKey(Material.LIGHT_GRAY_DYE));
    assertTrue(allRoleSlots.containsKey(Material.NETHERITE_INGOT));
  }

  @Test
  void repeatedRecipesDoNotDuplicateSlots() {
    List<Material[]> grids = List.of(dual(HONEY, Material.POTATO), dual(HONEY, Material.POTATO));
    Map<Material, int[]> roles = CoreFabricatorSlots.computeRoleSlots(grids);
    assertArrayEquals(new int[]{10, 11, 12, 28, 29, 30}, roles.get(HONEY));
    assertArrayEquals(new int[]{19, 20, 21}, roles.get(Material.POTATO));
  }

  @Test
  void eligibleSlotsSkipsFullSlotsSoSmartFillCanAdvance() {
    assertArrayEquals(
        new int[]{11},
        CoreFabricatorSlots.eligibleSlots(
            allRoleSlots.get(HONEY),
            new int[]{16, 0, 16, 16, 16, 16},
            new int[]{16, 16, 16, 16, 16, 16}));
  }

  @Test
  void eligibleSlotsSkipsSlotsOwnedByAnotherMaterial() {
    assertArrayEquals(
        new int[]{20},
        CoreFabricatorSlots.eligibleSlots(
            allRoleSlots.get(Material.POTATO),
            new int[]{-1, 8, -1},
            new int[]{64, 64, 64}));
  }

  @Test
  void eligibleSlotsPrefersPartialSlotsBeforeEmptyAndSortsByAmount() {
    assertArrayEquals(
        new int[]{30, 12, 28, 10, 11, 29},
        CoreFabricatorSlots.eligibleSlots(
            allRoleSlots.get(HONEY),
            new int[]{8, 0, 4, 6, 0, 2},
            new int[]{16, 16, 16, 16, 16, 16}));
  }

  @Test
  void eligibleSlotsKeepsRoleOrderWhenEverythingIsEmpty() {
    assertArrayEquals(
        allRoleSlots.get(Material.POTATO),
        CoreFabricatorSlots.eligibleSlots(
            allRoleSlots.get(Material.POTATO),
            new int[]{0, 0, 0},
            new int[]{64, 64, 64}));
  }

  @Test
  void eligibleSlotsReturnsNothingWithoutRoleSlots() {
    assertEquals(0, CoreFabricatorSlots.eligibleSlots(new int[0], new int[0], new int[0]).length);
    assertEquals(0, CoreFabricatorSlots.eligibleSlots(null, new int[0], new int[0]).length);
  }

  @Test
  void eligibleSlotsReturnsNothingWhenEverySlotIsFull() {
    assertEquals(0, CoreFabricatorSlots.eligibleSlots(
        allRoleSlots.get(HONEY),
        new int[]{16, 16, 16, 16, 16, 16},
        new int[]{16, 16, 16, 16, 16, 16}).length);
  }

  @Test
  void eligibleSlotsTreatsAmountAtMaxStackAsFull() {
    assertArrayEquals(
        new int[]{19, 21},
        CoreFabricatorSlots.eligibleSlots(
            allRoleSlots.get(Material.POTATO),
            new int[]{0, 64, 0},
            new int[]{64, 64, 64}));
  }

  @Test
  void noMaterialMapsToNullEntry() {
    assertNull(CoreFabricatorSlots.computeRoleSlots(List.of()).get(Material.DIRT));
  }
}