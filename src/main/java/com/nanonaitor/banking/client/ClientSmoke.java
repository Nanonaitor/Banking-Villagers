package com.nanonaitor.banking.client;
import com.nanonaitor.banking.BankingVillagers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
@Mod.EventBusSubscriber(modid=BankingVillagers.ID,value=Dist.CLIENT)
public final class ClientSmoke {
    private static boolean done;
    @SubscribeEvent public static void tick(TickEvent.ClientTickEvent e){
        if(done||e.phase!=TickEvent.Phase.END||!Boolean.getBoolean("banking.clientSmokeTest"))return;
        Minecraft mc=Minecraft.getInstance();if(mc.getOverlay()!=null||mc.screen==null)return;
        var manager=mc.getModelManager();int count=0;
        for(var item:BankingVillagers.ITEMS.getEntries()){
            var model=manager.getModel(new ModelResourceLocation(item.getId(),"inventory"));
            if(model==manager.getMissingModel())throw new IllegalStateException("Missing Banking Villagers item model: "+item.getId());
            count++;
        }
        done=true;com.mojang.logging.LogUtils.getLogger().info("BANKING CLIENT SMOKE PASS: {} item models baked; renderer registration completed",count);mc.stop();
    }
}
