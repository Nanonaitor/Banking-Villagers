package com.nanonaitor.banking;
import java.util.*;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraft.nbt.*;
import net.minecraft.item.ItemStack;
public final class BankData extends WorldSavedData {
 private final Map<UUID,Account> accounts=new HashMap<>();
 public BankData(){super("banking_villagers_accounts");} public BankData(String n){super(n);}
 public static BankData get(World w){World root=w.getMinecraftServer().getWorld(0);BankData d=(BankData)root.getMapStorage().getOrLoadData(BankData.class,"banking_villagers_accounts");if(d==null){d=new BankData();root.getMapStorage().setData("banking_villagers_accounts",d);}return d;}
 public Account account(UUID id){return accounts.computeIfAbsent(id,k->new Account(this));}
 @Override public void readFromNBT(NBTTagCompound tag){accounts.clear();NBTTagList list=tag.getTagList("Accounts",10);for(int i=0;i<list.tagCount();i++){NBTTagCompound t=list.getCompoundTagAt(i);Account a=new Account(this);a.space=t.getBoolean("Space");a.stack=t.getBoolean("Stack");NBTTagList items=t.getTagList("Items",10);for(int j=0;j<items.tagCount();j++){NBTTagCompound e=items.getCompoundTagAt(j);ItemStack s=new ItemStack(e.getCompoundTag("Item"));int slot=e.getInteger("Slot"),n=e.getInteger("Amount");if(!s.isEmpty()&&slot>=0&&slot<16384&&n>0)a.items.put(slot,new Entry(s,n));}accounts.put(t.getUniqueId("Player"),a);}}
 @Override public NBTTagCompound writeToNBT(NBTTagCompound tag){NBTTagList list=new NBTTagList();accounts.forEach((id,a)->{NBTTagCompound t=new NBTTagCompound();t.setUniqueId("Player",id);t.setBoolean("Space",a.space);t.setBoolean("Stack",a.stack);NBTTagList items=new NBTTagList();a.items.forEach((slot,e)->{NBTTagCompound v=new NBTTagCompound();v.setInteger("Slot",slot);v.setInteger("Amount",e.count);v.setTag("Item",e.item.writeToNBT(new NBTTagCompound()));items.appendTag(v);});t.setTag("Items",items);list.appendTag(t);});tag.setTag("Accounts",list);return tag;}
 public static final class Entry {public final ItemStack item;public int count;Entry(ItemStack s,int n){item=s.copy();item.setCount(1);count=n;}}
 public static final class Account {
  public boolean space,stack;private final BankData owner;public final Map<Integer,Entry> items=new HashMap<>();Account(BankData d){owner=d;}
  public int capacity(){return Math.min(16384,BankConfig.startingSlots*(space?BankConfig.spaceMultiplier:1));}
  public int extent(){return Math.max(capacity(),items.keySet().stream().mapToInt(i->i+1).max().orElse(0));}
  public void dirty(){owner.markDirty();}
  public int deposit(int slot,ItemStack s,int amount){if(slot>=capacity()||s.isEmpty())return 0;Entry e=items.get(slot);if(e!=null&&(!ItemStack.areItemsEqual(e.item,s)||!ItemStack.areItemStackTagsEqual(e.item,s)))return 0;int limit=Math.min(4096,s.getMaxStackSize()*(stack?BankConfig.stackMultiplier:1));int n=Math.min(Math.min(amount,s.getCount()),limit-(e==null?0:e.count));if(n<=0)return 0;if(e==null)items.put(slot,new Entry(s,n));else e.count+=n;s.shrink(n);dirty();return n;}
  public ItemStack take(int slot,int amount){Entry e=items.get(slot);if(e==null)return ItemStack.EMPTY;int n=Math.min(Math.min(amount,e.count),e.item.getMaxStackSize());ItemStack result=e.item.copy();result.setCount(n);e.count-=n;if(e.count==0)items.remove(slot);dirty();return result;}
 }
}
