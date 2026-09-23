package com.nanonaitor.banking;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.*;
import java.util.stream.Collectors;

@Mod(BankingVillagers.ID)
public final class BankingVillagers {
    public static final String ID="banking_villagers";
    public static final DeferredRegister<Block> BLOCKS=DeferredRegister.create(ForgeRegistries.BLOCKS,ID);
    public static final DeferredRegister<Item> ITEMS=DeferredRegister.create(ForgeRegistries.ITEMS,ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(ForgeRegistries.ENTITY_TYPES,ID);
    public static final DeferredRegister<PoiType> POIS=DeferredRegister.create(ForgeRegistries.POI_TYPES,ID);
    public static final DeferredRegister<VillagerProfession> PROFESSIONS=DeferredRegister.create(ForgeRegistries.VILLAGER_PROFESSIONS,ID);
    public static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(ForgeRegistries.MENU_TYPES,ID);
    public static final DeferredRegister<CreativeModeTab> TABS=DeferredRegister.create(Registries.CREATIVE_MODE_TAB,ID);
    public static final RegistryObject<Block> BOOTH=BLOCKS.register("bank_booth",BoothBlock::new);
    public static final RegistryObject<Item> BOOTH_ITEM=ITEMS.register("bank_booth",()->new BlockItem(BOOTH.get(),new Item.Properties()));
    public static final RegistryObject<PoiType> POI=POIS.register("bank_booth",()->new PoiType(BOOTH.get().getStateDefinition().getPossibleStates().stream().filter(s->s.getValue(BoothBlock.HALF)==DoubleBlockHalf.LOWER).collect(Collectors.toSet()),1,1));
    public static final RegistryObject<VillagerProfession> PROFESSION=PROFESSIONS.register("banker",()->new VillagerProfession("banker",h->h.value()==POI.get(),h->h.value()==POI.get(),ImmutableSet.of(),ImmutableSet.of(),SoundEvents.VILLAGER_WORK_LIBRARIAN));
    public static final RegistryObject<EntityType<BankerEntity>> BANKER=ENTITIES.register("banker",()->EntityType.Builder.<BankerEntity>of(BankerEntity::new,MobCategory.CREATURE).sized(.6F,1.95F).clientTrackingRange(10).build(ID+":banker"));
    public static final RegistryObject<Item> EGG=ITEMS.register("banker_spawn_egg",BankerSpawnEgg::new);
    public static final RegistryObject<Item> SPACE=ITEMS.register("space_certificate",()->new CertificateItem(0));
    public static final RegistryObject<Item> STACK=ITEMS.register("stack_certificate",()->new CertificateItem(1));
    public static final RegistryObject<MenuType<BankMenu>> BANK_MENU=MENUS.register("bank",()->IForgeMenuType.create(BankMenu::new));
    public static final RegistryObject<MenuType<BoothMenu>> SHOP_MENU=MENUS.register("booth",()->IForgeMenuType.create(BoothMenu::new));
    public static final RegistryObject<CreativeModeTab> CREATIVE=TABS.register("banking",()->CreativeModeTab.builder().title(Component.translatable("itemGroup.banking_villagers")).icon(()->new ItemStack(BOOTH_ITEM.get())).displayItems((p,out)->{out.accept(BOOTH_ITEM.get());out.accept(EGG.get());}).build());
    public BankingVillagers(){
        var bus=FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);POIS.register(bus);PROFESSIONS.register(bus);MENUS.register(bus);TABS.register(bus);
        bus.addListener(this::attributes);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER,BankConfig.SPEC);
        MinecraftForge.EVENT_BUS.addListener(this::interact);
        MinecraftForge.EVENT_BUS.addListener(this::removedItems);
        MinecraftForge.EVENT_BUS.addListener(VillageBanks::inject);
    }
    private void attributes(EntityAttributeCreationEvent e){e.put(BANKER.get(),Villager.createAttributes().build());}
    private void removedItems(net.minecraftforge.registries.MissingMappingsEvent event){
        // Intentional removal: suppress missing-registry prompts for old worlds.
        for(var mapping:event.getMappings(ForgeRegistries.Keys.ITEMS,ID))
            if(mapping.getKey().getPath().equals("tabs_certificate"))mapping.ignore();
    }
    public static Item certificate(int type){return type==0?SPACE.get():STACK.get();}
    public static boolean isBanker(Villager v){return v instanceof BankerEntity||v.getVillagerData().getProfession()==PROFESSION.get();}
    private void interact(PlayerInteractEvent.EntityInteract e){
        if(!(e.getTarget() instanceof Villager v)||!isBanker(v)||v.isBaby())return;
        e.setCanceled(true);e.setCancellationResult(InteractionResult.SUCCESS);
        if(!(e.getEntity() instanceof ServerPlayer player))return;
        if(player.distanceToSqr(v)>64)return;
        BankData.Account a=BankData.get(player.serverLevel()).account(player.getUUID());
        NetworkHooks.openScreen(player,new SimpleMenuProvider((id,inv,p)->new BankMenu(id,inv,a,v),Component.translatable("container.banking_villagers.bank")));
    }
}
