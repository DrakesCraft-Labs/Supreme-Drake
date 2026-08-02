package com.github.relativobr.supreme.machine.multiblock;

import com.github.relativobr.supreme.Supreme;
import com.github.relativobr.supreme.util.ItemGroups;
import com.github.relativobr.supreme.util.SupremeItemStack;
import com.github.drakescraft_labs.slimefun4.api.items.SlimefunItem;
import com.github.drakescraft_labs.slimefun4.api.items.SlimefunItemStack;
import com.github.drakescraft_labs.slimefun4.api.recipes.RecipeType;
import com.github.drakescraft_labs.slimefun4.core.attributes.NotPlaceable;
import com.github.drakescraft_labs.slimefun4.core.multiblocks.MultiBlockMachine;
import com.github.drakescraft_labs.slimefun4.implementation.Slimefun;
import com.github.drakescraft_labs.slimefun4.utils.SlimefunUtils;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MultiBlockCoreFabricator extends MultiBlockMachine implements NotPlaceable {

  public static final SlimefunItemStack CORE_FABRICATOR = new SupremeItemStack("SUPREME_MULTIBLOCK_CORE",
      Material.SHROOMLIGHT, "&eCore Fabricator", "", "&7&oYou can craft core here!", "", "&aMultiBlock Machine");
  public static final RecipeType MACHINE_CORE_FABRICATOR = new RecipeType(
      new NamespacedKey(Supreme.inst(), "SUPREME_MULTIBLOCK_CORE_KEY"), CORE_FABRICATOR);

  @ParametersAreNonnullByDefault
  public MultiBlockCoreFabricator() {
    super(ItemGroups.MACHINES_CATEGORY, CORE_FABRICATOR,
        new ItemStack[]{new ItemStack(Material.SHROOMLIGHT), new ItemStack(Material.ORANGE_STAINED_GLASS),
            new ItemStack(Material.SHROOMLIGHT), new ItemStack(Material.IRON_BARS),
            new ItemStack(Material.IRON_TRAPDOOR), new ItemStack(Material.IRON_BARS),
            new ItemStack(Material.GOLD_BLOCK), new ItemStack(Material.DISPENSER), new ItemStack(Material.GOLD_BLOCK)},
        new ItemStack[0], BlockFace.SELF);
  }

  public static RecipeType getMachine() {
    return MACHINE_CORE_FABRICATOR;
  }

  @Override
  public void onInteract(Player p, Block b) {

    Block dispenser = b.getRelative(BlockFace.DOWN);
    if (!dispenser.isEmpty()) {

      Inventory inv = ((Dispenser) dispenser.getState()).getInventory();
      List<ItemStack[]> inputs = RecipeType.getRecipeInputList(this);

      recipe:
      for (ItemStack[] input : inputs) {
        if (!matchesRecipe(inv, input)) continue;

        ItemStack output = RecipeType.getRecipeOutputList(this, input);
        SlimefunItem outputItem = SlimefunItem.getByItem(output);

        if (outputItem == null || outputItem.canUse(p, true)) {

          Inventory outputInv = findOutputInventory(output, dispenser, inv);
          if (!canFitOutput(outputInv == null ? inv : outputInv, output, input, outputInv == null)) {
            Slimefun.getLocalization().sendMessage(p, "machines.full-inventory", true);
            return;
          }

          consumeRecipe(inv, input);

          Bukkit.getScheduler().runTaskLater(Supreme.inst(),
              () -> p.getWorld().playSound(dispenser.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1F, 1F), 55L);
          for (int i = 1; i < 7; i++) {
            Bukkit.getScheduler().runTaskLater(Supreme.inst(),
                () -> p.getWorld().playSound(dispenser.getLocation(), Sound.BLOCK_METAL_PLACE, 7F, 1F), i * 5L);
          }

          if (outputInv != null) {
            outputInv.addItem(output);
          } else {
            inv.addItem(output);
          }
        }

        return;
      }
    }

    Slimefun.getLocalization().sendMessage(p, "machines.pattern-not-found", true);
  }

  /** Validates the full dispenser layout and all required stack amounts before consuming anything. */
  private boolean matchesRecipe(Inventory inventory, ItemStack[] recipe) {
    for (int slot = 0; slot < inventory.getSize(); slot++) {
      ItemStack expected = slot < recipe.length ? recipe[slot] : null;
      ItemStack actual = inventory.getItem(slot);
      if (expected == null || expected.getType().isAir()) {
        if (actual != null && !actual.getType().isAir()) return false;
        continue;
      }
      if (actual == null || actual.getAmount() < expected.getAmount()
          || !SlimefunUtils.isItemSimilar(actual, expected, false, true)) return false;
    }
    return true;
  }

  /** Simulates the destination after consumption so no crafted output is lost or duplicated. */
  private boolean canFitOutput(Inventory destination, ItemStack output, ItemStack[] input, boolean consumeInput) {
    Inventory simulation = Bukkit.createInventory(null, destination.getSize());
    for (int slot = 0; slot < destination.getSize(); slot++) {
      ItemStack item = destination.getItem(slot);
      simulation.setItem(slot, item == null ? null : item.clone());
    }
    if (consumeInput) consumeRecipe(simulation, input);
    return simulation.addItem(output.clone()).isEmpty();
  }

  private void consumeRecipe(Inventory inventory, ItemStack[] recipe) {
    for (int slot = 0; slot < recipe.length && slot < inventory.getSize(); slot++) {
      ItemStack expected = recipe[slot];
      if (expected == null || expected.getType().isAir()) continue;
      ItemStack current = inventory.getItem(slot);
      int remaining = current.getAmount() - expected.getAmount();
      if (remaining <= 0) inventory.setItem(slot, null);
      else current.setAmount(remaining);
    }
  }
}
