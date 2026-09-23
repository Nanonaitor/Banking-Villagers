package com.nanonaitor.banking;

import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;

public final class BankMenu extends AbstractContainerMenu {
    public static final int PAGE_SIZE=54;
    private final SimpleContainer display=new SimpleContainer(PAGE_SIZE);
    private final ContainerData info=new SimpleContainerData(5+PAGE_SIZE);
    private final BankData.Account account;
    private final Villager banker;
    private final Player owner;
    public BankMenu(int id,Inventory inv,FriendlyByteBuf buf){this(id,inv,null,null);}
    public BankMenu(int id,Inventory inv,BankData.Account account,Villager banker){
        super(BankingVillagers.BANK_MENU.get(),id);this.account=account;this.banker=banker;this.owner=inv.player;
        for(int i=0;i<PAGE_SIZE;i++)addSlot(new Slot(display,i,8+(i%9)*18,36+(i/9)*18){
            @Override public boolean mayPlace(ItemStack s){return false;}
            @Override public boolean mayPickup(Player p){return false;}
        });
        for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inv,col+row*9+9,8+col*18,166+row*18));
        for(int col=0;col<9;col++)addSlot(new Slot(inv,col,8+col*18,224));
        addDataSlots(info);refresh();
    }
    public int page(){return info.get(0);} public int pages(){return Math.max(1,info.get(1));}
    public int capacity(){return info.get(2);}
    public int bankCount(int slot){return info.get(5+slot);}
    public boolean usable(int slot){return page()*PAGE_SIZE+slot<capacity();}
    private void refresh(){
        if(account==null)return;
        int pages=Math.max(1,(account.extent()+PAGE_SIZE-1)/PAGE_SIZE);
        info.set(0,Math.max(0,Math.min(info.get(0),pages-1)));info.set(1,pages);info.set(2,account.capacity());info.set(3,0);info.set(4,account.stackUpgrades);
        for(int i=0;i<PAGE_SIZE;i++){
            BankData.Entry e=account.entry(page()*PAGE_SIZE+i);info.set(5+i,e==null?0:e.count);
            // Counts are synchronized separately: ghost stacks never draw a second vanilla count.
            display.setItem(i,e==null?ItemStack.EMPTY:e.item.copyWithCount(1));
        }
    }
    @Override public void broadcastChanges(){refresh();super.broadcastChanges();}
    @Override public boolean stillValid(Player p){return account==null||banker!=null&&banker.isAlive()&&banker.level()==p.level()&&p.distanceToSqr(banker)<64&&BankingVillagers.isBanker(banker);}
    @Override public boolean clickMenuButton(Player p,int button){
        if(account==null||!stillValid(p)||button<0||button>1)return false;
        info.set(0,Math.max(0,Math.min(pages()-1,page()+(button==0?-1:1))));broadcastChanges();return true;
    }
    @Override public void clicked(int slot,int button,ClickType type,Player p){
        if(account==null)return;
        if(!stillValid(p))return;
        if(slot>=0&&slot<PAGE_SIZE){
            int absolute=page()*PAGE_SIZE+slot;
            if(type==ClickType.QUICK_MOVE){quickMoveStack(p,slot);return;}
            if(type!=ClickType.PICKUP||button<0||button>1)return;
            ItemStack held=getCarried();
            if(held.isEmpty()){
                int count=account.count(absolute);setCarried(account.take(absolute,button==1?(count+1)/2:count));
            }else{
                BankData.Entry e=account.entry(absolute);
                if(e!=null&&ItemStack.isSameItemSameTags(held,e.item)&&account.isOverflow(absolute)){
                    ItemStack taken=account.take(absolute,Math.min(button==1?1:held.getMaxStackSize(),held.getMaxStackSize()-held.getCount()));held.grow(taken.getCount());
                }else account.deposit(absolute,held,button==1?1:held.getCount());
            }
            broadcastChanges();return;
        }
        // Ghost bank slots prohibit vanilla pickup/drag/swap. Only explicit bank
        // transfers above may mutate stored counts; vanilla manages player slots.
        super.clicked(slot,button,type,p);broadcastChanges();
    }
    @Override public boolean canDragTo(Slot slot){return slot.container!=display&&super.canDragTo(slot);}
    @Override public boolean canTakeItemForPickAll(ItemStack stack,Slot slot){return slot.container!=display;}
    private int inventoryRoom(Player p,ItemStack stack){
        int room=0;for(ItemStack s:p.getInventory().items){if(s.isEmpty())room+=stack.getMaxStackSize();else if(ItemStack.isSameItemSameTags(s,stack))room+=Math.max(0,s.getMaxStackSize()-s.getCount());}return room;
    }
    @Override public ItemStack quickMoveStack(Player p,int index){
        if(account==null||!stillValid(p)||index<0||index>=slots.size())return ItemStack.EMPTY;
        if(index<PAGE_SIZE){
            int absolute=page()*PAGE_SIZE+index;BankData.Entry e=account.entry(absolute);
            if(e==null)return ItemStack.EMPTY;
            int n=Math.min(e.count,inventoryRoom(p,e.item));
            while(n>0){ItemStack part=account.take(absolute,n);n-=part.getCount();p.getInventory().add(part);}
        }else{Slot slot=slots.get(index);ItemStack s=slot.getItem();account.depositAny(s);slot.set(s.isEmpty()?ItemStack.EMPTY:s);slot.setChanged();}
        broadcastChanges();return ItemStack.EMPTY;
    }
}
