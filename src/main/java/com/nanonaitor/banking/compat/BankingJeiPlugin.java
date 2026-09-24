package com.nanonaitor.banking.compat;
import com.nanonaitor.banking.BankingVillagers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.item.ItemStack;
/** Certificates are internal GUI art, not purchasable inventory items. */
@JEIPlugin
public final class BankingJeiPlugin implements IModPlugin {
 @Override public void register(IModRegistry registry){
  registry.getJeiHelpers().getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(BankingVillagers.SPACE));
  registry.getJeiHelpers().getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(BankingVillagers.STACK));
 }
}
