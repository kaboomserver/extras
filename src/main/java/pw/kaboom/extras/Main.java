package pw.kaboom.extras;

import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import javax.net.ssl.HttpsURLConnection;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	int onlineCount = 0;
	int fallingBlockCount = 0;
	HashMap<UUID, Long> commandMillisList = new HashMap<UUID, Long>();
	HashMap<UUID, Long> interactMillisList = new HashMap<UUID, Long>();
	HashMap<String, String> playerPremiumUUID = new HashMap<String, String>();
	HashSet<String> consoleCommandBlacklist = new HashSet<String>(Arrays.asList(new String[] {
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
		"extras:cc",
		"extras:clearchat",
		"minecraft:clear",
		"minecraft:effect",
		"minecraft:execute",
		"minecraft:gamemode",
		"minecraft:gamerule",
		"minecraft:kill",
		"minecraft:me",
		"minecraft:say",
		"minecraft:spreadplayers",
		"minecraft:tell",
		"minecraft:tellraw",
		"minecraft:title",
		"minecraft:tp",
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
		"cc",
		"ci",
		"clean",
		"clear",
		"clearchat",
		"clearinvent",
		"clearinventory",
		"creative",
		"creativemode",
		"describe",
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
		"effect",
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
		"execute",
		"feed",
		"gamemode",
		"gamerule",
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
		"me",
		"memo",
		"mute",
		"msg",
		"nuke",
		"paper:paper",
		"paper",
		"pardon",
		"pardonip",
		"pm",
		"shoutworld",
		"say",
		"silence",
		"sp",
		"spec",
		"spectator",
		"spigot:spigot",
		"spigot",
		"spreadplayers",
		"sudo",
		"survival",
		"survivalmode",
		"t",
		"tele",
		"teleport",
		"tell",
		"tempban",
		"title",
		"tjail",
		"togglejail",
		"tp",
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
	}));
	HashSet<Material> fallingBlockList = new HashSet<Material>(Arrays.asList(new Material[] {
		Material.ANVIL,
		Material.GRAVEL,
		Material.SAND,
	}));
	HashSet<Material> nonSolidDoubleBlockList = new HashSet<Material>(Arrays.asList(new Material[] {
		Material.LONG_GRASS,
		Material.SIGN_POST,
		Material.WOODEN_DOOR,
		Material.IRON_DOOR_BLOCK,
		Material.CACTUS,
		Material.SUGAR_CANE_BLOCK,
		Material.CAKE_BLOCK,
		Material.DAYLIGHT_DETECTOR,
		Material.CARPET,
		Material.DOUBLE_PLANT,
		Material.STANDING_BANNER,
		Material.DAYLIGHT_DETECTOR_INVERTED,
		Material.SPRUCE_DOOR,
		Material.BIRCH_DOOR,
		Material.JUNGLE_DOOR,
		Material.ACACIA_DOOR,
		Material.DARK_OAK_DOOR,
	}));
	HashSet<Material> nonSolidSingularBlockList = new HashSet<Material>(Arrays.asList(new Material[] {
		Material.SAPLING,
		Material.BED_BLOCK,
		Material.POWERED_RAIL,
		Material.DETECTOR_RAIL,
		Material.DEAD_BUSH,
		Material.YELLOW_FLOWER,
		Material.RED_ROSE,
		Material.BROWN_MUSHROOM,
		Material.RED_MUSHROOM,
		Material.FIRE,
		Material.CROPS,
		Material.RAILS,
		Material.STONE_PLATE,
		Material.WOOD_PLATE,
		Material.SNOW,
		Material.DIODE_BLOCK_OFF,
		Material.DIODE_BLOCK_ON,
		Material.PUMPKIN_STEM,
		Material.MELON_STEM,
		Material.WATER_LILY,
		Material.FLOWER_POT,
		Material.CARROT,
		Material.POTATO,
		Material.GOLD_PLATE,
		Material.IRON_PLATE,
		Material.REDSTONE_COMPARATOR_OFF,
		Material.REDSTONE_COMPARATOR_ON,
		Material.ACTIVATOR_RAIL,
		Material.BEETROOT_BLOCK,
		Material.NETHER_WART_BLOCK,
	}));
	HashSet<Material> nonSolidWallMountedBlockList = new HashSet<Material>(Arrays.asList(new Material[] {
		Material.TORCH,
		Material.LADDER,
		Material.WALL_SIGN,
		Material.LEVER,
		Material.REDSTONE_WIRE,
		Material.REDSTONE_TORCH_OFF,
		Material.REDSTONE_TORCH_ON,
		Material.STONE_BUTTON,
		Material.TRAP_DOOR,
		Material.VINE,
		Material.COCOA,
		Material.TRIPWIRE_HOOK,
		Material.WOOD_BUTTON,
		Material.IRON_TRAPDOOR,
		Material.WALL_BANNER,
		Material.PORTAL,
		Material.ENDER_PORTAL,
	}));
	HashSet<Material> nonSolidBlockList = new HashSet<Material>();

	public void onEnable() {
		this.nonSolidBlockList.addAll(nonSolidDoubleBlockList);
		this.nonSolidBlockList.addAll(nonSolidSingularBlockList);
		this.nonSolidBlockList.addAll(nonSolidWallMountedBlockList);

		this.getCommand("clearchat").setExecutor(new CommandClearChat());
		this.getCommand("console").setExecutor(new CommandConsole());
		this.getCommand("destroyentities").setExecutor(new CommandDestroyEntities());
		this.getCommand("enchantall").setExecutor(new CommandEnchantAll());
		this.getCommand("jumpscare").setExecutor(new CommandJumpscare());
		this.getCommand("prefix").setExecutor(new CommandPrefix(this));
		this.getCommand("skin").setExecutor(new CommandSkin(this));
		this.getCommand("spawn").setExecutor(new CommandSpawn());
		this.getCommand("tellraw").setExecutor(new CommandTellraw());
		this.getCommand("unloadchunks").setExecutor(new CommandUnloadChunks());
		this.getCommand("username").setExecutor(new CommandUsername(this));

		new Tick(this).runTaskTimer(this, 0, 1);
		new TickAsync(this).runTaskTimerAsynchronously(this, 0, 1);
		this.getServer().getPluginManager().registerEvents(new Events(this), this);
	}

	public void getSkin(String name, final Player player) {
		try {
			URL nameUrl = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
			HttpsURLConnection nameConnection = (HttpsURLConnection) nameUrl.openConnection();

			if (nameConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
				InputStreamReader nameStream = new InputStreamReader(nameConnection.getInputStream());
				String uuid = new JsonParser().parse(nameStream).getAsJsonObject().get("id").getAsString();
				nameStream.close();
				nameConnection.disconnect();

				URL uuidUrl = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
				HttpsURLConnection uuidConnection = (HttpsURLConnection) uuidUrl.openConnection();

				if (uuidConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
					InputStreamReader uuidStream = new InputStreamReader(uuidConnection.getInputStream());
					JsonObject response = new JsonParser().parse(uuidStream).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
					final String texture = response.get("value").getAsString();
					final String signature = response.get("signature").getAsString();
					uuidStream.close();
					uuidConnection.disconnect();

					final PlayerProfile textureProfile = player.getPlayerProfile();
					textureProfile.clearProperties();
					textureProfile.setProperty(new ProfileProperty("textures", texture, signature));

					Bukkit.getScheduler().runTask(this, new Runnable() {
						@Override
	    					public void run() {
							player.setPlayerProfile(textureProfile);
						}
					});
				} else {
					uuidConnection.disconnect();
				}
			} else {
				nameConnection.disconnect();
			}
		} catch (Exception exception) {
		}
	}
}
