package com.nanonaitor.banking;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NBTTagCompound;
public final class BankerEntity extends EntityVillager {
 public BlockPos booth;
 public BankerEntity(World w){super(w);enablePersistence();}
 @Override public boolean processInteract(EntityPlayer p,EnumHand hand){if(isChild())return super.processInteract(p,hand);if(!world.isRemote&&hand==EnumHand.MAIN_HAND)p.openGui(BankingVillagers.instance,0,world,getEntityId(),0,0);return true;}
 @Override public void writeEntityToNBT(NBTTagCompound t){super.writeEntityToNBT(t);if(booth!=null)t.setLong("BankBooth",booth.toLong());}
 @Override public void readEntityFromNBT(NBTTagCompound t){super.readEntityFromNBT(t);if(t.hasKey("BankBooth"))booth=BlockPos.fromLong(t.getLong("BankBooth"));}
}
