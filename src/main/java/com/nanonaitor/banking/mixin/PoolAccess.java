package com.nanonaitor.banking.mixin;
import java.util.List;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.level.levelgen.structure.pools.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
@Mixin(StructureTemplatePool.class)
public interface PoolAccess {
    @Accessor("rawTemplates") List<Pair<StructurePoolElement,Integer>> banking$getRaw();
    @Mutable @Accessor("rawTemplates") void banking$setRaw(List<Pair<StructurePoolElement,Integer>> list);
    @Accessor("templates") ObjectArrayList<StructurePoolElement> banking$getTemplates();
    @Accessor("maxSize") void banking$setMaxSize(int value);
}
