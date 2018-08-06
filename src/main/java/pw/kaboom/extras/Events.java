package pw.kaboom.extras;

import java.io.File;
import java.util.Iterator;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;

import com.destroystokyo.paper.profile.PlayerProfile;

import com.boydti.fawe.FaweAPI;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;

import org.bukkit.block.Block;

import org.bukkit.command.BlockCommandSender;

import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Slime;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
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

import org.bukkit.event.hanging.HangingPlaceEvent;

import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.server.ServerListPingEvent;

import org.bukkit.permissions.PermissionAttachment;

import org.bukkit.scheduler.BukkitRunnable;

import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.logging.AbstractLoggingExtent;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.util.eventbus.EventHandler.Priority;
import com.sk89q.worldedit.util.eventbus.Subscribe;

class Tick extends BukkitRunnable {
	public void run() {
		for (World world : Bukkit.getServer().getWorlds()) {
			WorldBorder worldborder = world.getWorldBorder();
			if (worldborder.getSize() != 60000000) {
				worldborder.setSize(60000000);
			} else if (worldborder.getCenter().getX() != 0 || worldborder.getCenter().getZ() != 0) {
				worldborder.setCenter(0, 0);
			}
		}
	}
}

class Update extends BukkitRunnable {
	public void run() {
		File file = new File("spawn.schematic");
		boolean allowUndo = false;
		boolean noAir = false;
		Vector position = new Vector(0, 100, 0);
		try {
			EditSession editSession = ClipboardFormat.SCHEMATIC.load(file).paste(FaweAPI.getWorld("world"), position, allowUndo, !noAir, (Transform) null);
		} catch(Exception exception) {
			exception.printStackTrace();
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
	void onBlockBreakEvent(BlockBreakEvent event) {
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
	void onBlockBurnEvent(BlockBurnEvent event) {
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
	void onBlockDamage(BlockDamageEvent event) {
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
	void onBlockFade(BlockFadeEvent event) {
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
	void onBlockGrow(BlockGrowEvent event) {
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
		block.getType() == Material.STATIONARY_LAVA ||
		block.getType() == Material.STATIONARY_WATER ||
		block.getType() == Material.LAVA ||
		block.getType() == Material.WATER) {
			event.setCancelled(true);
		}
        }

	@EventHandler
	void onBlockPlaceEvent(BlockPlaceEvent event) {
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
	void onBlockRedstone(BlockRedstoneEvent event) {
		Block block = event.getBlock();
		double x = block.getLocation().getX();
		double z = block.getLocation().getZ();
		double tps = Bukkit.getServer().getTPS()[0];

		if (block.getWorld().getName().equals("world") && (x > -20 && x < 20) && (z > -20 && z < 20) ||
		tps < 14) {
			event.setNewCurrent(0);
		}
        }

	@EventHandler
	void onEntityAddToWorld(EntityAddToWorldEvent event) {
		Entity entity = event.getEntity();
		Location entityLocation = entity.getLocation();

		int onChunk = 0;
		for (Entity chunkEntity : entityLocation.getWorld().getEntities()) {
			if (entityLocation.getChunk() == chunkEntity.getLocation().getChunk()) {
				onChunk++;
			}
		}

		if (onChunk >= 50) {
			entity.remove();
		}

		if (entity.getType() == EntityType.MAGMA_CUBE) {
			MagmaCube magmacube = (MagmaCube) event.getEntity();
			if (magmacube.getSize() > 100) {
				magmacube.setSize(100);
			}
		}

		if (entity.getType() == EntityType.SLIME) {
			Slime slime = (Slime) event.getEntity();
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
		Location entityLocation = event.getLocation();

		int onChunk = 0;
		for (Entity chunkEntity : entityLocation.getWorld().getEntities()) {
			if (entityLocation.getChunk() == chunkEntity.getLocation().getChunk()) {
				onChunk++;
			}
		}

		if (onChunk >= 50) {
			event.setCancelled(true);
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

		if (entity.getWorld().getName() == "world") {
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

		if (entity.getWorld().getName() == "world") {
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

		if (entity.getWorld().getName() == "world") {
			if ((x > -20 && x < 20) && (z > -20 && z < 20)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();
		double x = player.getLocation().getX();
		double z = player.getLocation().getZ();

		if (player.getWorld().getName() == "world") {
			if ((x > -20 && x < 20) && (z > -20 && z < 20)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		String arr[] = event.getMessage().split(" ");
		String ar[] = event.getMessage().split(" ", 10);

		/*if (arr[0].toLowerCase().equals("//schem") ||
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
		} else */if (arr[0].toLowerCase().equals("/minecraft:particle") ||
		arr[0].toLowerCase().equals("/particle")) {
			if (arr[9] != null && Integer.parseInt(arr[9]) > 10) {
				event.setMessage(String.join(" ", arr[0], arr[1], arr[2], arr[3], arr[4], arr[5], arr[6], arr[7], arr[8]) + " 10");
			}
		}
		event.setMessage(event.getMessage().substring(0, Math.min(256, event.getMessage().length())));
	}

	@EventHandler
	void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		player.setOp(true);
		player.sendTitle(ChatColor.GRAY + "Kaboom.pw", "Free OP • Anarchy • Creative", 10, 160, 5);
	}

	@EventHandler
	void onPlayerKick(PlayerKickEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	void onPlayerLogin(PlayerLoginEvent event) {
		event.allow();
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
}

class WorldEditEvent {
	@Subscribe(priority = Priority.VERY_EARLY)
	public void onEditSessionEvent(final EditSessionEvent event) {
		event.setExtent(new AbstractLoggingExtent(event.getExtent()) {
			private void onBlockChange(Vector position) {
				double x = position.getBlockX();
				double z = position.getBlockZ();
				if (event.getWorld().getName().equals("world") && (x > -20 && x < 20) && (z > -20 && z < 20)) {
					event.setCancelled(true);
				}
			}
		});
	}
}
