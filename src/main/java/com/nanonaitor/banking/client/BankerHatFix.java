package com.nanonaitor.banking.client;

import com.nanonaitor.banking.BankingVillagers;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.npc.Villager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;
import java.util.IdentityHashMap;
import java.util.Map;

/** The vanilla hat brim reuses clothing UVs. Suppress only that geometry for bankers. */
@Mod.EventBusSubscriber(modid=BankingVillagers.ID,value=Dist.CLIENT)
public final class BankerHatFix {
    private static final Map<ModelPart,Boolean> previous=new IdentityHashMap<>();
    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void before(RenderLivingEvent.Pre<?,?> event){
        if(event.getEntity() instanceof Villager v&&BankingVillagers.isBanker(v)
                &&event.getRenderer().getModel() instanceof VillagerModel<?> model){
            ModelPart brim=model.getHead().getChild("hat").getChild("hat_rim");
            previous.put(brim,brim.skipDraw);brim.skipDraw=true;
        }
    }
    @SubscribeEvent
    public static void after(RenderLivingEvent.Post<?,?> event){
        if(event.getRenderer().getModel() instanceof VillagerModel<?> model){
            ModelPart brim=model.getHead().getChild("hat").getChild("hat_rim");
            Boolean value=previous.remove(brim);if(value!=null)brim.skipDraw=value;
        }
    }
}
