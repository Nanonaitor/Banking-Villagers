package com.nanonaitor.banking;
import net.minecraftforge.common.config.Config;
@Config(modid=BankingVillagers.ID)
public final class BankConfig {
 @Config.Comment("Starting slots. Reduced capacity retains occupied overflow for withdrawal only.") @Config.RangeInt(min=1,max=16384) public static int startingSlots=162;
 @Config.Comment("One-time bank space multiplier.") @Config.RangeInt(min=2,max=8) public static int spaceMultiplier=2;
 @Config.Comment("One-time bank stack multiplier. Withdrawals use normal stack sizes.") @Config.RangeInt(min=2,max=8) public static int stackMultiplier=2;
 @Config.Comment("Registry ID used as payment for both upgrades.") public static String currencyItem="minecraft:emerald";
 @Config.RangeInt(min=1,max=4096) public static int spaceCost=32;
 @Config.RangeInt(min=1,max=4096) public static int stackCost=64;
 @Config.Comment("Chance that a villager breeding baby is a banker, from 0 to 1.") @Config.RangeDouble(min=0,max=1) public static double babyBankerChance=.1;
 @Config.Comment("Village bank house weight. 0 disables new bank houses. Restart required.") @Config.RangeInt(min=0,max=30) public static int villageWeight=3;
}
