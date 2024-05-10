# Better Pickup

This is a Minecraft Fabric server-side mod that improves the item pickup mechanics of Minecraft.

### This mod is heavily based on the [Better Pickup](https://www.spigotmc.org/resources/better-pickup.78596/) plugin for Spigot-based servers, developed by [Chailotl](https://github.com/Chailotl). Credits for the idea of this mod as well as the mod's name goes to them.

## Pickup delays

Server admins are able to configure individual item pickup delays for items dropped by players, items dropped by a block being mined, as well as a "steal delay" to serve for the pickup delay for players who didn't throw the item or didn't mine the block that the item was dropped from.
To configure these values, the following gamerules have been added:

- `/gamerule playerDropsDelay <integer>` - The delay in ticks for items dropped by players. Default value is `10`.
- `/gamerule blockDropsDelay <integer>` - The delay in ticks for items dropped by the mining of blocks. Default value is `20`.
- `/gamerule stealDelay <integer>` - The delay in ticks for items that were not thrown by the player or mined by the player. Default value is `40`.

All of these delays have a minimum value of one tick.

## Extra features

Additionally, two extra gamerules have been added to toggle certain server-wide features related to the picking up of items.

- `/gamerule doAutoPickups <boolean>` - If set to true, items dropped from blocks will be automatically picked up by the player who mined the block without any delay. Default value is `true`.
- `/gamerule invulnerableBlockDrops <boolean>` - If set to true, items dropped from blocks  will be invulnerable to damage. Default value is `true`.

## Support

If you would like to report a bug, or make a suggestion, you can do so via the mod's [issue tracker](https://github.com/ArkoSammy12/betterpickup/issues) or join my [Discord server](https://discord.gg/wScNgcvJ3y).

## Building

Clone this repository on your PC, then open your command line prompt on the main directory of the mod, and run the command: `gradlew build`. Once the build is successful, you can find the mod under `/betterpickup/build/libs`. Use the .jar file without the `"sources"`.