package com.nanonaitor.banking.client;
import com.nanonaitor.banking.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
@Mod.EventBusSubscriber(modid=BankingVillagers.ID,value=Side.CLIENT)
public final class ClientSmoke {
 private static boolean done;
 @SubscribeEvent public static void tick(TickEvent.ClientTickEvent e){if(done||e.phase!=TickEvent.Phase.END||!Boolean.getBoolean("banking.clientSmokeTest"))return;Minecraft m=Minecraft.getMinecraft();if(m.currentScreen==null)return;done=true;
  if(m.getRenderItem().getItemModelMesher().getItemModel(new ItemStack(BankingVillagers.BOOTH))==m.getRenderItem().getItemModelMesher().getModelManager().getMissingModel())throw new AssertionError("Booth model missing");
  if(!net.minecraftforge.fml.common.registry.ForgeRegistries.RECIPES.containsKey(new ResourceLocation(BankingVillagers.ID,"bank_booth")))throw new AssertionError("Booth recipe missing");
  if(m.getRenderManager().getEntityClassRenderObject(BankerEntity.class)==null)throw new AssertionError("Banker renderer missing");
  System.out.println("BANKING 1.12 CLIENT SMOKE PASS: booth model, recipe, banker renderer");m.shutdown();
 }
}
