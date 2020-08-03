# Extras

Extras is a Bukkit plugin that that adds extra functionality to the Kaboom server.

## Commands

| Command | Aliases | Permission | Description |
| ------- | ----- | ---------- | ----------- |
|/broadcastraw | /bcraw, /tellraw | extras.broadcastraw | Broadcasts raw text to the server|
|/broadcastvanilla | /bcv | extras.broadcastvanilla | Broadcasts text in vanilla style|
|/clearchat | /cc | extras.clearchat | Clears messages from the chat|
|/console | | extras.console | Broadcasts a message as the console|
|/destroyentities | /de | extras.destroyentities | Destroys all entities in every world|
|/enchantall | | extras.enchantall | Adds every enchantment to a held item|
|/jumpscare | /scare | extras.jumpscare | Scares a player|
|/kaboom | | extras.kaboom | I wonder...|
|/prefix | /rank, /tag | extras.prefix | Changes your tag|
|/pumpkin | | extras.pumpkin | Places a pumpkin on a player's head|
|/serverinfo | /specs | extras.serverinfo | Shows detailed server information|
|/skin | | extras.skin | Changes your skin|
|/spawn | | extras.spawn | Teleports you to spawn|
|/spidey | | extras.spidey | Annoying little spider...|
|/username | | extras.username | Changes your username on the server|


## Compiling

Use [Maven](https://maven.apache.org/) to compile the plugin.
```bash
mvn package
```
The generated .jar file will be located in the target/ folder.

## License
[Unlicense](https://unlicense.org/)
