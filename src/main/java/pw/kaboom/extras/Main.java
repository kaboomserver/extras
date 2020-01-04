package pw.kaboom.extras;

import java.util.Collections;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.block.BlockFace;
import org.bukkit.plugin.java.JavaPlugin;

import pw.kaboom.extras.commands.*;
import pw.kaboom.extras.modules.block.BlockCheck;
import pw.kaboom.extras.modules.block.BlockPhysics;
import pw.kaboom.extras.modules.entity.EntityExplosion;
import pw.kaboom.extras.modules.entity.EntityKnockback;
import pw.kaboom.extras.modules.entity.EntitySpawn;
import pw.kaboom.extras.modules.entity.EntityTeleport;
import pw.kaboom.extras.modules.player.PlayerChat;
import pw.kaboom.extras.modules.player.PlayerCommand;
import pw.kaboom.extras.modules.player.PlayerConnection;
import pw.kaboom.extras.modules.player.PlayerDamage;
import pw.kaboom.extras.modules.player.PlayerInteract;
import pw.kaboom.extras.modules.player.PlayerTeleport;
import pw.kaboom.extras.modules.server.ServerCommand;
import pw.kaboom.extras.modules.server.ServerPing;

public final class Main extends JavaPlugin {
	@Override
	public void onLoad() {
		/* Fill lists */
		Collections.addAll(
			ServerCommand.consoleCommandBlacklist,
			"bukkit:about",
			"bukkit:ver",
			"bukkit:version",
			"about",
			"icanhasbukkit",
			"ver",
			"version",

			"essentials:action",
			"essentials:adventure",
			"essentials:adventuremode",
			"essentials:afk",
			"essentials:amsg",
			"essentials:away",
			"essentials:ban",
			"essentials:banip",
			"essentials:bc",
			"essentials:bcast",
			"essentials:bcastw",
			"essentials:bcw",
			"essentials:broadcast",
			"essentials:broadcastworld",
			"essentials:ci",
			"essentials:clean",
			"essentials:clear",
			"essentials:clearinvent",
			"essentials:clearinventory",
			"essentials:creative",
			"essentials:creativemode",
			"essentials:describe",
			"essentials:feed",
			"essentials:gamemode",
			"essentials:gm",
			"essentials:gma",
			"essentials:gmc",
			"essentials:gms",
			"essentials:gmsp",
			"essentials:gmt",
			"essentials:heal",
			"essentials:helpop",
			"essentials:jail",
			"essentials:kick",
			"essentials:kill",
			"essentials:m",
			"essentials:mail",
			"essentials:me",
			"essentials:memo",
			"essentials:mute",
			"essentials:msg",
			"essentials:nuke",
			"essentials:pardon",
			"essentials:pardonip",
			"essentials:pm",
			"essentials:shoutworld",
			"essentials:silence",
			"essentials:sp",
			"essentials:spec",
			"essentials:spectator",
			"essentials:sudo",
			"essentials:survival",
			"essentials:survivalmode",
			"essentials:t",
			"essentials:tele",
			"essentials:teleport",
			"essentials:tell",
			"essentials:tempban",
			"essentials:tjail",
			"essentials:togglejail",
			"essentials:tp",
			"essentials:tp2p",
			"essentials:tpaall",
			"essentials:tpall",
			"essentials:tppos",
			"essentials:tptoggle",
			"essentials:unban",
			"essentials:unbanip",
			"essentials:v",
			"essentials:vanish",
			"essentials:w",
			"essentials:warp",
			"essentials:warps",
			"essentials:whisper",
			"action",
			"adventure",
			"adventuremode",
			"afk",
			"amsg",
			"away",
			"ban",
			"banip",
			"bc",
			"bcast",
			"bcastw",
			"bcw",
			"broadcast",
			"broadcastworld",
			"ci",
			"clean",
			"clearinvent",
			"clearinventory",
			"creative",
			"creativemode",
			"describe",
			"feed",
			"gm",
			"gma",
			"gmc",
			"gms",
			"gmsp",
			"gmt",
			"heal",
			"helpop",
			"jail",
			"kill",
			"m",
			"mail",
			"memo",
			"mute",
			"nuke",
			"pardon",
			"pardonip",
			"pm",
			"shoutworld",
			"silence",
			"sp",
			"spec",
			"spectator",
			"sudo",
			"survival",
			"survivalmode",
			"t",
			"tele",
			"teleport",
			"tempban",
			"tjail",
			"togglejail",
			"tp2p",
			"tpaall",
			"tpall",
			"tppos",
			"tptoggle",
			"unban",
			"unbanip",
			"v",
			"vanish",
			"w",
			"warp",
			"warps",
			"whisper",

			"essentials:eaction",
			"essentials:eadventure",
			"essentials:eadventuremode",
			"essentials:eafk",
			"essentials:eat",
			"essentials:eamsg",
			"essentials:eaway",
			"essentials:eban",
			"essentials:ebanip",
			"essentials:ebc",
			"essentials:ebcast",
			"essentials:ebcastw",
			"essentials:ebcw",
			"essentials:ebroadcast",
			"essentials:ebroadcastworld",
			"essentials:eci",
			"essentials:eco",
			"essentials:economy",
			"essentials:eclean",
			"essentials:eclear",
			"essentials:eclearinvent",
			"essentials:eclearinventory",
			"essentials:ecreative",
			"essentials:ecreativemode",
			"essentials:edescribe",
			"essentials:eeat",
			"essentials:eeco",
			"essentials:eeconomy",
			"essentials:eemail",
			"essentials:efeed",
			"essentials:egamemode",
			"essentials:egm",
			"essentials:egma",
			"essentials:egmc",
			"essentials:egms",
			"essentials:egmsp",
			"essentials:egmt",
			"essentials:eheal",
			"essentials:ehelpop",
			"essentials:ejail",
			"essentials:ekick",
			"essentials:ekill",
			"essentials:email",
			"essentials:eme",
			"essentials:ememo",
			"essentials:emute",
			"essentials:emsg",
			"essentials:enuke",
			"essentials:epardon",
			"essentials:epardonip",
			"essentials:epm",
			"essentials:eshoutworld",
			"essentials:esilence",
			"essentials:esudo",
			"essentials:esurvival",
			"essentials:esurvivalmode",
			"essentials:etele",
			"essentials:eteleport",
			"essentials:etell",
			"essentials:etempban",
			"essentials:etjail",
			"essentials:etogglejail",
			"essentials:etp",
			"essentials:etp2p",
			"essentials:etpaall",
			"essentials:etpall",
			"essentials:etppos",
			"essentials:etptoggle",
			"essentials:eunban",
			"essentials:eunbanip",
			"essentials:ev",
			"essentials:evanish",
			"essentials:ewarp",
			"essentials:ewarps",
			"essentials:ewhisper",
			"eaction",
			"eadventure",
			"eadventuremode",
			"eafk",
			"eamsg",
			"eat",
			"eaway",
			"eban",
			"ebanip",
			"ebc",
			"ebcast",
			"ebcastw",
			"ebcw",
			"ebroadcast",
			"ebroadcastworld",
			"eci",
			"eclean",
			"eclear",
			"eclearinvent",
			"eclearinventory",
			"ecreativemode",
			"eco",
			"economy",
			"edescribe",
			"eeat",
			"eeco",
			"eeconomy",
			"eecreative",
			"eemail",
			"efeed",
			"egamemode",
			"egm",
			"egma",
			"egmc",
			"egms",
			"egmsp",
			"egmt",
			"eheal",
			"ehelpop",
			"ejail",
			"ekick",
			"ekill",
			"email",
			"eme",
			"ememo",
			"emute",
			"emsg",
			"enuke",
			"epardon",
			"epardonip",
			"epm",
			"eshoutworld",
			"esilence",
			"esudo",
			"esurvival",
			"esurvivalmode",
			"etele",
			"eteleport",
			"etell",
			"etempban",
			"etjail",
			"etogglejail",
			"etp",
			"etp2p",
			"etpaall",
			"etpall",
			"etppos",
			"etptoggle",
			"eunban",
			"eunbanip",
			"ev",
			"evanish",
			"ewarp",
			"ewarps",
			"ewhisper",

			"extras:bcraw",
			"extras:broadcastraw",
			"extras:cc",
			"extras:clearchat",
			"extras:console",
			"extras:jumpscare",
			"extras:scare",
			"extras:tellraw",
			"bcraw",
			"broadcastraw",
			"cc",
			"clearchat",
			"console",
			"jumpscare",
			"scare",
			"tellraw",

			"minecraft:clear",
			"minecraft:clone",
			"minecraft:datapack",
			"minecraft:effect",
			"minecraft:execute",
			"minecraft:gamemode",
			"minecraft:gamerule",
			"minecraft:me",
			"minecraft:msg",
			"minecraft:say",
			"minecraft:spreadplayers",
			"minecraft:tell",
			"minecraft:tellraw",
			"minecraft:title",
			"minecraft:tp",
			"minecraft:worldborder",
			"clear",
			"clone",
			"datapack",
			"effect",
			"execute",
			"gamemode",
			"gamerule",
			"me",
			"msg",
			"say",
			"spreadplayers",
			"tell",
			"title",
			"tp",
			"worldborder",

			"paper:paper",
			"paper",

			"spigot:spigot",
			"spigot",

			"viaversion:viaver",
			"viaversion:viaversion",
			"viaversion:vvbukkit",
			"viaver",
			"viaversion",
			"vvbukkit"
		);

		Collections.addAll(
			BlockPhysics.blockFaces,
			BlockFace.NORTH,
			BlockFace.SOUTH,
			BlockFace.WEST,
			BlockFace.EAST,
			BlockFace.UP
		);

		/* Load missing config.yml defaults */
		getConfig().options().copyDefaults(true);
		saveConfig();
	}

