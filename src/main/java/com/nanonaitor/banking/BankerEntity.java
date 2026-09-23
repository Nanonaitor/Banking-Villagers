package com.nanonaitor.banking;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;

public final class BankerEntity extends Villager {
    public BankerEntity(EntityType<? extends Villager> type,Level level){
        super(type,level);
        setVillagerData(getVillagerData().setProfession(BankingVillagers.PROFESSION.get()));
        // Vanilla keeps experienced villagers in their profession. This avoids
        // fighting ResetProfession every tick when an egg banker has no booth.
        setVillagerXp(1);
        setPersistenceRequired();
    }
    @Override public void tick(){
        // Also repair legacy egg bankers saved with zero experience.
        if(!level().isClientSide&&getVillagerXp()==0)setVillagerXp(1);
        super.tick();
    }
}
