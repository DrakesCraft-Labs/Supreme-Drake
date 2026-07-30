package com.github.relativobr.supreme.generic.electric;

import com.github.relativobr.supreme.util.UtilEnergy;
import com.github.drakescraft_labs.slimefun4.api.items.ItemGroup;
import com.github.drakescraft_labs.slimefun4.api.items.SlimefunItemStack;
import com.github.drakescraft_labs.slimefun4.api.recipes.RecipeType;
import com.github.drakescraft_labs.slimefun4.core.attributes.EnergyNetProvider;
import com.github.drakescraft_labs.slimefun4.core.networks.energy.EnergyNetComponentType;
import javax.annotation.Nonnull;

import com.github.drakescraft_labs.slimefun4.libraries.dough.items.CustomItemStack;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import com.github.drakescraft_labs.slimefun4.legacy.api.BlockStorage;
import com.github.drakescraft_labs.slimefun4.legacy.api.inventory.BlockMenu;
import com.github.drakescraft_labs.slimefun4.legacy.api.inventory.BlockMenuPreset;
import com.github.drakescraft_labs.slimefun4.legacy.api.inventory.DirtyChestMenu;
import dev.drake.infinitylib.machines.MenuBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class EnergyGenerator extends MenuBlock implements EnergyNetProvider {

  private int energy;
  private int buffer;
  private GenerationType type;
  private final Map<Block, Integer> generationByBlock = new ConcurrentHashMap<>();


  public EnergyGenerator(ItemGroup categories, SlimefunItemStack item, ItemStack[] recipe) {
    super(categories, item, RecipeType.ENHANCED_CRAFTING_TABLE, recipe);
  }

  public GenerationType getType() {
    return type;
  }

  public EnergyGenerator setType(GenerationType value) {
    this.type = value;
    return this;
  }

  public EnergyGenerator setEnergy(int value) {
    this.energy = value;
    return this;
  }

  public EnergyGenerator setBuffer(int value) {
    this.buffer = value;
    return this;
  }

  @Nonnull
  @Override
  public EnergyNetComponentType getEnergyComponentType() {
    return EnergyNetComponentType.GENERATOR;
  }

  @Override
  protected void setup(BlockMenuPreset blockMenuPreset) {
    blockMenuPreset.drawBackground(new int[] {
            0, 1, 2, 3, 4, 5, 6, 7, 8,
            9, 10, 11, 12, 13, 14, 15, 16, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26
    });
  }

  @Nonnull
  @Override
  protected int[] getInputSlots(DirtyChestMenu dirtyChestMenu, ItemStack itemStack) {
    return new int[0];
  }

  @Override
  protected int[] getInputSlots() {
    return new int[0];
  }

  @Override
  protected int[] getOutputSlots() {
    return new int[0];
  }

  @Override
  public int getGeneratedOutput(Location l, Config data) {

    // Generator instances are shared by every placed block of the same item.
    // Keep the result per block so one invalid generator cannot inherit another's output.
    Block block = l.getBlock();
    int generated = generationByBlock.compute(block,
        (ignored, previous) -> type == null ? 0 : type.generate(l.getWorld(), block, this.energy));


    BlockMenu inv = BlockStorage.getInventory(l);
    if (inv != null && inv.hasViewer()) {
      if (generated == 0) {
        inv.replaceExistingItem(13, new CustomItemStack(
                Material.RED_STAINED_GLASS_PANE,
                "&cNot generating",
                "&7Type: &6" + this.type,
                "&7Stored: &6" + UtilEnergy.format(getCharge(l)) + " J",
                "&7Capacity: &6" + UtilEnergy.format(this.buffer) + " J"
        ));
      } else {
        inv.replaceExistingItem(13, new CustomItemStack(
                Material.GREEN_STAINED_GLASS_PANE,
                "&aGeneration",
                "&7Type: &6" + this.type,
                "&7Generating: &6" + UtilEnergy.format(generated) + " J/tick ",
                "&7Stored: &6" + UtilEnergy.format(getCharge(l)) + " J",
                "&7Capacity: &6" + UtilEnergy.format(this.buffer) + " J"
        ));
      }
    }

    return generated;
  }

  @Override
  protected void onBreak(BlockBreakEvent event, BlockMenu menu) {
    generationByBlock.remove(event.getBlock());
    super.onBreak(event, menu);
  }

  @Override
  public int getCapacity() {
    return this.buffer;
  }


}
