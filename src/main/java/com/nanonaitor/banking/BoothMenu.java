package com.nanonaitor.banking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraftforge.registries.ForgeRegistries;

public final class BoothMenu extends AbstractContainerMenu {
    public final BlockPos pos;
    private final Level level;
    private final Player owner;
    private final ContainerData data=new SimpleContainerData(9);
    public BoothMenu(int id,Inventory inv,FriendlyByteBuf buf){this(id,inv,buf.readBlockPos());}
    public BoothMenu(int id,Inventory inv,BlockPos pos){
        super(BankingVillagers.SHOP_MENU.get(),id);this.pos=pos;level=inv.player.level();owner=inv.player;
        addDataSlots(data);
        for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inv,col+row*9+9,8+col*18,166+row*18));
        for(int col=0;col<9;col++)addSlot(new Slot(inv,col,8+col*18,224));
        refresh();
    }
    public int cost(int kind){return data.get(kind);}
    public boolean owned(int kind){return data.get(3+kind)>0;}
    public boolean affordable(int kind){return data.get(5+kind)>0;}
    public boolean assigned(){return data.get(7)>0;}
    public boolean bellReady(){return assigned()&&data.get(8)==0;}
    public Item displayCurrency(){return Item.byId(data.get(2));}
    public static Item currency(){ResourceLocation id=ResourceLocation.tryParse(BankConfig.CURRENCY.get());Item item=id==null?null:ForgeRegistries.ITEMS.getValue(id);return item==null||item==Items.AIR?Items.EMERALD:item;}
    private BankData.Account account(){return BankData.get((ServerLevel)level).account(owner.getUUID());}
    private Villager banker(){
        if(!(level instanceof ServerLevel server))return null;
        for(var e:server.getAllEntities())if(e instanceof Villager v&&v.isAlive()&&!v.isBaby()&&BankingVillagers.isBanker(v)){
            var job=v.getBrain().getMemory(MemoryModuleType.JOB_SITE);
            if(job.isPresent()&&job.get().equals(GlobalPos.of(level.dimension(),pos)))return v;
        }
        return null;
    }
    private void refresh(){
        if(level.isClientSide)return;
        var a=account();int total=owner.getInventory().items.stream().filter(s->s.is(currency())).mapToInt(ItemStack::getCount).sum();
        for(int i=0;i<2;i++)data.set(i,BankConfig.COST[i].get());
        data.set(2,Item.getId(currency()));data.set(3,a.spaceUpgrades);data.set(4,a.stackUpgrades);
        data.set(5,total>=cost(0)&&a.spaceUpgrades==0&&BankConfig.SIZE_LEVELS.get()>0&&a.capacity()<16384?1:0);
        data.set(6,total>=cost(1)&&a.stackUpgrades==0&&BankConfig.STACK_LEVELS.get()>0?1:0);
        Villager v=banker();data.set(7,v==null?0:1);
        data.set(8,v==null?0:(int)Math.max(0,v.getPersistentData().getLong("BankBellUntil")-level.getGameTime()));
    }
    @Override public void broadcastChanges(){refresh();super.broadcastChanges();}
    @Override public boolean stillValid(Player p){return level.getBlockState(pos).is(BankingVillagers.BOOTH.get())&&p.distanceToSqr(pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5)<=64;}
    @Override public boolean clickMenuButton(Player p,int kind){
        if(level.isClientSide||p!=owner||!stillValid(p))return false;
        if(kind==2)return ring();
        if(kind<0||kind>1)return false;
        refresh();if(owned(kind)||!affordable(kind))return false;
        if(!account().upgrade(kind))return false;
        int remaining=cost(kind);Item money=currency();
        for(ItemStack s:p.getInventory().items)if(s.is(money)&&remaining>0){int n=Math.min(s.getCount(),remaining);s.shrink(n);remaining-=n;}
        p.getInventory().setChanged();
        level.playSound(null,pos,SoundEvents.PLAYER_LEVELUP,SoundSource.BLOCKS,.5F,1.2F);
        broadcastChanges();return true;
    }
    private boolean ring(){
        Villager v=banker();if(v==null||v.getPersistentData().getLong("BankBellUntil")>level.getGameTime())return false;
        // Consume cooldown even if all neighboring destinations are obstructed.
        v.getPersistentData().putLong("BankBellUntil",level.getGameTime()+20);
        for(var d:net.minecraft.core.Direction.Plane.HORIZONTAL){
            BlockPos at=pos.relative(d);var floor=level.getBlockState(at.below());
            if(!level.hasChunkAt(at)||!floor.isFaceSturdy(level,at.below(),net.minecraft.core.Direction.UP))continue;
            if(floor.is(net.minecraft.world.level.block.Blocks.MAGMA_BLOCK)||floor.is(net.minecraft.world.level.block.Blocks.CACTUS)||floor.is(net.minecraft.world.level.block.Blocks.CAMPFIRE)||floor.is(net.minecraft.world.level.block.Blocks.SOUL_CAMPFIRE))continue;
            if(!level.getFluidState(at).isEmpty()||!level.getFluidState(at.above()).isEmpty())continue;
            if(!level.getBlockState(at).isAir()||!level.getBlockState(at.above()).isAir())continue;
            var box=v.getBoundingBox().move(at.getX()+.5-v.getX(),at.getY()-v.getY(),at.getZ()+.5-v.getZ());
            if(!level.getWorldBorder().isWithinBounds(box)||!level.noCollision(v,box)||!level.getEntities(v,box).isEmpty())continue;
            v.getNavigation().stop();v.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
            v.teleportTo(at.getX()+.5,at.getY(),at.getZ()+.5);v.setDeltaMovement(0,0,0);v.fallDistance=0;
            level.playSound(null,pos,SoundEvents.BELL_BLOCK,SoundSource.BLOCKS,1,1);broadcastChanges();return true;
        }
        owner.displayClientMessage(net.minecraft.network.chat.Component.literal("No safe empty space beside this booth."),true);
        broadcastChanges();return false;
    }
    @Override public ItemStack quickMoveStack(Player p,int i){return ItemStack.EMPTY;}
}
