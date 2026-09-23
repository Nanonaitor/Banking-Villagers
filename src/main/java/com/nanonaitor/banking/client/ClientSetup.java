package com.nanonaitor.banking.client;
import com.nanonaitor.banking.*;
import net.minecraft.client.model.ModelVillager;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
@Mod.EventBusSubscriber(modid=BankingVillagers.ID,value=Side.CLIENT)
public final class ClientSetup {
 @SubscribeEvent public static void models(ModelRegistryEvent e){for(Item i:new Item[]{Item.getItemFromBlock(BankingVillagers.BOOTH),BankingVillagers.SPACE,BankingVillagers.STACK})ModelLoader.setCustomModelResourceLocation(i,0,new ModelResourceLocation(i.getRegistryName(),"inventory"));}
 static final class Renderer extends RenderVillager {
  Renderer(RenderManager m){super(m);addLayer(new LayerRenderer<EntityVillager>(){
   public void doRenderLayer(EntityVillager v,float a,float b,float partial,float age,float yaw,float pitch,float scale){bindTexture(new ResourceLocation(BankingVillagers.ID,"textures/entity/villager/profession/banker.png"));GlStateManager.color(1,1,1,1);getMainModel().render(v,a,b,age,yaw,pitch,scale);}
   public boolean shouldCombineTextures(){return false;}
  });}
  @Override protected ResourceLocation getEntityTexture(EntityVillager v){return new ResourceLocation("minecraft","textures/entity/villager/villager.png");}
 }
}
