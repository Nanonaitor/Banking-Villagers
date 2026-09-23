package com.nanonaitor.banking;
import java.util.ArrayList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.*;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import com.nanonaitor.banking.mixin.PoolAccess;
public final class VillageBanks {
    public static void inject(ServerAboutToStartEvent event){
        int weight=BankConfig.VILLAGE_WEIGHT.get();if(weight<=0)return;
        var pools=event.getServer().registryAccess().registryOrThrow(Registries.TEMPLATE_POOL);
        for(String biome:new String[]{"plains","desert","savanna","snowy","taiga"}){
            StructureTemplatePool pool=pools.get(new ResourceLocation("minecraft","village/"+biome+"/houses"));if(pool==null)continue;
            StructurePoolElement element=StructurePoolElement.single("banking_villagers:village/bank_stall").apply(StructureTemplatePool.Projection.RIGID);
            PoolAccess access=(PoolAccess)pool;var raw=new ArrayList<>(access.banking$getRaw());raw.add(Pair.of(element,weight));access.banking$setRaw(raw);
            for(int i=0;i<weight;i++)access.banking$getTemplates().add(element);access.banking$setMaxSize(-1);
        }
    }
}
