package com.nanonaitor.banking;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
public final class CertificateItem extends Item {
    public final int kind;
    public CertificateItem(int kind){super(new Item.Properties().stacksTo(16));this.kind=kind;}
    @Override public void appendHoverText(ItemStack s,@Nullable Level l,List<Component> lines,TooltipFlag flag){
        lines.add(Component.literal("Decorative upgrade icon. Purchase upgrades at a Bank Booth.").withStyle(ChatFormatting.GRAY));
        lines.add(Component.translatable("tooltip.banking_villagers."+(kind==0?"space":"stack")).withStyle(kind==1?ChatFormatting.RED:ChatFormatting.DARK_GREEN));
    }
}
