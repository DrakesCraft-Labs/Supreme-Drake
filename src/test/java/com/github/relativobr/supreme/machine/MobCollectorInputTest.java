package com.github.relativobr.supreme.machine;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.bukkit.Material;
import org.junit.jupiter.api.Test;

class MobCollectorInputTest {

  @Test
  void acceptsVanillaToolWithExpectedMaterial() {
    assertTrue(MobCollectorInputMatcher.matches(
        Material.SHEARS, Material.SHEARS, false));
  }

  @Test
  void rejectsSlimefunToolEvenWhenMaterialMatches() {
    assertFalse(MobCollectorInputMatcher.matches(
        Material.SHEARS, Material.SHEARS, true));
  }

  @Test
  void rejectsDifferentVanillaMaterial() {
    assertFalse(MobCollectorInputMatcher.matches(
        Material.IRON_SWORD, Material.SHEARS, false));
  }
}
