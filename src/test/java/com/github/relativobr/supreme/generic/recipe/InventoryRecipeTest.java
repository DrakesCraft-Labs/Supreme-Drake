package com.github.relativobr.supreme.generic.recipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class InventoryRecipeTest {

  @Test
  void mediumInputHasNineSlots() {
    assertEquals(9, InventoryRecipe.MEDIUM_INPUT.length);
  }

  @Test
  void mediumInputHasNoDuplicates() {
    Integer[] ints = new Integer[InventoryRecipe.MEDIUM_INPUT.length];
    for (int i = 0; i < InventoryRecipe.MEDIUM_INPUT.length; i++) {
      ints[i] = InventoryRecipe.MEDIUM_INPUT[i];
    }
    assertEquals(new HashSet<>(java.util.Arrays.asList(ints)).size(), ints.length);
  }

  @Test
  void mediumInputStaysInsideTheCraftingGrid() {
    for (int slot : InventoryRecipe.MEDIUM_INPUT) {
      assertTrue(slot >= 9 && slot < 45, String.valueOf(slot));
    }
  }

  @Test
  void mediumInputAndOutputDoNotOverlap() {
    Set<Integer> output = new HashSet<>();
    for (int slot : InventoryRecipe.MEDIUM_OUTPUT) {
      output.add(slot);
    }
    for (int slot : InventoryRecipe.MEDIUM_INPUT) {
      assertTrue(!output.contains(slot), "slot " + slot);
    }
  }

  @Test
  void borderSlotsDoNotIncludeInputs() {
    Set<Integer> border = new HashSet<>();
    for (int slot : InventoryRecipe.MEDIUM_BORDER) {
      border.add(slot);
    }
    for (int slot : InventoryRecipe.MEDIUM_INPUT) {
      assertTrue(!border.contains(slot), "slot " + slot);
    }
  }
}