	@Override
	public void onEnable() {
		/* Commands */
		this.getCommand("clearchat").setExecutor(new CommandClearChat());
		this.getCommand("console").setExecutor(new CommandConsole());
		this.getCommand("destroyentities").setExecutor(new CommandDestroyEntities());
		this.getCommand("enchantall").setExecutor(new CommandEnchantAll());
		this.getCommand("jumpscare").setExecutor(new CommandJumpscare());
		this.getCommand("kaboom").setExecutor(new CommandKaboom());
		this.getCommand("prefix").setExecutor(new CommandPrefix());
		this.getCommand("pumpkin").setExecutor(new CommandPumpkin());
		this.getCommand("serverinfo").setExecutor(new CommandServerInfo());
		this.getCommand("skin").setExecutor(new CommandSkin());
		this.getCommand("spawn").setExecutor(new CommandSpawn());
		this.getCommand("spidey").setExecutor(new CommandSpidey());
		this.getCommand("tellraw").setExecutor(new CommandTellraw());
		this.getCommand("unloadchunks").setExecutor(new CommandUnloadChunks());
		this.getCommand("username").setExecutor(new CommandUsername());

		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Client.WINDOW_CLICK) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
				final int maxInventorySize = 46;
				if (event.getPacket().getIntegers().read(1) > maxInventorySize ||
						event.getPacket().getIntegers().read(1) < 0) {
					event.setCancelled(true);
				}
			}
		});

		/* Block-related modules */
		this.getServer().getPluginManager().registerEvents(new BlockCheck(), this);
		this.getServer().getPluginManager().registerEvents(new BlockPhysics(), this);

		/* Entity-related modules */
		this.getServer().getPluginManager().registerEvents(new EntityExplosion(), this);
		this.getServer().getPluginManager().registerEvents(new EntityKnockback(), this);
		this.getServer().getPluginManager().registerEvents(new EntitySpawn(), this);
		this.getServer().getPluginManager().registerEvents(new EntityTeleport(), this);

		/* Player-related modules */
		this.getServer().getPluginManager().registerEvents(new PlayerChat(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerCommand(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerConnection(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerDamage(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerInteract(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerTeleport(), this);

		/* Server-related modules */
		this.getServer().getPluginManager().registerEvents(new ServerCommand(), this);
		this.getServer().getPluginManager().registerEvents(new ServerPing(), this);
	}
}
