package com.nanonaitor.banking.test;
import com.nanonaitor.banking.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.nbt.*;
import net.minecraft.world.item.*;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.gametest.*;
import java.util.UUID;

@GameTestHolder(BankingVillagers.ID)
@PrefixGameTestTemplate(false)
public final class BankTests {
    @GameTest(template="empty",templateNamespace="forge",timeoutTicks=600)
    public static void unemployedVillagerClaimsBooth(GameTestHelper h){
        h.getLevel().setDayTime(2000);
        for(int x=-2;x<=6;x++)for(int z=-2;z<=6;z++)h.setBlock(new BlockPos(x,0,z),net.minecraft.world.level.block.Blocks.STONE);
        h.setBlock(new BlockPos(3,1,2),BankingVillagers.BOOTH.get().defaultBlockState());
        h.setBlock(new BlockPos(3,2,2),BankingVillagers.BOOTH.get().defaultBlockState().setValue(BoothBlock.HALF,net.minecraft.world.level.block.state.properties.DoubleBlockHalf.UPPER));
        Villager v=h.spawn(EntityType.VILLAGER,new BlockPos(1,1,2));
        v.setVillagerData(v.getVillagerData().setProfession(net.minecraft.world.entity.npc.VillagerProfession.NONE));
        h.succeedWhen(()->{
            h.assertTrue(!v.isNoAi(),"Vanilla AI remains enabled");
            h.assertTrue(v.getVillagerData().getProfession()==BankingVillagers.PROFESSION.get(),"Unemployed villager acquires banker profession");
            h.assertTrue(v.getBrain().getMemory(net.minecraft.world.entity.ai.memory.MemoryModuleType.JOB_SITE).isPresent(),"Banker has a claimed job-site memory");
        });
    }
    @GameTest(template="empty",templateNamespace="forge")
    public static void countsAndNbt(GameTestHelper h){
        var a=new BankData.Account(()->{});a.stackUpgrades=1;
        ItemStack first=new ItemStack(Items.DIAMOND,64);a.deposit(0,first,64);a.deposit(0,new ItemStack(Items.DIAMOND,64),64);
        h.assertTrue(a.count(0)==128,"128 items stored without signed-byte truncation");
        var loaded=BankData.Account.load(a.save(),()->{});
        h.assertTrue(loaded.count(0)==128,"128 count survives NBT round trip");
        h.assertTrue(loaded.take(0,128).getCount()==64&&loaded.count(0)==64,"Withdrawal never exceeds legal stack");
        ItemStack named=new ItemStack(Items.DIAMOND,12);named.setHoverName(net.minecraft.network.chat.Component.literal("Different data"));
        h.assertTrue(loaded.deposit(0,named,12)==0&&named.getCount()==12,"Different NBT never merges");
        h.succeed();
    }
    @GameTest(template="empty",templateNamespace="forge")
    public static void overflowSafety(GameTestHelper h){
        int original=BankConfig.START.get();
        try{
            BankConfig.START.set(128);var a=new BankData.Account(()->{});a.deposit(127,new ItemStack(Items.EMERALD,10),10);BankConfig.START.set(8);
            h.assertTrue(a.extent()==128&&a.count(127)==10,"Config shrink retains occupied overflow");
            h.assertTrue(a.deposit(127,new ItemStack(Items.EMERALD),1)==0,"Overflow is withdrawal-only");
            h.assertTrue(a.take(127,4).getCount()==4&&a.extent()==128,"Partially withdrawn overflow remains");
            a.take(127,64);h.assertTrue(a.extent()==8&&a.count(127)==0,"Empty overflow disappears");
        }finally{BankConfig.START.set(original);}h.succeed();
    }
    @GameTest(template="empty",templateNamespace="forge")
    public static void accountIsolation(GameTestHelper h){
        BankData data=new BankData();UUID alice=UUID.randomUUID(),bob=UUID.randomUUID();
        data.account(alice).deposit(0,new ItemStack(Items.GOLD_INGOT,3),3);
        var loaded=BankData.load(data.save(new CompoundTag()));
        h.assertTrue(loaded.account(alice).count(0)==3&&loaded.account(bob).count(0)==0,"UUID accounts isolated and persistent");
        h.assertTrue(loaded.account(alice).upgrade(1)&&!loaded.account(alice).upgrade(1),"Stack upgrade respects configured limit");
        h.assertTrue(!loaded.account(alice).upgrade(2),"Removed upgrade id rejected");h.succeed();
    }
    @GameTest(template="empty",templateNamespace="forge")
    public static void menuTransfers(GameTestHelper h){
        var p=h.makeMockPlayer();var v=new Villager(EntityType.VILLAGER,h.getLevel());v.setVillagerData(v.getVillagerData().setProfession(BankingVillagers.PROFESSION.get()));v.moveTo(p.position());
        var a=new BankData.Account(()->{});a.stackUpgrades=1;var menu=new BankMenu(1,p.getInventory(),a,v);
        menu.setCarried(new ItemStack(Items.EMERALD,64));menu.clicked(0,0,ClickType.PICKUP,p);
        menu.setCarried(new ItemStack(Items.EMERALD,64));menu.clicked(0,0,ClickType.PICKUP,p);
        h.assertTrue(a.count(0)==128&&menu.getCarried().isEmpty(),"Deposits consume carried stack exactly once");
        menu.clicked(0,0,ClickType.SWAP,p);h.assertTrue(a.count(0)==128,"Hotbar swap cannot steal ghost display");
        menu.clicked(0,1,ClickType.PICKUP,p);h.assertTrue(menu.getCarried().getCount()==64&&a.count(0)==64,"Right-click withdraw is legal-size half");
        menu.setCarried(ItemStack.EMPTY);menu.quickMoveStack(p,0);h.assertTrue(a.count(0)==0&&p.getInventory().countItem(Items.EMERALD)==64,"Shift withdraw transfers without duplication");
        menu.clicked(0,0,ClickType.PICKUP_ALL,p);h.assertTrue(a.count(0)==0,"Double click cannot mutate ghost storage");h.succeed();
    }
    @GameTest(template="empty",templateNamespace="forge")
    public static void boothTrades(GameTestHelper h){
        var p=h.makeMockPlayer();BlockPos pos=h.absolutePos(new BlockPos(1,1,1));h.getLevel().setBlock(pos,BankingVillagers.BOOTH.get().defaultBlockState(),3);p.moveTo(pos.getX(),pos.getY(),pos.getZ());
        var account=BankData.get(h.getLevel()).account(p.getUUID());account.spaceUpgrades=0;
        var m=new BoothMenu(2,p.getInventory(),pos);
        h.assertTrue(!m.clickMenuButton(p,0),"No currency means no voucher");
        p.getInventory().add(new ItemStack(Items.EMERALD,64));h.assertTrue(m.clickMenuButton(p,0),"Purchase succeeds");
        h.assertTrue(p.getInventory().countItem(Items.EMERALD)==64-BankConfig.COST[0].get()&&p.getInventory().countItem(BankingVillagers.SPACE.get())==0&&account.spaceUpgrades==1,"Exact price, direct upgrade, no certificate");
        h.assertTrue(m.owned(0)&&!m.clickMenuButton(p,0),"Owned upgrade cannot be purchased twice");
        h.assertTrue(!m.clickMenuButton(p,99),"Invalid purchase rejected");h.succeed();
    }
    @GameTest(template="empty",templateNamespace="forge")
    public static void bellAndCustomCurrency(GameTestHelper h){
        var p=h.makeMockPlayer();var pos=h.absolutePos(new BlockPos(2,1,2));
        for(int x=0;x<5;x++)for(int z=0;z<5;z++)h.setBlock(new BlockPos(x,0,z),net.minecraft.world.level.block.Blocks.STONE);
        h.getLevel().setBlock(pos,BankingVillagers.BOOTH.get().defaultBlockState(),3);p.moveTo(pos.getX()+.5,pos.getY(),pos.getZ()+.5);
        var account=BankData.get(h.getLevel()).account(p.getUUID());account.stackUpgrades=0;
        var m=new BoothMenu(3,p.getInventory(),pos);
        h.assertTrue(!m.clickMenuButton(p,2),"Unassigned booth cannot summon banker");
        Villager v=h.spawn(EntityType.VILLAGER,new BlockPos(0,1,0));v.setNoAi(true);v.setVillagerData(v.getVillagerData().setProfession(BankingVillagers.PROFESSION.get()));
        v.getBrain().setMemory(net.minecraft.world.entity.ai.memory.MemoryModuleType.JOB_SITE,net.minecraft.core.GlobalPos.of(h.getLevel().dimension(),pos));
        h.assertTrue(m.clickMenuButton(p,2),"Assigned banker teleports safely");
        h.assertTrue(v.blockPosition().distManhattan(pos)==1,"Destination is immediately adjacent");
        h.assertTrue(!m.clickMenuButton(p,2),"Bell cooldown rejects repeated click");
        String old=BankConfig.CURRENCY.get();
        try{BankConfig.CURRENCY.set("minecraft:diamond");p.getInventory().clearContent();p.getInventory().add(new ItemStack(Items.DIAMOND,64));
            h.assertTrue(m.clickMenuButton(p,1)&&account.stackUpgrades==1,"Custom currency applies upgrade directly");
            h.assertTrue(p.getInventory().countItem(Items.DIAMOND)==64-BankConfig.COST[1].get(),"Custom currency exact debit");
        }finally{BankConfig.CURRENCY.set(old);}h.succeed();
    }
    @GameTest(template="empty",templateNamespace="forge")
    public static void villageAsset(GameTestHelper h){
        var template=h.getLevel().getStructureManager().get(new ResourceLocation(BankingVillagers.ID,"village/bank_stall"));
        h.assertTrue(template.isPresent()&&template.get().getSize().getY()==5,"Bank stall NBT is loadable");
        h.assertTrue(BankingVillagers.POI.get().matchingStates().stream().allMatch(s->s.is(BankingVillagers.BOOTH.get())),"Job POI is bank booth");
        h.succeed();
    }
}
