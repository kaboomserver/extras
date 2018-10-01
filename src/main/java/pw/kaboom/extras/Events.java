package pw.kaboom.extras;

import java.io.File;
import java.util.Iterator;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;

import com.boydti.fawe.FaweAPI;

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

import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Slime;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
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
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.server.ServerListPingEvent;

import org.bukkit.scheduler.BukkitRunnable;

import org.spigotmc.event.player.PlayerSpawnLocationEvent;

class Tick extends BukkitRunnable {
	public void run() {
		for (World world : Bukkit.getServer().getWorlds()) {
			WorldBorder worldborder = world.getWorldBorder();
			if (worldborder.getSize() != 60000000) {
				worldborder.setSize(60000000);
			} else if (worldborder.getCenter().getX() != 0 || worldborder.getCenter().getZ() != 0) {
				worldborder.setCenter(0, 0);
			}

			/*String tickSpeed = world.getGameRuleValue​("randomTickSpeed");
			if (Integer.parseInt(tickSpeed) > 10) {
				world.setGameRuleValue​("randomTickSpeed", "10");
			}*/
		}
	}
}

class Update extends BukkitRunnable {
	public void run() {
		File file = new File("worlds/world/spawn.schematic");
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
		block.getType() == Material.STATIONARY_LAVA ||
		block.getType() == Material.STATIONARY_WATER ||
		block.getType() == Material.LAVA ||
		block.getType() == Material.WATER) {
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
		LivingEntity mob = (LivingEntity) event.getEntity();
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

		AttributeInstance followAttribute = mob.getAttribute(Attribute.GENERIC_FOLLOW_RANGE);

		if (followAttribute.getBaseValue() > 32) {
			followAttribute.setBaseValue(32);
		}

		/*System.out.println(String.valueOf("begin"));
		if (mob.getAttribute(Attribute.GENERIC_ARMOR) != null) {
			System.out.println("ARMOR " + String.valueOf(mob.getAttribute(Attribute.GENERIC_ARMOR).getBaseValue()));
		} else if (mob.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS) != null) {
			System.out.println("ARMOR_TOUGHNESS " + String.valueOf(mob.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getBaseValue()));
		} else if (mob.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE) != null) {
			System.out.println("ATTACK_DAMAGE " + String.valueOf(mob.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getBaseValue()));
		} else if (mob.getAttribute(Attribute.GENERIC_ATTACK_SPEED) != null) {
			System.out.println("ATTACK_SPEED " + String.valueOf(mob.getAttribute(Attribute.GENERIC_ATTACK_SPEED).getBaseValue()));
		} else if (mob.getAttribute(Attribute.GENERIC_FLYING_SPEED) != null) {
			System.out.println("FLYING_SPEED " + String.valueOf(mob.getAttribute(Attribute.GENERIC_FLYING_SPEED).getBaseValue()));
		} else if (mob.getAttribute(Attribute.GENERIC_FOLLOW_RANGE) != null) {
			System.out.println("FOLLOW_RANGE " + String.valueOf(mob.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).getBaseValue()));
		} else if (mob.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE) != null) {
			System.out.println("KNOCKBACK_RESISTANCE " + String.valueOf(mob.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).getBaseValue()));
		} else if (mob.getAttribute(Attribute.GENERIC_LUCK) != null) {
			System.out.println("LUCK " + String.valueOf(mob.getAttribute(Attribute.GENERIC_LUCK).getBaseValue()));
		} else if (mob.getAttribute(Attribute.GENERIC_MAX_HEALTH) != null) {
			System.out.println("MAX_HEALTH " + String.valueOf(mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()));
		} else if (mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED) != null) {
			System.out.println("MOVEMENT_SPEED " + String.valueOf(mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue()));
		} else if (mob.getAttribute(Attribute.HORSE_JUMP_STRENGTH) != null) {
			System.out.println("HORSE_JUMP_STRENGTH " + String.valueOf(mob.getAttribute(Attribute.HORSE_JUMP_STRENGTH).getBaseValue()));
		} else if (mob.getAttribute(Attribute.ZOMBIE_SPAWN_REINFORCEMENTS) != null) {
			System.out.println("ZOMBIE_SPAWN_REINFORCEMENTS " + String.valueOf(mob.getAttribute(Attribute.ZOMBIE_SPAWN_REINFORCEMENTS).getBaseValue()));
		}
		System.out.println(String.valueOf("end"));*/
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
			if (arr[2] != null && Integer.parseInt(arr[2]) > 6) {
				event.setMessage(String.join(" ", arr[0], arr[1]) + " 6");
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
		} else if (arr[0].toLowerCase().equals("/minecraft:gamerule") ||
		arr[0].toLowerCase().equals("/gamerule")) {
			if (arr[2] != null && Integer.parseInt(arr[2]) > 6) {
				event.setCommand(String.join(" ", arr[0], arr[1]) + " 6");
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
