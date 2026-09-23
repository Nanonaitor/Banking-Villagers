package com.nanonaitor.banking.client;
import com.nanonaitor.banking.*;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;
public final class BankerRenderer extends MobRenderer<BankerEntity,VillagerModel<BankerEntity>> {
    public BankerRenderer(EntityRendererProvider.Context context){super(context,new VillagerModel<>(context.bakeLayer(ModelLayers.VILLAGER)),.5F);addLayer(new BankerClothes<>(this));}
    @Override public ResourceLocation getTextureLocation(BankerEntity e){return new ResourceLocation("minecraft","textures/entity/villager/villager.png");}
}
