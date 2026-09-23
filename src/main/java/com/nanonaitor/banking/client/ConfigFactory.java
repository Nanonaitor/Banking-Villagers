package com.nanonaitor.banking.client;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.common.config.ConfigManager;
import com.nanonaitor.banking.BankingVillagers;
public final class ConfigFactory implements IModGuiFactory {
 public void initialize(Minecraft m){} public boolean hasConfigGui(){return true;}
 public GuiScreen createConfigGui(GuiScreen parent){return new GuiConfig(parent,BankingVillagers.ID,"Banking Villagers");}
 public Set<RuntimeOptionCategoryElement> runtimeGuiCategories(){return null;}
}
