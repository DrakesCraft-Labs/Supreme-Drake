package com.github.relativobr.supreme.generic.recipe;

import com.github.drakescraft_labs.slimefun4.api.items.SlimefunItemStack;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.github.relativobr.supreme.Supreme;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Data
@AllArgsConstructor
@NoArgsConstructor
public final class CustomCoreRecipe {

  private SlimefunItemStack material;
  private String name;

  //index 0,1,2
  private Material mainItem;
  //index 3,4,5
  private Material secondItem;
  //index 6,7,8
  private Material lastItem;


  public CustomCoreRecipe(SlimefunItemStack material, Material mainItem) {
    this.material = material;
    this.name = material.getItemId();
    this.mainItem = mainItem;
    this.secondItem = mainItem;
    this.lastItem = mainItem;
  }

  public CustomCoreRecipe(SlimefunItemStack material, Material mainItem, Material secondItem) {
    this.material = material;
    this.name = material.getItemId();
    this.mainItem = mainItem;
    this.secondItem = secondItem;
    this.lastItem = mainItem;
  }

  public CustomCoreRecipe(SlimefunItemStack material, Material mainItem, Material secondItem,
      Material lastItem) {
    this.material = material;
    this.name = material.getItemId();
    this.mainItem = mainItem;
    this.secondItem = secondItem;
    this.lastItem = lastItem;
  }

  /**
   * Cuanto material pide un hueco del fabricador.
   *
   * Antes cada hueco pedia una pila llena, con lo que el precio de un core lo acababa decidiendo
   * el tamaño de pila del material en vanilla en vez de lo que cuesta conseguirlo: el core de
   * netherita salia por 576 lingotes -- nueve huecos de 64 -- y el de patata por 96 botellas de
   * miel, no porque la miel valga menos sino porque apila de 16.
   *
   * Ahora los nueve huecos piden lo mismo, y se recorta al tamaño de pila del material para que
   * la cantidad siempre quepa en el hueco: un material que no apile pedira uno por hueco.
   */
  private static int cantidadPorHueco(Material material) {
    return Math.min(Supreme.getSupremeOptions().getCoreAmountPerSlot(), material.getMaxStackSize());
  }

  /** Un hueco de la receta: el material y lo que pide. */
  public static ItemStack hueco(Material material) {
    return new ItemStack(material, cantidadPorHueco(material));
  }

  public static ItemStack[] getRecipe(CustomCoreRecipe customCoreRecipe) {
    final ItemStack principal = hueco(customCoreRecipe.getMainItem());
    final ItemStack segundo = hueco(customCoreRecipe.getSecondItem());
    final ItemStack ultimo = hueco(customCoreRecipe.getLastItem());
    return new ItemStack[]{
        principal, principal, principal,
        segundo, segundo, segundo,
        ultimo, ultimo, ultimo
    };
  }


  public static SlimefunItemStack getOutput(CustomCoreRecipe customCoreRecipe) {
    SlimefunItemStack output = (SlimefunItemStack) customCoreRecipe.getMaterial().clone();
    output.setAmount(1);
    return output;
  }

}
