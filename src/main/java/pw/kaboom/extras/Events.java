package pw.kaboom.extras;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.boydti.fawe.FaweAPI;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;

import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;

import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Slime;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;

import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;

import org.bukkit.event.hanging.HangingPlaceEvent;

import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerRespawnEvent;

import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.server.ServerListPingEvent;

import org.bukkit.inventory.ItemStack;

import org.bukkit.scheduler.BukkitRunnable;

import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.math.transform.Transform;

class PasteSpawn extends BukkitRunnable {
	Main main;
	PasteSpawn(Main main) {
		this.main = main;
	}

	public void run() {
		boolean allowUndo = false;
		boolean noAir = false;
		Vector position = new Vector(0, 100, 0);

		try {
			EditSession editSession = ClipboardFormat.findByFile(main.spawnSchematic).load(main.spawnSchematic).paste(FaweAPI.getWorld("world"), position, allowUndo, !noAir, (Transform) null);
		} catch(Exception exception) {
			exception.printStackTrace();
 		}
	}
}

class Tick extends BukkitRunnable {
	public void run() {
		for (World world : Bukkit.getServer().getWorlds()) {
			WorldBorder worldborder = world.getWorldBorder();
			if (worldborder.getSize() != 60000000) {
				worldborder.setSize(60000000);
			} else if (worldborder.getCenter().getX() != 0 || worldborder.getCenter().getZ() != 0) {
				worldborder.setCenter(0, 0);
			}

			for (Entity entity : world.getEntities()) {
				if (entity instanceof LivingEntity) {
					LivingEntity mob = (LivingEntity) entity;
					AttributeInstance followAttribute = mob.getAttribute(Attribute.GENERIC_FOLLOW_RANGE);

					if (followAttribute != null && followAttribute.getBaseValue() > 32) {
						followAttribute.setBaseValue(32);
					}
				}
			}
		}
	}
}

class Events implements Listener {
	Main main;
	Events(Main main) {
		this.main = main;
	}

	@EventHandler
	void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		String message = event.getMessage().substring(0, Math.min(256, event.getMessage().length()));

