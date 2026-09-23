package com.nanonaitor.banking;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import java.util.UUID;
public final class StorageTest {
 private static int checks;private static void check(boolean b){checks++;if(!b)throw new AssertionError("Check "+checks);}
 public static void main(String[] args){Bootstrap.register();BankData d=new BankData();UUID id=UUID.randomUUID();BankData.Account a=d.account(id);check(a.capacity()==162);a.stack=true;ItemStack x=new ItemStack(Items.DIAMOND,64);check(a.deposit(0,x,64)==64&&x.isEmpty());a.deposit(0,new ItemStack(Items.DIAMOND,64),64);check(a.items.get(0).count==128);BankData loaded=new BankData();loaded.readFromNBT(d.writeToNBT(new NBTTagCompound()));a=loaded.account(id);check(a.items.get(0).count==128);check(a.take(0,128).getCount()==64);check(loaded.account(UUID.randomUUID()).items.isEmpty());a.deposit(161,new ItemStack(Items.EMERALD,3),3);BankConfig.startingSlots=54;check(a.extent()==162);check(a.deposit(161,new ItemStack(Items.EMERALD),1)==0);check(a.take(161,64).getCount()==3&&a.extent()==54);a.space=true;check(a.capacity()==108);BankConfig.startingSlots=162;System.out.println("Passed "+checks+" bank storage checks");}
}
