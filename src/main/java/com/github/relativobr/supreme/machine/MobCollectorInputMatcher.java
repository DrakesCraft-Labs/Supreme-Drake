package com.github.relativobr.supreme.machine;

import com.github.drakescraft_labs.slimefun4.api.items.SlimefunItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

final class MobCollectorInputMatcher {

  private MobCollectorInputMatcher() {
  }

  /**
   * Matches only vanilla collector tools. Slimefun tools such as Multi Tools share the same
   * base material, but have their own charge and behavior and must not be used as substitutes.
   */
  static boolean matches(ItemStack actual, ItemStack expected) {
    return matches(
        actual == null ? null : actual.getType(),
        expected == null ? null : expected.getType(),
        actual != null && SlimefunItem.getByItem(actual) != null);
  }

  /** Package-visible seam used by the regression test without bootstrapping Bukkit or Slimefun. */
  static boolean matches(Material actual, Material expected, boolean slimefunItem) {
    return actual != null && expected != null && actual == expected && !slimefunItem;
  }
}
