package com.github.relativobr.supreme.generic.recipe;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.bukkit.Material;
import org.junit.jupiter.api.Test;

class CustomCoreRecipeTest {

  @Test
  void gridForTwoMaterialRecipeRepeatsEdgesWithMainAndMiddleWithSecond() {
    assertArrayEquals(new Material[]{
            Material.HONEY_BOTTLE, Material.HONEY_BOTTLE, Material.HONEY_BOTTLE,
            Material.POTATO, Material.POTATO, Material.POTATO,
            Material.HONEY_BOTTLE, Material.HONEY_BOTTLE, Material.HONEY_BOTTLE
        },
        CustomCoreRecipe.gridMaterials(
            Material.HONEY_BOTTLE, Material.POTATO, Material.HONEY_BOTTLE));
  }

  @Test
  void gridForThreeMaterialRecipeHasOneRowPerMaterial() {
    assertArrayEquals(new Material[]{
            Material.STONE, Material.STONE, Material.STONE,
            Material.IRON_INGOT, Material.IRON_INGOT, Material.IRON_INGOT,
            Material.EMERALD, Material.EMERALD, Material.EMERALD
        },
        CustomCoreRecipe.gridMaterials(Material.STONE, Material.IRON_INGOT, Material.EMERALD));
  }

  @Test
  void slotAmountAsksForConfiguredAmount() {
    assertEquals(16, CustomCoreRecipe.slotAmount(64, 16));
    assertEquals(32, CustomCoreRecipe.slotAmount(64, 32));
  }

  @Test
  void slotAmountNeverOverflowsTheStack() {
    assertEquals(16, CustomCoreRecipe.slotAmount(16, 64));
    assertEquals(64, CustomCoreRecipe.slotAmount(64, 64));
    assertEquals(1, CustomCoreRecipe.slotAmount(1, 64));
  }

  @Test
  void gridForOneMaterialRecipeFillsEveryRowWithTheSameMaterial() {
    assertArrayEquals(new Material[]{
            Material.COBBLESTONE, Material.COBBLESTONE, Material.COBBLESTONE,
            Material.COBBLESTONE, Material.COBBLESTONE, Material.COBBLESTONE,
            Material.COBBLESTONE, Material.COBBLESTONE, Material.COBBLESTONE
        },
        CustomCoreRecipe.gridMaterials(Material.COBBLESTONE, Material.COBBLESTONE, Material.COBBLESTONE));
  }

  @Test
  void gridAlwaysHasNineEntries() {
    assertEquals(9,
        CustomCoreRecipe.gridMaterials(Material.STONE, Material.IRON_INGOT, Material.EMERALD).length);
    assertEquals(9,
        CustomCoreRecipe.gridMaterials(Material.HONEY_BOTTLE, Material.POTATO, Material.HONEY_BOTTLE).length);
    assertEquals(9,
        CustomCoreRecipe.gridMaterials(Material.COBBLESTONE, Material.COBBLESTONE, Material.COBBLESTONE).length);
  }

  @Test
  void everyLifeCoreRecipeHasSixHoneyAndThreeCrop() {
    for (Material crop : new Material[]{
        Material.POTATO, Material.APPLE, Material.BEETROOT, Material.WHEAT, Material.SUGAR_CANE,
        Material.SWEET_BERRIES, Material.MELON, Material.CARROT, Material.PUMPKIN}) {
      Material[] grid = CustomCoreRecipe.gridMaterials(Material.HONEY_BOTTLE, crop, Material.HONEY_BOTTLE);
      int honey = 0;
      int crops = 0;
      for (Material material : grid) {
        if (material == Material.HONEY_BOTTLE) {
          honey++;
        } else if (material == crop) {
          crops++;
        }
      }
      assertEquals(6, honey, crop.name());
      assertEquals(3, crops, crop.name());
    }
  }

  @Test
  void slotAmountRespectsConfiguredAmountAndStackSize() {
    assertEquals(16, CustomCoreRecipe.slotAmount(64, 16));
    assertEquals(16, CustomCoreRecipe.slotAmount(16, 16));
    assertEquals(64, CustomCoreRecipe.slotAmount(64, 64));
    assertEquals(16, CustomCoreRecipe.slotAmount(16, 64));
    assertEquals(1, CustomCoreRecipe.slotAmount(1, 64));
    assertEquals(0, CustomCoreRecipe.slotAmount(0, 16));
  }

  @Test
  void honeyBottleRecipeNeedsNinetySixAndPotatoFortyEightWithDefaultStack() {
    assertEquals(6 * 16, 6 * CustomCoreRecipe.slotAmount(16, 16));
    assertEquals(3 * 16, 3 * CustomCoreRecipe.slotAmount(64, 16));
  }

  @Test
  void potatoCoreNeedsNinetySixHoneyAndFortyEightPotato() {
    int honey = 6 * CustomCoreRecipe.slotAmount(16, 16);
    int crop = 3 * CustomCoreRecipe.slotAmount(64, 16);
    assertEquals(96, honey);
    assertEquals(48, crop);
  }
}