package com.nanonaitor.banking.client;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.Villager;
/** Texture on vanilla geometry, without accessory cubes. */
public final class BankerClothes<T extends Villager> extends RenderLayer<T,VillagerModel<T>> {
    public static final ResourceLocation TEXTURE=new ResourceLocation("banking_villagers","textures/entity/villager/profession/banker.png");
    public BankerClothes(RenderLayerParent<T,VillagerModel<T>> parent){super(parent);}
    @Override public void render(PoseStack pose,MultiBufferSource buffers,int light,T entity,float a,float b,float partial,float age,float yaw,float pitch){
        if(!(entity instanceof com.nanonaitor.banking.BankerEntity)||entity.isInvisible())return;
        getParentModel().renderToBuffer(pose,buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)),light,OverlayTexture.NO_OVERLAY,1,1,1,1);
    }
}
