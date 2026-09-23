# Banking Villagers — Minecraft 1.12.2

Personal banks accessed through banker villagers. Requires Forge 14.23.5.2860 and Java 8. Install on both client and server.

## Features

- 162 starting slots per player, with pages and search highlighting.
- The same personal bank is available through every banker in the world.
- Bank Space costs 32 emeralds and doubles capacity by default.
- Double Stacking costs 64 emeralds and doubles bank-only stack sizes. Withdrawals use normal item stack limits.
- Purchases apply immediately and can be made once per upgrade per player.
- Capacity reductions preserve occupied overflow slots for withdrawal.
- Bankers use normal villager AI and the black banker coat.
- Villager breeding has a configurable 10% chance to produce a banker baby. Babies grow into bankers; adults do not change jobs at a block in this version.
- Bank houses containing a banker and booth can appear in newly generated villages.
- A booth can link to an unassigned adult banker within eight blocks when opened. The Call button brings its assigned loaded banker to an empty supported adjacent space, with a one-second cooldown.

## Recipe

| Planks | Glass Pane | Planks |
|---|---|---|
| Planks | Gold Ingot | Planks |
| Stone Slab | Stone Slab | Stone Slab |

Clear or stained glass panes are accepted. This version uses stone slabs instead of deepslate slabs.

## Configuration

`config/banking_villagers.cfg` controls starting space, upgrade multipliers, payment item and prices, banker baby probability, and village bank weight. Server settings control gameplay. Restart after changing generation settings.

Bank data is saved in the world's `data/banking_villagers_accounts.dat`. Back up worlds before changing mods. Banks are separate from vanilla Ender Chests and from saves created in newer Minecraft versions.

## Version differences

The coat, certificates, and booth geometry are shared with the 1.20.1 edition. Block textures use available 1.12.2 equivalents for deepslate and stripped wood. The call button uses a note-block bell sound; 1.12.2 has no vanilla bell item. Villagers do not claim job blocks in this edition.

## Build

With Java 8, run `gradlew.bat build`. The build runs storage persistence, stack-count, account-isolation and overflow checks. `gradlew.bat runClient -PclientSmokeTest` performs a client asset-loading check.

All Rights Reserved.
