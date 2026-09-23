package com.nanonaitor.banking.client;
import com.nanonaitor.banking.BankingVillagers;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
@Mod.EventBusSubscriber(modid=BankingVillagers.ID,bus=Mod.EventBusSubscriber.Bus.MOD,value=Dist.CLIENT)
public final class ClientSetup {
    @SubscribeEvent public static void config(FMLClientSetupEvent e){net.minecraftforge.fml.ModLoadingContext.get().registerExtensionPoint(net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory.class,()->new net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory((mc,parent)->new BankConfigScreen(parent)));}
    @SubscribeEvent public static void setup(FMLClientSetupEvent e){e.enqueueWork(()->{MenuScreens.register(BankingVillagers.BANK_MENU.get(),BankScreen::new);MenuScreens.register(BankingVillagers.SHOP_MENU.get(),BoothScreen::new);});}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers e){e.registerEntityRenderer(BankingVillagers.BANKER.get(),BankerRenderer::new);}
    @SubscribeEvent public static void layers(EntityRenderersEvent.AddLayers e){VillagerRenderer r=e.getRenderer(EntityType.VILLAGER);if(r!=null)r.addLayer(new BankerClothes<>(r));}
}
