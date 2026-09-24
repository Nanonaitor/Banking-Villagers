package com.nanonaitor.banking;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.world.World;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.fml.common.registry.*;
import net.minecraftforge.fml.common.network.*;
import net.minecraftforge.common.MinecraftForge;
@Mod(modid=BankingVillagers.ID,name="Banking Villagers",version="0.1.1",guiFactory="com.nanonaitor.banking.client.ConfigFactory")
@Mod.EventBusSubscriber(modid=BankingVillagers.ID)
public final class BankingVillagers implements IGuiHandler {
 public static final String ID="banking_villagers";
 public static final net.minecraft.creativetab.CreativeTabs TAB=new net.minecraft.creativetab.CreativeTabs(ID){
  @Override public ItemStack getTabIconItem(){return new ItemStack(BOOTH);}
  @Override public void displayAllRelevantItems(net.minecraft.util.NonNullList<ItemStack> list){super.displayAllRelevantItems(list);ItemStack egg=new ItemStack(net.minecraft.init.Items.SPAWN_EGG);ItemMonsterPlacer.applyEntityIdToItemStack(egg,new ResourceLocation(ID,"banker"));list.add(egg);}
 };
 public static VillagerRegistry.VillagerProfession BANKER_PROFESSION;
 @Mod.Instance public static BankingVillagers instance;
 @SidedProxy(clientSide="com.nanonaitor.banking.client.ClientProxy",serverSide="com.nanonaitor.banking.CommonProxy") public static CommonProxy proxy;
 public static final BoothBlock BOOTH=new BoothBlock();
 public static final Item SPACE=item("space_certificate"),STACK=item("stack_certificate");
 private static Item item(String n){return new Item().setRegistryName(ID,n).setUnlocalizedName(ID+"."+n);}
 @SubscribeEvent public static void blocks(RegistryEvent.Register<net.minecraft.block.Block> e){e.getRegistry().register(BOOTH);}
 @SubscribeEvent public static void professions(RegistryEvent.Register<VillagerRegistry.VillagerProfession> e){
  BANKER_PROFESSION=new VillagerRegistry.VillagerProfession(ID+":banker","minecraft:textures/entity/villager/villager.png","minecraft:textures/entity/zombie_villager/zombie_villager.png");
  new VillagerRegistry.VillagerCareer(BANKER_PROFESSION,"banker");
  e.getRegistry().register(BANKER_PROFESSION);
 }
 @SubscribeEvent public static void bankerProfessionSpawn(net.minecraftforge.event.entity.EntityJoinWorldEvent e){
  if(e.getWorld().isRemote||e.getEntity().getClass()!=net.minecraft.entity.passive.EntityVillager.class)return;
  net.minecraft.entity.passive.EntityVillager v=(net.minecraft.entity.passive.EntityVillager)e.getEntity();
  if(BANKER_PROFESSION==null||v.getProfessionForge()!=BANKER_PROFESSION)return;
  // Forge already rolled the profession. Preserve the original entity data;
  // do not reroll jobs on saved villagers or on every chunk load.
  net.minecraft.nbt.NBTTagCompound data=new net.minecraft.nbt.NBTTagCompound();v.writeToNBT(data);
  BankerEntity banker=new BankerEntity(e.getWorld());banker.readFromNBT(data);banker.setProfession(BANKER_PROFESSION);
  if(e.getWorld().spawnEntity(banker))e.setCanceled(true);
 }
 @SubscribeEvent public static void items(RegistryEvent.Register<Item> e){e.getRegistry().registerAll(new ItemBlock(BOOTH).setRegistryName(BOOTH.getRegistryName()),SPACE,STACK);}
 @Mod.EventHandler public void pre(FMLPreInitializationEvent e){EntityRegistry.registerModEntity(new ResourceLocation(ID,"banker"),BankerEntity.class,"banker",0,this,64,3,true,0x171717,0xddbb44);NetworkRegistry.INSTANCE.registerGuiHandler(this,this);proxy.pre();}
 @Mod.EventHandler public void init(FMLInitializationEvent e){VillageBank.register();}
 @SubscribeEvent public static void config(net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent e){if(ID.equals(e.getModID()))net.minecraftforge.common.config.ConfigManager.sync(ID,net.minecraftforge.common.config.Config.Type.INSTANCE);}
 @SubscribeEvent public static void baby(BabyEntitySpawnEvent e){if(e.getChild() instanceof net.minecraft.entity.passive.EntityVillager&&e.getParentA().getRNG().nextDouble()<BankConfig.babyBankerChance){BankerEntity b=new BankerEntity(e.getParentA().world);b.setGrowingAge(-24000);e.setChild(b);}}
 @Override public Object getServerGuiElement(int id,EntityPlayer p,World w,int x,int y,int z){if(id==0){net.minecraft.entity.Entity e=w.getEntityByID(x);return e instanceof BankerEntity?new BankMenu(p.inventory,(BankerEntity)e):null;}BlockPos pos=new BlockPos(x,y,z);return w.getBlockState(pos).getBlock()==BOOTH?new BoothMenu(p.inventory,pos):null;}
 @Override public Object getClientGuiElement(int id,EntityPlayer p,World w,int x,int y,int z){if(id==0){net.minecraft.entity.Entity e=w.getEntityByID(x);return e instanceof BankerEntity?new com.nanonaitor.banking.client.BankScreen(new BankMenu(p.inventory,(BankerEntity)e),p):null;}return new com.nanonaitor.banking.client.BoothScreen(new BoothMenu(p.inventory,new BlockPos(x,y,z)));}
}
