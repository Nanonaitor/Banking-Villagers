package com.nanonaitor.banking;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
public final class BankerSpawnEgg extends ForgeSpawnEggItem {
    public BankerSpawnEgg(){super(BankingVillagers.BANKER,0x15151c,0xd6b24f,new Properties());}
    @Override public InteractionResult useOn(UseOnContext c){return c.getPlayer()!=null&&c.getPlayer().getAbilities().instabuild?super.useOn(c):InteractionResult.FAIL;}
    @Override public InteractionResultHolder<ItemStack> use(Level l,Player p,InteractionHand h){return p.getAbilities().instabuild?super.use(l,p,h):InteractionResultHolder.fail(p.getItemInHand(h));}
}
