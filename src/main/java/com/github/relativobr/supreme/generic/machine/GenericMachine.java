package com.github.relativobr.supreme.generic.machine;

import static java.util.Objects.nonNull;

import com.github.relativobr.supreme.Supreme;
import com.github.relativobr.supreme.generic.recipe.AbstractItemRecipe;
import com.github.relativobr.supreme.generic.recipe.InventoryRecipe;
import com.github.drakescraft_labs.slimefun4.api.items.ItemGroup;
import com.github.drakescraft_labs.slimefun4.api.items.SlimefunItemStack;
import com.github.drakescraft_labs.slimefun4.api.recipes.RecipeType;
import com.github.drakescraft_labs.slimefun4.core.attributes.NotHopperable;
import com.github.drakescraft_labs.slimefun4.core.attributes.RecipeDisplayItem;
import com.github.drakescraft_labs.slimefun4.core.handlers.BlockBreakHandler;
import com.github.drakescraft_labs.slimefun4.implementation.Slimefun;
import com.github.drakescraft_labs.slimefun4.implementation.handlers.SimpleBlockBreakHandler;
import com.github.drakescraft_labs.slimefun4.libraries.dough.items.CustomItemStack;
import com.github.drakescraft_labs.slimefun4.libraries.dough.protection.Interaction;
import com.github.drakescraft_labs.slimefun4.utils.ChestMenuUtils;
import com.github.drakescraft_labs.slimefun4.utils.SlimefunUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import com.github.drakescraft_labs.slimefun4.legacy.Objects.SlimefunItem.abstractItems.AContainer;
import com.github.drakescraft_labs.slimefun4.legacy.Objects.SlimefunItem.abstractItems.MachineRecipe;
import com.github.drakescraft_labs.slimefun4.legacy.api.BlockStorage;
import com.github.drakescraft_labs.slimefun4.legacy.api.inventory.BlockMenu;
import com.github.drakescraft_labs.slimefun4.legacy.api.inventory.BlockMenuPreset;
import com.github.drakescraft_labs.slimefun4.legacy.api.inventory.DirtyChestMenu;
import com.github.drakescraft_labs.slimefun4.legacy.api.item_transport.ItemTransportFlow;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GenericMachine extends AContainer implements NotHopperable, RecipeDisplayItem {

  private final Map<Block, MachineRecipe> processing = new HashMap<>();
  private final Map<Block, Integer> progressTime = new HashMap<>();
  private final Map<Block, Map<ItemStack, Integer>> consumedItemsMap = new HashMap<>();
  private final Map<Block, Integer> attemptCount = new HashMap<>();
  public List<AbstractItemRecipe> machineRecipes = new ArrayList<>();
  private Integer timeProcess;
  private String machineIdentifier = "MediumContainerMachine";

  @ParametersAreNonnullByDefault
  public GenericMachine(ItemGroup category, SlimefunItemStack item, RecipeType recipeType,
      ItemStack[] recipe) {
    super(category, item, recipeType, recipe);

    addItemHandler(onBlockBreak());

    new BlockMenuPreset(getId(), getItemName()) {

      @Override
      public void init() {
        constructMenu(this);
      }

      @Override
      public boolean canOpen(Block b, Player p) {
        return p.hasPermission("slimefun.inventory.bypass") || Slimefun.getProtectionManager()
            .hasPermission(p, b.getLocation(), Interaction.INTERACT_BLOCK);
      }

      @Override
      public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
        if (flow == ItemTransportFlow.WITHDRAW) {
          return getOutputSlots();
        }
        return new int[0];
      }

      @Override
      public int[] getSlotsAccessedByItemTransport(DirtyChestMenu menu, ItemTransportFlow flow,
          ItemStack item) {
        if (flow == ItemTransportFlow.WITHDRAW) {
          return getOutputSlots();
        }

        return getInsertSlotsForItem(menu, item);
      }

    };
  }

  /**
   * Ranuras de entrada donde puede caer un item que llega por transporte automatico.
   *
   * Se devuelven tanto las que ya contienen el material y admiten mas como las libres. Antes solo
   * se devolvian las que ya lo contenian: en cuanto una se llenaba, la insercion automatica
   * quedaba rechazada aunque el resto de la entrada estuviera vacia, y la maquina no podia reunir
   * los materiales de la receta. A mano si funcionaba, porque el filtro solo aplica al transporte.
   *
   * Se completan primero los stacks empezados para no fragmentar la entrada.
   *
   * Las maquinas cuya receta no depende de la posicion pueden sobrescribir esto y aceptar
   * cualquier ranura; es lo que hace ElectricCoreFabricator, cuya rejilla 3x3 confundia a los
   * hoppers.
   */
  protected int[] getInsertSlotsForItem(DirtyChestMenu menu, ItemStack item) {
    List<Integer> withRoom = new LinkedList<>();
    List<Integer> empty = new LinkedList<>();

    for (int slot : getInputSlots()) {
      ItemStack stack = menu.getItemInSlot(slot);
      if (stack == null || stack.getType() == Material.AIR) {
        empty.add(slot);
      } else if (SlimefunUtils.isItemSimilar(stack, item, false, true)
          && stack.getAmount() < stack.getMaxStackSize()) {
        withRoom.add(slot);
      }
    }

    if (withRoom.isEmpty() && empty.isEmpty()) {
      return new int[0];
    }

    withRoom.sort(compareSlots(menu));
    List<Integer> destinations = new LinkedList<>(withRoom);
    destinations.addAll(empty);

    int[] array = new int[destinations.size()];
    for (int i = 0; i < destinations.size(); i++) {
      array[i] = destinations.get(i);
    }
    return array;
  }

  @Nonnull
  @Override
  protected BlockBreakHandler onBlockBreak() {
    return new SimpleBlockBreakHandler() {
      public void onBlockBreak(Block b) {
        BlockMenu inv = BlockStorage.getInventory(b);
        if (inv != null) {
          revertConsumedItem(b, inv);
          inv.dropItems(b.getLocation(), getInputSlots());
          inv.dropItems(b.getLocation(), getOutputSlots());
        }
        removeMapBlock(b);
        onMachineRemoved(b);
      }
    };
  }

  /**
   * Hook para que las subclases con mapas estaticos propios (VirtualGarden,
   * VirtualAquarium, MobCollector) liberen su estado al romper el bloque.
   * Sin esto sus mapas crecen indefinidamente y una maquina nueva en la misma
   * ubicacion hereda el progreso/receta del bloque anterior.
   */
  protected void onMachineRemoved(Block b) {
    // Por defecto no hace nada; la base ya limpia sus propios mapas en removeMapBlock.
  }

  protected void updateStatusReset(BlockMenu menu) {
    menu.replaceExistingItem(getStatusSlot(), getDisplayOrInfo(null, " "));
  }

  protected void updateStatusInvalidInput(BlockMenu menu) {
    menu.replaceExistingItem(getStatusSlot(),getDisplayOrWarn(null,"&cMete un material valido para empezar"));
  }

  /** Shows the missing amount when a player has started, but not completed, a recipe. */
  protected void updateStatusMissingMaterial(BlockMenu menu, MissingIngredient missing) {
    // El nombre venia del enum Material, o sea en ingles y en mayusculas ("PORKCHOP"). Si el
    // objeto trae nombre visible se usa ese, que es el que el jugador tiene delante.
    String material = missing.item().getType().name().toLowerCase(Locale.ROOT).replace('_', ' ');
    if (missing.item().hasItemMeta() && missing.item().getItemMeta().hasDisplayName()) {
      material = missing.item().getItemMeta().getDisplayName();
    }
    menu.replaceExistingItem(getStatusSlot(), getDisplayOrWarn(null,
        "&eFaltan " + missing.amount() + " de " + material));
  }

  protected void updateStatusOutputFull(BlockMenu menu) {
    menu.replaceExistingItem(getStatusSlot(), getDisplayOrWarn(null,"&cLa salida esta llena"));
  }

  protected void updateStatusConnectEnergy(BlockMenu menu, ItemStack itemStack) {
    menu.replaceExistingItem(getStatusSlot(), getDisplayOrWarn(itemStack, "&cConecta energia para continuar"));
  }

  protected void updateStatusLoadMaterial(BlockMenu menu, ItemStack itemStack, int attempts, int progressCount, int totalProgress) {
    var infoDetail = new CustomItemStack(itemStack,
        "&cFalta material para empezar", "",
        "&7Intentos: &e" + attempts + " &7/ &e" + Supreme.getSupremeOptions().getMachineMaxAttemptConsumed(),
        "&7Progreso: &e" + progressCount + " &7/ &e" + totalProgress, "");
    menu.replaceExistingItem(getStatusSlot(), infoDetail);
  }

  public GenericMachine setMachineRecipes(@Nonnull List<AbstractItemRecipe> machineRecipes) {
    this.machineRecipes = machineRecipes;
    return this;
  }

  public GenericMachine setTimeProcess(int timeProcess) {
    this.timeProcess = timeProcess;
    return this;
  }

  public int getTimeProcess() {
    if(timeProcess == null){
      timeProcess = 15;
    }
    return this.timeProcess;
  }

  @Nonnull
  private Comparator<Integer> compareSlots(@Nonnull DirtyChestMenu menu) {
    return Comparator.comparingInt(slot -> menu.getItemInSlot(slot).getAmount());
  }

  @Override
  protected void constructMenu(BlockMenuPreset preset) {
    for (int i : getBorderSlots()) {
      preset.addItem(i, new CustomItemStack(Material.GRAY_STAINED_GLASS_PANE, " "),
          ChestMenuUtils.getEmptyClickHandler());
    }

    for (int i : getInputBorderSlots()) {
      preset.addItem(i, new CustomItemStack(Material.CYAN_STAINED_GLASS_PANE, " "),
          ChestMenuUtils.getEmptyClickHandler());
    }

    for (int i : getOutputBorderSlots()) {
      preset.addItem(i, new CustomItemStack(Material.ORANGE_STAINED_GLASS_PANE, " "),
          ChestMenuUtils.getEmptyClickHandler());
    }

    preset.addItem(getStatusSlot(),
        new CustomItemStack(Material.BLACK_STAINED_GLASS_PANE, " "),
        ChestMenuUtils.getEmptyClickHandler());

    for (int i : getOutputSlots()) {
      preset.addMenuClickHandler(i, new ChestMenu.AdvancedMenuClickHandler() {

        @Override
        public boolean onClick(Player p, int slot, ItemStack cursor, ClickAction action) {
          return false;
        }

        @Override
        public boolean onClick(InventoryClickEvent e, Player p, int slot, ItemStack cursor,
            ClickAction action) {
          if (cursor == null) {
            return true;
          }
          return cursor.getType() == Material.AIR;
        }
      });
    }
  }

  @Override
  public ItemStack getProgressBar() {
    return new ItemStack(Material.PISTON);
  }

  @Override
  public int[] getInputSlots() {
    return InventoryRecipe.MEDIUM_INPUT;
  }

  @Override
  public int[] getOutputSlots() {
    return InventoryRecipe.MEDIUM_OUTPUT;
  }

  public int getStatusSlot(){
    return InventoryRecipe.MEDIUM_STATUS_SLOT;
  }

  public int[] getBorderSlots(){
    return InventoryRecipe.MEDIUM_BORDER;
  }

  public int[] getInputBorderSlots(){
    return InventoryRecipe.MEDIUM_INPUT_BORDER;
  }

  public int[] getOutputBorderSlots(){
    return InventoryRecipe.MEDIUM_OUTPUT_BORDER;
  }

  @Nonnull
  @Override
  public String getMachineIdentifier() {
    return nonNull(this.machineIdentifier) ? this.machineIdentifier : "MachineIdentifier";
  }

  public GenericMachine setMachineIdentifier(@Nonnull String machineIdentifier) {
    this.machineIdentifier = machineIdentifier;
    return this;
  }

  @Override
  protected void tick(Block b) {
    BlockMenu inv = BlockStorage.getInventory(b);
    if (inv == null) {
      return;
    }

    if (this.isProcessing(b)) {
      doProcessing(b, inv);
    } else {
      nextProcessing(b, inv);
    }
  }

  @Nonnull
  @Override
  public List<ItemStack> getDisplayRecipes() {
    List<ItemStack> displayRecipes = new ArrayList();
    machineRecipes.forEach(recipe -> {
      displayRecipes.add(new CustomItemStack(Material.GRAY_STAINED_GLASS_PANE, " "));
      displayRecipes.add(recipe.getFirstItemOutput());
    });
    return displayRecipes;
  }

  @Override
  protected MachineRecipe findNextRecipe(BlockMenu inv) {
    for (AbstractItemRecipe recipe : machineRecipes) {
      if (matchingRecipe(recipe.getInputNotNull(), inv)) {
        return new MachineRecipe(getTimeProcess(), recipe.getInputNotNull(), recipe.getOutputNotNull());
      }
    }
    AbstractItemRecipe parcial = findUnambiguousPartialRecipe(inv);
    if (parcial != null) {
      return new MachineRecipe(getTimeProcess(), parcial.getInputNotNull(), parcial.getOutputNotNull());
    }
    return null;
  }

  /**
   * Receta que se puede arrancar aunque todavia no este todo el material.
   *
   * Reporte de Macacrack334 (2026-09-19): el core de patata pide 96 botellas de miel (apilan de
   * 16) y 48 patatas, o sea 7 de las 9 ranuras repartidas de una forma concreta. El cargo llena
   * las ranuras en el orden en que llegan los items, asi que casi nunca coincide la reparticion y
   * la receta jamas "encajaba" entera: la maquina no arrancaba y la miel se quedaba atascada.
   *
   * Ahora, si en la entrada hay al menos una unidad de CADA ingrediente distinto de una receta y
   * esa receta es la unica que se puede deducir, la maquina arranca y va consumiendo lo que llega
   * (el ciclo de carga ya existia: intentos + progreso). Es ambiguo, y por tanto no arranca, cuando
   * otra receta candidata usa un conjunto mayor de ingredientes: con solo A cargado no se elige (A)
   * si tambien existe (A, B), porque el jugador puede estar cargando la segunda.
   */
  private AbstractItemRecipe findUnambiguousPartialRecipe(BlockMenu inv) {
    List<AbstractItemRecipe> candidatas = new ArrayList<>();
    List<List<RequiredIngredient>> ingredientes = new ArrayList<>();
    for (AbstractItemRecipe recipe : machineRecipes) {
      List<RequiredIngredient> requeridos = aggregateIngredients(recipe.getInputNotNull());
      if (requeridos.isEmpty()) {
        continue;
      }
      boolean todosPresentes = true;
      for (RequiredIngredient ingrediente : requeridos) {
        if (countAvailable(inv, ingrediente.item()) <= 0) {
          todosPresentes = false;
          break;
        }
      }
      if (todosPresentes) {
        candidatas.add(recipe);
        ingredientes.add(requeridos);
      }
    }
    if (candidatas.isEmpty()) {
      return null;
    }
    // Se queda la candidata cuyo conjunto de ingredientes no este contenido en el de otra.
    AbstractItemRecipe elegida = null;
    for (int i = 0; i < candidatas.size(); i++) {
      boolean contenida = false;
      for (int j = 0; j < candidatas.size(); j++) {
        if (i != j && ingredientes.get(j).size() > ingredientes.get(i).size()
            && contieneIngredientes(ingredientes.get(j), ingredientes.get(i))) {
          contenida = true;
          break;
        }
      }
      if (!contenida) {
        if (elegida != null) {
          return null; // dos recetas maximales: ambiguo, que el jugador complete una
        }
        elegida = candidatas.get(i);
      }
    }
    return elegida;
  }

  private boolean contieneIngredientes(List<RequiredIngredient> mayor, List<RequiredIngredient> menor) {
    for (RequiredIngredient a : menor) {
      boolean hallado = false;
      for (RequiredIngredient b : mayor) {
        if (SlimefunUtils.isItemSimilar(a.item(), b.item(), false, false)) {
          hallado = true;
          break;
        }
      }
      if (!hallado) {
        return false;
      }
    }
    return true;
  }

  protected int getProgressTime(Block b) {
    return progressTime.get(b) != null ? progressTime.get(b) : getTimeProcess();
  }

  protected MachineRecipe getProcessing(Block b) {
    return processing.get(b);
  }

  protected Map<ItemStack, Integer> getConsumedItems(Block b) {
    // Older persisted machine states can survive without this cache entry.
    // Recreate it instead of crashing on block break or partial processing.
    return consumedItemsMap.computeIfAbsent(b, ignored -> new HashMap<>());
  }

  protected boolean isProcessing(Block b) {
    return getProcessing(b) != null;
  }

  protected boolean notHasSpaceOutput(BlockMenu inv, ItemStack[] result) {
    List<ItemStack> simulatedSlots = new ArrayList<>(getOutputSlots().length);
    for (int slot : getOutputSlots()) {
      ItemStack itemInSlot = inv.getItemInSlot(slot);
      simulatedSlots.add(itemInSlot == null ? null : itemInSlot.clone());
    }

    for (ItemStack output : result) {
      if (output == null || output.getType().isAir() || output.getAmount() <= 0) continue;

      int remaining = output.getAmount();
      for (ItemStack existing : simulatedSlots) {
        if (existing == null || !SlimefunUtils.isItemSimilar(existing, output, false, false)) continue;

        int capacity = existing.getMaxStackSize() - existing.getAmount();
        if (capacity <= 0) continue;
        int inserted = Math.min(capacity, remaining);
        existing.setAmount(existing.getAmount() + inserted);
        remaining -= inserted;
        if (remaining == 0) break;
      }

      while (remaining > 0) {
        int emptySlot = simulatedSlots.indexOf(null);
        if (emptySlot < 0) return true;

        ItemStack inserted = output.clone();
        int insertedAmount = Math.min(inserted.getMaxStackSize(), remaining);
        inserted.setAmount(insertedAmount);
        simulatedSlots.set(emptySlot, inserted);
        remaining -= insertedAmount;
      }
    }

    return false;
  }

  private void nextProcessing(Block b, BlockMenu inv) {
    MachineRecipe next = this.findNextRecipe(inv);
    if (next != null) {
      processing.put(b, next);
      progressTime.put(b, next.getTicks());
      consumedItemsMap.put(b, new HashMap<>());
      attemptCount.put(b, 0);
    } else {
      MissingIngredient missing = findMissingIngredient(inv);
      if (missing != null) {
        updateStatusMissingMaterial(inv, missing);
      } else if (getInputSlots().length <= 5) {
        updateStatusReset(inv);
      } else {
        updateStatusInvalidInput(inv);
      }
    }
  }

  private void removeMapBlock(Block b) {
    progressTime.remove(b);
    processing.remove(b);
    attemptCount.remove(b);
    consumedItemsMap.remove(b);
  }

  private void doProcessing(Block b, BlockMenu inv) {
    var result = getProcessing(b).getOutput();

    if (result == null || result.length == 0) {
      removeMapBlock(b);
      updateStatusReset(inv);
      return;
    }

    if (getCharge(b.getLocation()) < getEnergyConsumption()) {
      updateStatusConnectEnergy(inv, null);
      return;
    }

    final int ticks = getProcessing(b).getTicks();
    int ticksRemaining = getProgressTime(b);
    if (ticks == ticksRemaining) {
      // Fase de carga: la energia solo se cobra cuando el material se consume de verdad.
      // Antes cada tick de espera cobraba, y con arranque parcial la espera puede durar.
      if (startProcessTicks(b, inv, ticksRemaining)) {
        removeCharge(b.getLocation(), getEnergyConsumption());
      }
      return;
    }
    removeCharge(b.getLocation(), getEnergyConsumption());
    if (ticksRemaining == 0) {
      endProcessTicks(b, inv, result);
    } else {
      doProcessTicks(b, inv, ticks, ticksRemaining, result[0]);
    }
  }

  /** @return true si en este tick se termino de consumir toda la receta. */
  private boolean startProcessTicks(Block b, BlockMenu inv, int ticksRemaining) {
    int antes = getConsumedItems(b).values().stream().mapToInt(Integer::intValue).sum();
    if (consumptionRecipe(b, inv)) {
      progressTime.put(b, Math.max(ticksRemaining - this.getSpeed(), 0));
      attemptCount.put(b, 0);
      consumedItemsMap.put(b, new HashMap<>());
      return true;
    }
    int progressCount = getConsumedItems(b).values().stream().mapToInt(Integer::intValue).sum();
    // Un intento solo cuenta si no entro nada: mientras el cargo siga trayendo material la
    // maquina espera. Antes 30 ticks seguidos sin completar la receta la abortaban aunque el
    // material estuviera llegando de a poco (miel de a 16).
    int attempts = progressCount > antes ? 0 : attemptCount.getOrDefault(b, 0) + 1;
    if (attempts >= Supreme.getSupremeOptions().getMachineMaxAttemptConsumed()) {
      revertConsumedItem(b, inv);
      removeMapBlock(b);
      updateStatusInvalidInput(inv);
    } else {
      attemptCount.put(b, attempts);
      var totalProgress = Arrays.stream(getProcessing(b).getInput()).mapToInt(ItemStack::getAmount).sum();
      updateStatusLoadMaterial(inv, getProcessing(b).getOutput()[0], attempts, progressCount, totalProgress);
    }
    return false;
  }

  private void revertConsumedItem(Block b, BlockMenu inv) {
    /*
     * Esto corre en el ticker ASINCRONO de Slimefun. dropItemNaturally crea una
     * entidad, que solo el hilo principal puede hacer: hacerlo aqui disparaba el
     * AsyncCatcher, mataba el bloque y --como el clear() quedaba sin ejecutar-- el
     * siguiente tick volvia a revertir los MISMOS items. Ese es el duplicado que
     * reporto Chagui.
     *
     * Ahora: 1) se toma una copia de lo consumido, 2) se limpia el estado de
     * inmediato para que nada se pueda re-revertir, 3) lo que cabe en la entrada
     * se devuelve aqui (pushItem sobre el BlockMenu es seguro), y 4) SOLO los
     * sobrantes se sueltan al suelo en el hilo principal.
     */
    Map<ItemStack, Integer> consumed = new HashMap<>(getConsumedItems(b));
    getConsumedItems(b).clear();

    List<ItemStack> aSoltar = new ArrayList<>();
    for (Map.Entry<ItemStack, Integer> consumedEntry : consumed.entrySet()) {
      ItemStack consumedItem = consumedEntry.getKey();
      int amount = consumedEntry.getValue();
      if (consumedItem != null && consumedItem.getType() != Material.AIR) {
        int maxStackSize = consumedItem.getMaxStackSize();
        while (amount > 0) {
          int stackSize = Math.min(maxStackSize, amount);
          ItemStack returnItem = consumedItem.clone();
          returnItem.setAmount(stackSize);
          ItemStack sobrante = inv.pushItem(returnItem, getInputSlots());
          if (sobrante != null && sobrante.getType() != Material.AIR && sobrante.getAmount() > 0) {
            aSoltar.add(sobrante);
          }
          amount -= stackSize;
        }
      }
    }

    if (!aSoltar.isEmpty()) {
      var loc = b.getLocation().add(0.5, 1, 0.5);
      org.bukkit.Bukkit.getScheduler().runTask(Supreme.inst(), () -> {
        for (ItemStack drop : aSoltar) {
          b.getWorld().dropItemNaturally(loc, drop);
        }
      });
    }
  }

  private void endProcessTicks(Block b, BlockMenu inv, ItemStack[] result) {
    if (notHasSpaceOutput(inv, result)) {
      updateStatusOutputFull(inv);
      return;
    }
    for (ItemStack itemStack : result) {
      inv.pushItem(itemStack.clone(), getOutputSlots());
    }
    removeMapBlock(b);
    updateStatusReset(inv);
  }

  private void doProcessTicks(Block b, BlockMenu inv, int ticks, int ticksRemaining, ItemStack result) {
    progressTime.put(b, Math.max(ticksRemaining - this.getSpeed(), 0));
    ChestMenuUtils.updateProgressbar(inv, getStatusSlot(), ticksRemaining, ticks, result);
  }

  private boolean consumptionRecipe(Block b, BlockMenu inv) {
    boolean consumeFailure = false;
    for (RequiredIngredient ingredient : aggregateIngredients(getProcessing(b).getInput())) {
      ItemStack requiredItem = ingredient.item();
      int requiredAmount = ingredient.amount();
      int foundAmount = 0;

      for (Map.Entry<ItemStack, Integer> consumedEntry : getConsumedItems(b).entrySet()) {
        if (SlimefunUtils.isItemSimilar(consumedEntry.getKey(), requiredItem, false, false)) {
          foundAmount += consumedEntry.getValue();
        }
      }

      for (int slot : this.getInputSlots()) {
        if (inv.getItemInSlot(slot) == null) {
          continue;
        }
        ItemStack inputItem = inv.getItemInSlot(slot).clone();
        if (SlimefunUtils.isItemSimilar(inputItem, requiredItem, false, false)) {
          int amountToConsume = Math.min(inputItem.getAmount(), requiredAmount - foundAmount);
          if (amountToConsume > 0) {
            inv.consumeItem(slot, amountToConsume);
            // La clave se normaliza a 1 porque ItemStack.equals compara el amount: usando el
            // stack tal cual, cada ciclo con un tamano distinto creaba una entrada nueva y el
            // mapa de consumidos se fragmentaba en vez de acumular.
            ItemStack key = inputItem.clone();
            key.setAmount(1);
            getConsumedItems(b).merge(key, amountToConsume, Integer::sum);
          }
          foundAmount += amountToConsume;
          if (foundAmount >= requiredAmount) {
            break;
          }
        }
      }
      if (foundAmount < requiredAmount) {
        consumeFailure = true;
        break;
      }
    }

    return !consumeFailure;
  }

  private boolean matchingRecipe(ItemStack[] recipe, BlockMenu inv) {
    for (RequiredIngredient ingredient : aggregateIngredients(recipe)) {
      int available = 0;
      for (int slot : getInputSlots()) {
        ItemStack itemInSlot = inv.getItemInSlot(slot);
        if (itemInSlot != null
            && SlimefunUtils.isItemSimilar(itemInSlot, ingredient.item(), false, false)) {
          available += itemInSlot.getAmount();
        }
      }
      if (available < ingredient.amount()) {
        return false;
      }
    }
    return true;
  }

  /**
   * Finds a recipe the player has partially supplied. This avoids reporting a
   * valid stack as invalid when the recipe requires several complete stacks.
   */
  private MissingIngredient findMissingIngredient(BlockMenu inv) {
    for (AbstractItemRecipe recipe : machineRecipes) {
      boolean matchedAnyIngredient = false;
      for (RequiredIngredient ingredient : aggregateIngredients(recipe.getInputNotNull())) {
        int available = countAvailable(inv, ingredient.item());
        if (available > 0) {
          matchedAnyIngredient = true;
        }
        if (available < ingredient.amount()) {
          if (matchedAnyIngredient) {
            return new MissingIngredient(ingredient.item(), ingredient.amount() - available);
          }
          break;
        }
      }
    }
    return null;
  }

  private int countAvailable(BlockMenu inv, ItemStack required) {
    int available = 0;
    for (int slot : getInputSlots()) {
      ItemStack item = inv.getItemInSlot(slot);
      if (item != null && SlimefunUtils.isItemSimilar(item, required, false, false)) {
        available += item.getAmount();
      }
    }
    return available;
  }

  /** Groups recipe requirements by Slimefun identity instead of mutable stack amount. */
  private List<RequiredIngredient> aggregateIngredients(ItemStack[] recipe) {
    List<RequiredIngredient> ingredients = new ArrayList<>();
    for (ItemStack item : recipe) {
      if (item == null || item.getType().isAir() || item.getAmount() <= 0) {
        continue;
      }
      int matchingIndex = -1;
      for (int i = 0; i < ingredients.size(); i++) {
        if (SlimefunUtils.isItemSimilar(ingredients.get(i).item(), item, false, false)) {
          matchingIndex = i;
          break;
        }
      }
      if (matchingIndex < 0) {
        ItemStack identity = item.clone();
        identity.setAmount(1);
        ingredients.add(new RequiredIngredient(identity, item.getAmount()));
      } else {
        RequiredIngredient current = ingredients.get(matchingIndex);
        ingredients.set(matchingIndex,
            new RequiredIngredient(current.item(), current.amount() + item.getAmount()));
      }
    }
    return ingredients;
  }

  private record RequiredIngredient(ItemStack item, int amount) {}

  protected record MissingIngredient(ItemStack item, int amount) {}

  private ItemStack getDisplayOrInfo(ItemStack itemStack, String name) {
    return new CustomItemStack(itemStack != null ? itemStack : new ItemStack(Material.BLACK_STAINED_GLASS_PANE), name);
  }

  private ItemStack getDisplayOrWarn(ItemStack itemStack, String name) {
    return new CustomItemStack(itemStack != null ? itemStack : new ItemStack(Material.RED_STAINED_GLASS_PANE), name);
  }
}
