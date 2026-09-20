package com.github.relativobr.supreme.machine.multiblock;

import com.github.relativobr.supreme.generic.machine.GenericMachine;
import com.github.relativobr.supreme.generic.recipe.AbstractItemRecipe;
import com.github.relativobr.supreme.generic.recipe.CustomCoreRecipe;
import com.github.relativobr.supreme.resource.SupremeComponents;
import com.github.relativobr.supreme.resource.core.SupremeCoreAlloy;
import com.github.relativobr.supreme.resource.core.SupremeCoreBlock;
import com.github.relativobr.supreme.resource.core.SupremeCoreColor;
import com.github.relativobr.supreme.resource.core.SupremeCoreDeath;
import com.github.relativobr.supreme.resource.core.SupremeCoreLife;
import com.github.relativobr.supreme.resource.core.SupremeCoreNature;
import com.github.relativobr.supreme.resource.magical.SupremeCetrus;
import com.github.relativobr.supreme.util.SupremeItemStack;
import com.github.relativobr.supreme.util.UtilEnergy;
import com.github.drakescraft_labs.slimefun4.api.items.ItemGroup;
import com.github.drakescraft_labs.slimefun4.api.items.SlimefunItemStack;
import com.github.drakescraft_labs.slimefun4.api.recipes.RecipeType;
import com.github.drakescraft_labs.slimefun4.core.attributes.MachineTier;
import com.github.drakescraft_labs.slimefun4.core.attributes.MachineType;
import com.github.drakescraft_labs.slimefun4.implementation.SlimefunItems;
import com.github.drakescraft_labs.slimefun4.legacy.api.inventory.DirtyChestMenu;
import com.github.drakescraft_labs.slimefun4.utils.LoreBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ElectricCoreFabricator extends GenericMachine {

  /**
   * Cada material solo puede caer en los huecos de su rol dentro de la rejilla 3x3.
   *
   * matchingRecipe y consumptionRecipe siguen buscando en las nueve ranuras, de modo que da igual
   * la posicion al procesar. Pero el transporte automatico se guiaba por la primera ranura
   * disponible y completaba los huecos en orden secuencial, llenando los de un material (por
   * ejemplo las papas, que apilan de 64) sin dejar huecos para el otro (la miel, que apila de 16 y
   * necesita 6 huecos para reunir sus 96 botellas): la receta nunca se completaba por transporte.
   *
   * Ahora cada material solo llena los huecos de su rol, y ademas se descartan los huecos llenos o
   * ocupados por otro material para que el smart-fill del cargo, que abandona cuando la primera
   * ranura del mismo material esta llena, salte a la siguiente del rol en vez de quedarse atascado.
   */
  @Override
  protected int[] getInsertSlotsForItem(DirtyChestMenu menu, ItemStack item) {
    if (item == null) {
      return new int[0];
    }
    int[] roleSlots = INPUT_SLOTS_BY_MATERIAL.get(item.getType());
    if (roleSlots == null) {
      // Material de ninguna receta: el cargo no debe poder atascar la maquina con el.
      return new int[0];
    }
    int[] amounts = new int[roleSlots.length];
    int[] maxStacks = new int[roleSlots.length];
    for (int i = 0; i < roleSlots.length; i++) {
      ItemStack stack = menu.getItemInSlot(roleSlots[i]);
      if (stack == null || stack.getType() == Material.AIR) {
        amounts[i] = 0;
      } else if (stack.getType() == item.getType()) {
        amounts[i] = stack.getAmount();
      } else {
        amounts[i] = -1;
      }
      maxStacks[i] = item.getMaxStackSize();
    }
    return CoreFabricatorSlots.eligibleSlots(roleSlots, amounts, maxStacks);
  }

  public static final SlimefunItemStack ELECTRIC_CORE_MACHINE = new SupremeItemStack("SUPREME_ELECTRIC_CORE_I",
      Material.SHROOMLIGHT, "&bElectric Core Machine", "", "&fCraft resource of core", "",
      LoreBuilder.machine(MachineTier.ADVANCED, MachineType.MACHINE), LoreBuilder.speed(1),
      UtilEnergy.energyPowerPerSecond(20), "", "&3Supreme Machine");

  public static final ItemStack[] RECIPE_ELECTRIC_CORE_MACHINE = new ItemStack[]{SupremeComponents.RUSTLESS_MACHINE,
      SlimefunItems.PROGRAMMABLE_ANDROID_3, SupremeComponents.RUSTLESS_MACHINE, SupremeComponents.INDUCTIVE_MACHINE,
      SupremeComponents.SYNTHETIC_RUBY, SupremeComponents.INDUCTIVE_MACHINE, SlimefunItems.ELECTRIC_MOTOR,
      SlimefunItems.CARBONADO_EDGED_CAPACITOR, SlimefunItems.ELECTRIC_MOTOR};

  public static final SlimefunItemStack ELECTRIC_CORE_MACHINE_II = new SupremeItemStack("SUPREME_ELECTRIC_CORE_II",
      Material.SHROOMLIGHT, "&bElectric Core Machine II", "", "&fAdvanced craft resource of core", "",
      LoreBuilder.machine(MachineTier.END_GAME, MachineType.MACHINE), LoreBuilder.speed(5),
      UtilEnergy.energyPowerPerSecond(100), "", "&3Supreme Machine");

  public static final ItemStack[] RECIPE_ELECTRIC_CORE_MACHINE_II = new ItemStack[]{
      SupremeComponents.CONVEYANCE_MACHINE, SupremeCetrus.CETRUS_LUX, SupremeComponents.CONVEYANCE_MACHINE,
      SupremeComponents.INDUCTOR_MACHINE, ElectricCoreFabricator.ELECTRIC_CORE_MACHINE,
      SupremeComponents.INDUCTOR_MACHINE, SupremeComponents.BLEND_MACHINE, SupremeCetrus.CETRUS_IGNIS,
      SupremeComponents.BLEND_MACHINE};

  public static final SlimefunItemStack ELECTRIC_CORE_MACHINE_III = new SupremeItemStack("SUPREME_ELECTRIC_CORE_III",
      Material.SHROOMLIGHT, "&bElectric Core Machine III", "", "&fAdvanced craft resource of core", "",
      LoreBuilder.machine(MachineTier.END_GAME, MachineType.MACHINE), LoreBuilder.speed(15),
      UtilEnergy.energyPowerPerSecond(300), "", "&3Supreme Machine");

  public static final ItemStack[] RECIPE_ELECTRIC_CORE_MACHINE_III = new ItemStack[]{SupremeComponents.THORNERITE,
      SupremeCetrus.CETRUS_LUX, SupremeComponents.THORNERITE, SupremeComponents.SUPREME,
      ElectricCoreFabricator.ELECTRIC_CORE_MACHINE_II, SupremeComponents.SUPREME,
      SupremeComponents.CRYSTALLIZER_MACHINE, SupremeCetrus.CETRUS_LUMIUM, SupremeComponents.CRYSTALLIZER_MACHINE};

  @ParametersAreNonnullByDefault
  public ElectricCoreFabricator(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
    super(category, item, recipeType, recipe);
  }

  /** Recetas de todos los cores, en el orden en que se muestran en la guia. */
  private static final List<CustomCoreRecipe> ALL_CORE_RECIPES = List.of(
      SupremeCoreBlock.RECIPE_RESOURCE_CORE_STONE,
      SupremeCoreBlock.RECIPE_RESOURCE_CORE_GRANITE,
      SupremeCoreBlock.RECIPE_RESOURCE_CORE_DIORITE,
      SupremeCoreBlock.RECIPE_RESOURCE_CORE_ANDESITE,
      SupremeCoreBlock.RECIPE_RESOURCE_CORE_GRAVEL,
      SupremeCoreBlock.RECIPE_RESOURCE_CORE_SAND,
      SupremeCoreBlock.RECIPE_RESOURCE_CORE_ENDSTONE,
      SupremeCoreBlock.RECIPE_RESOURCE_CORE_CLAY,
      SupremeCoreBlock.RECIPE_RESOURCE_CORE_SNOW,
      SupremeCoreColor.RECIPE_RESOURCE_CORE_RED,
      SupremeCoreColor.RECIPE_RESOURCE_CORE_YELLOW,
      SupremeCoreColor.RECIPE_RESOURCE_CORE_PURPLE,
      SupremeCoreColor.RECIPE_RESOURCE_CORE_BLUE,
      SupremeCoreColor.RECIPE_RESOURCE_CORE_BLACK,
      SupremeCoreColor.RECIPE_RESOURCE_CORE_GREEN,
      SupremeCoreColor.RECIPE_RESOURCE_CORE_PINK,
      SupremeCoreColor.RECIPE_RESOURCE_CORE_GRAY,
      SupremeCoreColor.RECIPE_RESOURCE_CORE_CYAN,
      SupremeCoreDeath.RECIPE_RESOURCE_CORE_PORKCHOP,
      SupremeCoreDeath.RECIPE_RESOURCE_CORE_BEEF,
      SupremeCoreDeath.RECIPE_RESOURCE_CORE_MUTTON,
      SupremeCoreDeath.RECIPE_RESOURCE_CORE_CHICKEN,
      SupremeCoreDeath.RECIPE_RESOURCE_CORE_SALMON,
      SupremeCoreDeath.RECIPE_RESOURCE_CORE_COD,
      SupremeCoreDeath.RECIPE_RESOURCE_CORE_STRING,
      SupremeCoreDeath.RECIPE_RESOURCE_CORE_SPIDER_EYE,
      SupremeCoreDeath.RECIPE_RESOURCE_CORE_TEAR,
      SupremeCoreLife.RECIPE_RESOURCE_CORE_POTATO,
      SupremeCoreLife.RECIPE_RESOURCE_CORE_APPLE,
      SupremeCoreLife.RECIPE_RESOURCE_CORE_BEETROOT,
      SupremeCoreLife.RECIPE_RESOURCE_CORE_WHEAT,
      SupremeCoreLife.RECIPE_RESOURCE_CORE_SUGAR_CANE,
      SupremeCoreLife.RECIPE_RESOURCE_CORE_SWEET_BERRIES,
      SupremeCoreLife.RECIPE_RESOURCE_CORE_MELON,
      SupremeCoreLife.RECIPE_RESOURCE_CORE_CARROT,
      SupremeCoreLife.RECIPE_RESOURCE_CORE_PUMPKIN,
      SupremeCoreNature.RECIPE_RESOURCE_CORE_OAK_LOG,
      SupremeCoreNature.RECIPE_RESOURCE_CORE_SPRUCE_LOG,
      SupremeCoreNature.RECIPE_RESOURCE_CORE_BIRCH_LOG,
      SupremeCoreNature.RECIPE_RESOURCE_CORE_JUNGLE_LOG,
      SupremeCoreNature.RECIPE_RESOURCE_CORE_ACACIA_LOG,
      SupremeCoreNature.RECIPE_RESOURCE_CORE_DARK_OAK_LOG,
      SupremeCoreNature.RECIPE_RESOURCE_CORE_CRIMSON_STEM,
      SupremeCoreNature.RECIPE_RESOURCE_CORE_WARPED_STEM,
      SupremeCoreNature.RECIPE_RESOURCE_CORE_WITHER_ROSE,
      SupremeCoreAlloy.RECIPE_RESOURCE_CORE_COAL,
      SupremeCoreAlloy.RECIPE_RESOURCE_CORE_IRON,
      SupremeCoreAlloy.RECIPE_RESOURCE_CORE_GOLD,
      SupremeCoreAlloy.RECIPE_RESOURCE_CORE_LAPIS,
      SupremeCoreAlloy.RECIPE_RESOURCE_CORE_REDSTONE,
      SupremeCoreAlloy.RECIPE_RESOURCE_CORE_QUARTZ,
      SupremeCoreAlloy.RECIPE_RESOURCE_CORE_DIAMOND,
      SupremeCoreAlloy.RECIPE_RESOURCE_CORE_EMERALD,
      SupremeCoreAlloy.RECIPE_RESOURCE_CORE_NETHERITE);

  /** Huecos de cada material en la rejilla 3x3, calculados una sola vez. */
  private static final Map<Material, int[]> INPUT_SLOTS_BY_MATERIAL = buildInputSlotsByMaterial();

  private static Map<Material, int[]> buildInputSlotsByMaterial() {
    List<Material[]> grids = new ArrayList<>(ALL_CORE_RECIPES.size());
    for (CustomCoreRecipe recipe : ALL_CORE_RECIPES) {
      grids.add(CustomCoreRecipe.gridMaterials(recipe));
    }
    return CoreFabricatorSlots.computeRoleSlots(grids);
  }

  public static List<AbstractItemRecipe> getAllRecipe() {
    List<AbstractItemRecipe> list = new ArrayList<>(ALL_CORE_RECIPES.size());
    for (CustomCoreRecipe recipe : ALL_CORE_RECIPES) {
      list.add(addRecipe(recipe));
    }
    return list;
  }

  private static AbstractItemRecipe addRecipe(CustomCoreRecipe customCoreRecipe) {
    // Delega en CustomCoreRecipe: antes esta clase repetia el mismo calculo, y con dos copias
    // cualquier cambio de precio se aplicaba en un sitio y no en el otro.
    return new AbstractItemRecipe(CustomCoreRecipe.getRecipe(customCoreRecipe),
        customCoreRecipe.getMaterial());
  }

}