		if (main.getConfig().getString(player.getUniqueId().toString()) != null) {
			String prefix = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString(player.getUniqueId().toString()));
			event.setFormat(prefix + ChatColor.RESET + " " + player.getDisplayName().toString() + ChatColor.RESET + ": " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', message));
		} else if (event.getPlayer().isOp()) {
			String prefix = ChatColor.translateAlternateColorCodes('&', "&4&l[&c&lOP&4&l]");
			event.setFormat(prefix + ChatColor.RED + " " + player.getDisplayName().toString() + ChatColor.RESET + ": " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', message));
		} else {
			String prefix = ChatColor.translateAlternateColorCodes('&', "&8&l[&7&lDeOP&8&l]");
			event.setFormat(prefix + ChatColor.GRAY + " " + player.getDisplayName().toString() + ChatColor.RESET + ": " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', message));
		}
	}

	@EventHandler
	void onBlockBreak(BlockBreakEvent event) {
		for (ItemStack item : event.getBlock().getDrops()) {
			main.editMeta(item);
		}
	}

	@EventHandler
	void onBlockDispense(BlockDispenseEvent event) {
		ItemStack item = event.getItem();
		main.editMeta(item);
	}

	@EventHandler
	void onBlockExplode(BlockExplodeEvent event) {
		Block block = event.getBlock();
		double x = block.getLocation().getX();
		double z = block.getLocation().getZ();
		double tps = Bukkit.getServer().getTPS()[0];

		if (block.getWorld().getName().equals("world")) {
			if ((x > -20 && x < 20) && (z > -20 && z < 20)) {
				event.blockList().clear();
			}
		}

		if (tps < 14) {
			event.setCancelled(true);
		}

		event.setYield(0);
	}

	@EventHandler
	void onBlockFromTo(BlockFromToEvent event) {
		Block block = event.getBlock();
		double x = block.getLocation().getX();
		double z = block.getLocation().getZ();

		if (block.getWorld().getName().equals("world")) {
			if ((x > -20 && x < 20) && (z > -20 && z < 20)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	void onBlockPhysics(BlockPhysicsEvent event) {
		Block block = event.getBlock();
		double x = block.getLocation().getX();
		double z = block.getLocation().getZ();

		if (block.getWorld().getName().equals("world")) {
			if ((x > -20 && x < 20) && (z > -20 && z < 20)) {
				event.setCancelled(true);
			}
		}

		if (block.getType() == Material.SOIL ||
		block.getType() == Material.LAVA ||
		block.getType() == Material.WATER) {
			event.setCancelled(true);
		}
        }

	@EventHandler
	void onBlockPlace(BlockPlaceEvent event) {
		if (event.getItemInHand().toString().length() > 3019) {
			event.setCancelled(true);
		}
        }

	@EventHandler
	void onBlockRedstone(BlockRedstoneEvent event) {
		double tps = Bukkit.getServer().getTPS()[0];

		if (tps < 14) {
			event.setNewCurrent(0);
		}
        }

	@EventHandler
	void onEntityAddToWorld(EntityAddToWorldEvent event) {
		Entity entity = event.getEntity();
		Entity[] chunkEntities = entity.getLocation().getChunk().getEntities();
		int onChunk = 0;

		for (Entity chunkEntity : chunkEntities) {
			if (onChunk < 50 && !(chunkEntity instanceof Player)) {
				onChunk++;
			}
		}

		if (onChunk == 50 && !(entity instanceof LivingEntity)) {
			entity.remove();
		}

		if (entity instanceof MagmaCube) {
			MagmaCube magmacube = (MagmaCube) entity;
			if (magmacube.getSize() > 100) {
				magmacube.setSize(100);
			}
		}

		if (entity instanceof Slime) {
			Slime slime = (Slime) entity;
			if (slime.getSize() > 100) {
				slime.setSize(100);
			}
		}
	}

	@EventHandler
	void onEntityChangeBlockEvent(EntityChangeBlockEvent event) {
		Block block = event.getBlock();
		double x = block.getLocation().getX();
		double z = block.getLocation().getZ();

		if (block.getWorld().getName().equals("world")) {
			if ((x > -20 && x < 20) && (z > -20 && z < 20)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	void onEntityDamage(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		if (entity.getType() == EntityType.PLAYER) {
			if (event.getCause() == DamageCause.VOID && entity.getLocation().getY() > -64 ||
			event.getCause() == DamageCause.CUSTOM ||
			event.getCause() == DamageCause.SUICIDE) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	void onEntityExplode(EntityExplodeEvent event) {
		double tps = Bukkit.getServer().getTPS()[0];

		Iterator<Block> iter = event.blockList().iterator();
		while (iter.hasNext()) {
			Block block = iter.next();
			double x = block.getX();
			double z = block.getZ();

			if (block.getWorld().getName().equals("world")) {
				if ((x > -20 && x < 20) && (z > -20 && z < 20)) {
					iter.remove();
				}
			}
		}

		if (tps < 14) {
			event.setCancelled(true);
		}

		event.setYield(0);
	}

	@EventHandler
	void onEntitySpawn(EntitySpawnEvent event) {
		Entity entity = event.getEntity();
		Entity[] chunkEntities = event.getLocation().getChunk().getEntities();
		int onChunk = 0;

		for (Entity chunkEntity : chunkEntities) {
			if (onChunk < 50 && !(chunkEntity instanceof Player)) {
				onChunk++;
			}
		}

		if (onChunk == 50 && !(entity instanceof Player)) {
			event.setCancelled(true);
		}

		if (entity instanceof LivingEntity) {
			LivingEntity mob = (LivingEntity) entity;
			AttributeInstance followAttribute = mob.getAttribute(Attribute.GENERIC_FOLLOW_RANGE);

			if (followAttribute != null && followAttribute.getBaseValue() > 32) {
				followAttribute.setBaseValue(32);
			}
		}
	}

	@EventHandler
	void onExplosionPrime(ExplosionPrimeEvent event) {
		Entity entity = event.getEntity();
		double tps = Bukkit.getServer().getTPS()[0];

		if (tps < 14) {
			event.setCancelled(true);
		}

		if (event.getRadius() > 20) {
			event.setRadius(20);
		}
	}

	@EventHandler
	void onLingeringPotionSplash(LingeringPotionSplashEvent event) {
		Entity entity = event.getEntity();
		double x = entity.getLocation().getX();
		double z = entity.getLocation().getZ();

		if (entity.getWorld().getName().equals("world")) {
			if ((x > -20 && x < 20) && (z > -20 && z < 20)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	void onPotionSplash(PotionSplashEvent event) {
		Entity entity = event.getEntity();
		double x = entity.getLocation().getX();
		double z = entity.getLocation().getZ();

		if (entity.getWorld().getName().equals("world")) {
			if ((x > -20 && x < 20) && (z > -20 && z < 20)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	void onHangingPlace(HangingPlaceEvent event) {
		Entity entity = event.getEntity();
		double x = entity.getLocation().getX();
		double z = entity.getLocation().getZ();

		if (entity.getWorld().getName().equals("world")) {
			if ((x > -20 && x < 20) && (z > -20 && z < 20)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		String arr[] = event.getMessage().split(" ");

		if (arr[0].toLowerCase().equals("//schem") ||
		arr[0].toLowerCase().equals("//schematic") ||
		arr[0].toLowerCase().equals("/schem") ||
		arr[0].toLowerCase().equals("/schematic") ||
		arr[0].toLowerCase().equals("/worldedit:/schem") ||
		arr[0].toLowerCase().equals("/worldedit:/schematic") ||
		arr[0].toLowerCase().equals("/worldedit:schem") ||
		arr[0].toLowerCase().equals("/worldedit:schematic")) {
			if (arr[1].toLowerCase().equals("delete")) {
				event.setCancelled(true);
			}
		} else if (arr[0].toLowerCase().equals("/minecraft:gamerule") ||
		arr[0].toLowerCase().equals("/gamerule")) {
			if (arr[1] != null && arr[1].toLowerCase().equals("randomtickspeed")) {
				if (arr[2] != null && Integer.parseInt(arr[2]) > 6) {
					event.setMessage(String.join(" ", arr[0], arr[1]) + " 6");
				}
			}
		} else if (arr[0].toLowerCase().equals("/minecraft:particle") ||
		arr[0].toLowerCase().equals("/particle")) {
			if (arr[9] != null && Integer.parseInt(arr[9]) > 10) {
				event.setMessage(String.join(" ", arr[0], arr[1], arr[2], arr[3], arr[4], arr[5], arr[6], arr[7], arr[8]) + " 10");
			}
		}
	}

	@EventHandler
	void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		/*if (player.getInventory().getContents() != null) {
			for (ItemStack item : player.getInventory().getContents()) {
				main.editMeta(item);
			}
		}*/
		player.getInventory().clear();

		main.getSkin(player.getName(), player);
		player.setOp(true);
		player.sendTitle(ChatColor.GRAY + "Kaboom.pw", "Free OP • Anarchy • Creative", 10, 160, 5);
	}

	@EventHandler
	void onPlayerKick(PlayerKickEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	void onPlayerLogin(PlayerLoginEvent event) {
		if (Bukkit.getOnlinePlayers().size() > 30) {
			if (main.onlineCount == 5) {
				event.allow();
				main.onlineCount = 0;
			} else {
				event.disallow(Result.KICK_OTHER, "The server is throttled due to bot attacks. Please try logging in again.");
				main.onlineCount++;
			}
		} else {
			event.allow();
		}
		/*event.allow();*/
	}

	@EventHandler
	void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		World world = event.getRespawnLocation().getWorld();
		if (world.getName().equals("world") && player.getBedSpawnLocation() == null) {
			event.setRespawnLocation(new Location(world, 0.5, 100, 0.5));
		}
	}

	@EventHandler
	void onPlayerSpawnLocation(PlayerSpawnLocationEvent event) {
		Player player = event.getPlayer();
		World world = event.getSpawnLocation().getWorld();
		if (!player.hasPlayedBefore()) {
			event.setSpawnLocation(new Location(world, 0.5, 100, 0.5));
		}
	}

	@EventHandler
	void onPreCreatureSpawn(PreCreatureSpawnEvent event) {
		Entity[] chunkEntities = event.getSpawnLocation().getChunk().getEntities();
		int onChunk = 0;

		for (Entity chunkEntity : chunkEntities) {
			if (onChunk < 50 && event.getType() != EntityType.PLAYER) {
				onChunk++;
			}
		}

		if (onChunk == 50 && event.getType() != EntityType.PLAYER) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	void onServerCommand(ServerCommandEvent event) {
		String arr[] = event.getCommand().split(" ");

		if (main.consoleCommandBlacklist.contains(arr[0].toLowerCase())) {
			event.setCancelled(true);
		} else if (arr[0].toLowerCase().equals("/schem") ||
		arr[0].toLowerCase().equals("/schematic") ||
		arr[0].toLowerCase().equals("schem") ||
		arr[0].toLowerCase().equals("schematic") ||
		arr[0].toLowerCase().equals("worldedit:/schem") ||
		arr[0].toLowerCase().equals("worldedit:/schematic") ||
		arr[0].toLowerCase().equals("worldedit:schem") ||
		arr[0].toLowerCase().equals("worldedit:schematic")) {
			if (arr[1].toLowerCase().equals("delete")) {
				event.setCancelled(true);
			}
		} else if (arr[0].toLowerCase().equals("/minecraft:gamerule") ||
		arr[0].toLowerCase().equals("/gamerule")) {
			if (arr[1] != null && arr[1].toLowerCase().equals("randomtickspeed")) {
				if (arr[2] != null && Integer.parseInt(arr[2]) > 6) {
					event.setCommand(String.join(" ", arr[0], arr[1]) + " 6");
				}
			}
		} else if (arr[0].toLowerCase().equals("minecraft:particle") ||
		arr[0].toLowerCase().equals("particle")) {
			if (arr[9] != null && Integer.parseInt(arr[9]) > 10) {
				event.setCommand(String.join(" ", arr[0], arr[1], arr[2], arr[3], arr[4], arr[5], arr[6], arr[7], arr[8]) + " 10");
			}
		}
	}

	@EventHandler
	void onServerListPing(ServerListPingEvent event) {
		event.setMaxPlayers(event.getNumPlayers() + 1);
	}

	@EventHandler
	void onSpawnerSpawn(SpawnerSpawnEvent event) {
		CreatureSpawner spawner = event.getSpawner();
		Entity entity = event.getEntity();
		LivingEntity mob = (LivingEntity) entity;

		if (spawner.getSpawnCount() > 200) {
			spawner.setSpawnCount(200);
			spawner.update();
		}
	}
}
