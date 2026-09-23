package com.nanonaitor.banking;

import java.util.*;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;

/** Stores counts as integers, never ItemStack's signed-byte Count NBT. */
public final class BankData extends SavedData {
    private final Map<UUID,Account> accounts=new HashMap<>();
    public static BankData get(ServerLevel level) {return level.getServer().overworld().getDataStorage().computeIfAbsent(BankData::load,BankData::new,"banking_villagers_accounts");}
    public Account account(UUID id) {return accounts.computeIfAbsent(id,k->{setDirty();return new Account(this::setDirty);});}
    public static BankData load(CompoundTag root) {
        BankData data=new BankData();
        for(Tag tag:root.getList("Accounts",Tag.TAG_COMPOUND)) {
            CompoundTag t=(CompoundTag)tag;
            if(t.hasUUID("Owner"))data.accounts.put(t.getUUID("Owner"),Account.load(t,data::setDirty));
        }
        return data;
    }
    @Override public CompoundTag save(CompoundTag root) {
        ListTag list=new ListTag(); accounts.forEach((id,a)->{CompoundTag t=a.save();t.putUUID("Owner",id);list.add(t);});
        root.put("Accounts",list);root.putInt("DataVersion",1);return root;
    }
    public static final class Entry {
        public final ItemStack item; public int count;
        Entry(ItemStack item,int count){this.item=item.copyWithCount(1);this.count=count;}
    }
    public static final class Account {
        private final NavigableMap<Integer,Entry> items=new TreeMap<>();
        private final Runnable dirty;
        public int spaceUpgrades,stackUpgrades;
        public Account(Runnable dirty){this.dirty=dirty;}
        public int capacity(){return BankConfig.capacity(spaceUpgrades);}
        public int extent(){return Math.max(capacity(),items.isEmpty()?0:items.lastKey()+1);}
        public Entry entry(int slot){return items.get(slot);}
        public int count(int slot){Entry e=entry(slot);return e==null?0:e.count;}
        public boolean isOverflow(int slot){return slot>=capacity();}
        public int limit(ItemStack stack){return BankConfig.stackLimit(stack.getMaxStackSize(),stackUpgrades);}
        public int deposit(int slot,ItemStack source,int wanted) {
            if(slot<0||slot>=capacity()||source.isEmpty()||wanted<=0)return 0;
            Entry e=entry(slot);
            if(e!=null&&!ItemStack.isSameItemSameTags(e.item,source))return 0;
            int n=Math.min(Math.min(wanted,source.getCount()),Math.max(0,limit(source)-(e==null?0:e.count)));
            if(n==0)return 0;
            if(e==null)items.put(slot,new Entry(source,n));else e.count+=n;
            source.shrink(n);dirty.run();return n;
        }
        public int depositAny(ItemStack source) {
            int before=source.getCount();
            for(int i=0;i<capacity()&&!source.isEmpty();i++)if(entry(i)!=null)deposit(i,source,source.getCount());
            for(int i=0;i<capacity()&&!source.isEmpty();i++)if(entry(i)==null)deposit(i,source,source.getCount());
            return before-source.getCount();
        }
        public ItemStack take(int slot,int requested) {
            Entry e=entry(slot);if(e==null||requested<=0)return ItemStack.EMPTY;
            int n=Math.min(Math.min(requested,e.count),e.item.getMaxStackSize());
            ItemStack result=e.item.copyWithCount(n);e.count-=n;
            if(e.count==0)items.remove(slot);dirty.run();return result;
        }
        public boolean upgrade(int kind) {
            if(kind==0){if(spaceUpgrades>=BankConfig.SIZE_LEVELS.get()||capacity()>=16384)return false;spaceUpgrades++;}
            else if(kind==1){if(stackUpgrades>=BankConfig.STACK_LEVELS.get())return false;stackUpgrades++;}
            else return false;
            dirty.run();return true;
        }
        public CompoundTag save() {
            CompoundTag root=new CompoundTag();root.putInt("Space",spaceUpgrades);root.putInt("Stack",stackUpgrades);
            ListTag list=new ListTag();items.forEach((slot,e)->{CompoundTag t=new CompoundTag();t.putInt("Slot",slot);t.putInt("Count",e.count);t.put("Item",e.item.save(new CompoundTag()));list.add(t);});root.put("Items",list);return root;
        }
        public static Account load(CompoundTag root,Runnable dirty) {
            Account a=new Account(dirty);a.spaceUpgrades=Math.max(0,Math.min(8,root.getInt("Space")));a.stackUpgrades=Math.max(0,Math.min(4,root.getInt("Stack")));
            for(Tag tag:root.getList("Items",Tag.TAG_COMPOUND)){
                CompoundTag t=(CompoundTag)tag;int slot=t.getInt("Slot"),n=t.getInt("Count");ItemStack item=ItemStack.of(t.getCompound("Item"));
                if(slot>=0&&slot<1048576&&n>0&&!item.isEmpty())a.items.put(slot,new Entry(item,n));
            }return a;
        }
    }
}
