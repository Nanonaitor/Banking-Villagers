# Banking Villagers

A personal bank with a familiar face. For Minecraft **1.20.1**, **Forge 47.4.18+**, and **Java 17**. Current version: **0.1.8**.

Right-click a banker to open your own storage. Every banker accesses the same bank for you in that world, while other players have their own separate accounts. This storage is separate from your vanilla Ender Chest.

## Features

- **162 starting slots**, with pages and a search field that dims nonmatching items.
- A banker profession with a custom coat and monocle. Unemployed adult villagers can take the job at a Bank Booth.
- Bank stalls can appear in newly generated vanilla villages; not every village is guaranteed a stall.
- A craftable Bank Booth sells upgrades that apply immediately to your bank.
- **Bank Space:** 32 emeralds to double capacity to 324 slots by default.
- **Double Stacking:** 64 emeralds to double stack limits inside the bank. Withdrawn items return to normal stack sizes.
- Each upgrade is purchased once per player bank. Owned upgrades cannot be purchased again.
- A booth bell calls its assigned, loaded banker to a safe adjacent space, with a one-second cooldown.
- Configurable starting space, upgrade multipliers, payment item, prices, and village generation weight.
- Bank contents are stored by player UUID, independently of the banker.

## Getting started

Craft the Bank Booth with this arrangement:

| Planks | Glass pane | Planks |
|---|---|---|
| Planks | Gold ingot | Planks |
| Polished deepslate slab | Polished deepslate slab | Polished deepslate slab |

Any vanilla-colored glass pane is accepted. Place the booth where an unemployed adult villager can claim it. Right-click the banker for storage or the booth for upgrades. A creative-only banker spawn egg is also available.

Install the mod on both the client and server. Arsenal and Defenders are not required.

## Configuration and saves

In a singleplayer world, use **Mods > Banking Villagers > Config**. Multiplayer settings are controlled by the server owner. The per-world file is `serverconfig/banking_villagers-server.toml`.

Reducing capacity does not delete stored items. Occupied overflow slots become withdrawal-only and disappear when emptied. Back up your world before updates. Bank data is stored in `data/banking_villagers_accounts.dat` in the world folder.

The bell does not load distant chunks. Search highlights matches on the current page without rearranging items. Tabs are not included. Certificates are decorative shop icons, not redeemable items.

## Development

Use Java 17:

```text
gradlew.bat build
gradlew.bat runGameTestServer
gradlew.bat runClient -PclientSmokeTest
```

Build output: `build/libs/banking-villagers-1.20.1-0.1.8.jar`.

The banker texture is under `src/main/resources/assets/banking_villagers/textures/entity/villager/profession/banker.png`.

Automated storage/transaction tests and client model-loading checks have passed during development. These do not replace full multiplayer/modpack playtesting.

## License

All Rights Reserved, matching the mod metadata. Vanilla texture references remain Minecraft assets and are not relicensed by this project.
