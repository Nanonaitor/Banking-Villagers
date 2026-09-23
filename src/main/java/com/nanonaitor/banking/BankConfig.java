package com.nanonaitor.banking;

import net.minecraftforge.common.ForgeConfigSpec;

public final class BankConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.IntValue START, SIZE_MULTIPLIER, SIZE_LEVELS, STACK_MULTIPLIER, STACK_LEVELS, VILLAGE_WEIGHT;
    public static final ForgeConfigSpec.ConfigValue<String> CURRENCY;
    public static final ForgeConfigSpec.IntValue[] COST = new ForgeConfigSpec.IntValue[2];
    static {
        var b = new ForgeConfigSpec.Builder();
        b.push("bank");
        START=b.comment("Starting personal bank slots. WARNING: reducing capacity never deletes overflow items. Overflow slots become withdrawal-only and disappear after emptied. Restart/reopen menus after config changes.").defineInRange("startingSlots",162,1,16384);
        SIZE_MULTIPLIER=b.comment("Buying the space upgrade multiplies starting capacity by this amount. Purchased once per player bank. Hard cap: 16384 slots. Existing upgrades are preserved.").defineInRange("spaceMultiplier",2,2,8);
        SIZE_LEVELS=b.comment("0 disables space purchases. Values above 0 enable the single booth purchase; higher legacy limits do not allow repeat purchases.").defineInRange("maximumSpaceUpgrades",4,0,8);
        STACK_MULTIPLIER=b.comment("Buying the stack upgrade multiplies bank-only stack limits. Purchased once per player bank. Withdrawals use normal item limits; item data must match to merge.").defineInRange("stackMultiplier",2,2,8);
        STACK_LEVELS=b.comment("0 disables stack purchases. Values above 0 enable the single booth purchase. Existing upgrades remain applied.").defineInRange("maximumStackUpgrades",1,0,4);
        b.pop().push("shop");
        CURRENCY=b.comment("Registry ID of payment item. Invalid IDs fall back to emeralds.").define("currencyItem","minecraft:emerald");
        COST[0]=b.comment("Quantity of currencyItem charged for the space upgrade. Applied immediately; no certificate is given. Legacy setting name retained for compatibility.").defineInRange("spaceCertificateCost",32,1,4096);
        COST[1]=b.comment("Quantity of currencyItem charged for the stack upgrade. Applied immediately; no certificate is given. Payment is taken from main inventory and hotbar.").defineInRange("stackCertificateCost",64,1,4096);
        b.pop().push("villages");
        VILLAGE_WEIGHT=b.comment("Bank stall weight in vanilla village house pools. 0 disables generation. Applies only to newly generated villages; not guaranteed in every village.").defineInRange("boothWeight",3,0,30);
        b.pop(); SPEC=b.build();
    }
    public static int capacity(int upgrades) { return (int)Math.min(16384,START.get()*Math.pow(SIZE_MULTIPLIER.get(),Math.min(8,Math.max(0,upgrades)))); }
    public static int stackLimit(int natural,int upgrades) {return (int)Math.min(4096,natural*Math.pow(STACK_MULTIPLIER.get(),Math.min(4,Math.max(0,upgrades))));}
}
