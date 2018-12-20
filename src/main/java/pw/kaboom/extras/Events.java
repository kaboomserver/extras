package pw.kaboom.extras;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import java.util.concurrent.TimeUnit;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;
import com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Sign;

import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Slime;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.SignChangeEvent;

import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;

import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

import org.bukkit.event.server.ServerCommandEvent;

import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;

import org.bukkit.scheduler.BukkitRunnable;

class Tick extends BukkitRunnable {
	Main main;
	Tick(Main main) {
		this.main = main;
	}

	public void run() {
	}
}

class TickAsync extends BukkitRunnable {
	Main main;
	TickAsync(Main main) {
		this.main = main;
	}

	public void run() {
		for (final World world : Bukkit.getServer().getWorlds()) {
			WorldBorder worldborder = world.getWorldBorder();

			if (worldborder.getSize() != 60000000) {
				worldborder.setSize(60000000);
			}

			if (worldborder.getCenter().getX() != 0 || worldborder.getCenter().getZ() != 0) {
				worldborder.setCenter(0, 0);
			}

			if (world.isAutoSave() == false) {
				world.setAutoSave(true);
			}

			try {
				for (final Chunk chunk : world.getLoadedChunks()) {
					try {
						chunk.getTileEntities();
					} catch (Exception e) {
						Bukkit.getScheduler().runTask(main, new Runnable() {
							public void run() {
								world.regenerateChunk(chunk.getX(), chunk.getZ());
							}
						});
					}
				}
			} catch (Exception e) {
			}

			try {
				for (LivingEntity mob : world.getLivingEntities()) {
					final AttributeInstance followAttribute = mob.getAttribute(Attribute.GENERIC_FOLLOW_RANGE);

					if (followAttribute != null && followAttribute.getBaseValue() > 40) {
						Bukkit.getScheduler().runTask(main, new Runnable() {
							public void run() {
								followAttribute.setBaseValue(40);
							}
						});
					}
				}
			} catch (Exception e) {
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

		if (main.getConfig().getString(player.getUniqueId().toString()) != null) {
			String prefix = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString(player.getUniqueId().toString()));
			event.setFormat(prefix + ChatColor.RESET + " " + player.getDisplayName().toString() + ChatColor.RESET + ": " + ChatColor.RESET + "%2$s");
		} else if (event.getPlayer().isOp()) {
			String prefix = ChatColor.translateAlternateColorCodes('&', "&4&l[&c&lOP&4&l]");
			event.setFormat(prefix + ChatColor.RED + " " + player.getDisplayName().toString() + ChatColor.RESET + ": " + ChatColor.RESET + "%2$s");
		} else {
			String prefix = ChatColor.translateAlternateColorCodes('&', "&8&l[&7&lDeOP&8&l]");
			event.setFormat(prefix + ChatColor.GRAY + " " + player.getDisplayName().toString() + ChatColor.RESET + ": " + ChatColor.RESET + "%2$s");
		}

		event.setMessage(ChatColor.translateAlternateColorCodes('&', event.getMessage()));
	}

	@EventHandler
	void onBlockPhysics(BlockPhysicsEvent event) {
		Block block = event.getBlock();

		if (main.fallingBlockList.contains(block.getType())) {
			main.fallingBlockCount++;

			if (main.fallingBlockCount == 10) {
				event.setCancelled(true);
				main.fallingBlockCount = 0;
			}
		} else if (block.getType() == Material.SOIL) {
			event.setCancelled(true);
		} else if (main.nonSolidWallMountedBlockList.contains(block.getType())) {
			World world = event.getBlock().getWorld();
			int radius = 5;
			int blockCount = 0;

			for (int x = -radius; x <= radius; x++) {
				for (int y = -radius; y <= radius; y++) {
					for (int z = -radius; z <= radius; z++) {
						if (blockCount < 42) {
							Location blockLocation = new Location(world, block.getX() + x, block.getY() + y, block.getZ() + z);
							Block coordBlock = world.getBlockAt(blockLocation);

							if (coordBlock.getType() == block.getType() ||
							main.nonSolidWallMountedBlockList.contains(coordBlock.getType())) {
								blockCount++;
							}

							continue;
						}
						break;
					}
				}
			}

			if (blockCount == 42) {
				event.setCancelled(true);
			}
		} else if (main.nonSolidDoubleBlockList.contains(block.getType())) {
			if (main.nonSolidDoubleBlockList.contains(block.getRelative(BlockFace.DOWN).getType())) {
				event.setCancelled(true);
			} else if (block.getRelative(BlockFace.DOWN).getType() == Material.AIR ||
			(main.nonSolidBlockList.contains(block.getRelative(BlockFace.DOWN).getType()) &&
			!main.nonSolidDoubleBlockList.contains(block.getRelative(BlockFace.DOWN).getType()))) {
 				for (int y = block.getRelative(BlockFace.UP).getY(); y <= 256; y++) {
					World world = event.getBlock().getWorld();
					Block coordBlock = world.getBlockAt(new Location(world, block.getX(), y, block.getZ()));

					if (main.nonSolidDoubleBlockList.contains(coordBlock.getType())) {
						coordBlock.setType(Material.AIR, false);
						continue;
					}

					break;
				}

				block.setType(Material.AIR, false);
			}
		} else if (main.nonSolidSingularBlockList.contains(block.getType())) {
			if (block.getRelative(BlockFace.DOWN).getType() == Material.AIR ||
			main.nonSolidBlockList.contains(block.getRelative(BlockFace.DOWN).getType())) {
				block.setType(Material.AIR, false);
			}
		}
        }

	@EventHandler
	void onBlockPlace(BlockPlaceEvent event) {
		if (event.getItemInHand().toString().length() > 3019) {
			event.setCancelled(true);
		}

		try {
			event.getBlockPlaced().getState();
		} catch (Exception e) {
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
	void onBlockSpread(BlockSpreadEvent event) {
		double tps = Bukkit.getServer().getTPS()[0];

		if (tps < 14) {
			event.setCancelled(true);
		}
        }

	@EventHandler
	void onEntityAddToWorld(EntityAddToWorldEvent event) {
		Entity entity = event.getEntity();
		Entity[] chunkEntities = entity.getLocation().getChunk().getEntities();
		double tps = Bukkit.getServer().getTPS()[0];
		int onChunk = 0;

		for (Entity chunkEntity : chunkEntities) {
			if (onChunk < 50) {
				if (chunkEntity.getType() != EntityType.PLAYER) {
					onChunk++;
				}
				continue;
			}
			break;
		}

		if ((onChunk == 50 && !(entity instanceof LivingEntity)) ||
		(tps < 14 && entity.getType() == EntityType.PRIMED_TNT)) {
			entity.remove();
		}

		if (entity instanceof LivingEntity) {
			LivingEntity mob = (LivingEntity) entity;
			AttributeInstance followAttribute = mob.getAttribute(Attribute.GENERIC_FOLLOW_RANGE);

			if (followAttribute != null && followAttribute.getBaseValue() > 40) {
				followAttribute.setBaseValue(40);
			}
		}
	}

	@EventHandler
	void onEntityDeath(EntityDeathEvent event) {
		Entity entity = event.getEntity();

		if (entity.getType() == EntityType.PLAYER) {
			if ((entity.getLastDamageCause().getCause() == DamageCause.VOID && entity.getLocation().getY() > -64) ||
			entity.getLastDamageCause().getCause() == DamageCause.CUSTOM ||
			entity.getLastDamageCause().getCause() == DamageCause.SUICIDE) {
				((Player)entity).setHealth(20);
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	void onEntityKnockbackByEntity(EntityKnockbackByEntityEvent event) {
		Entity entity = event.getEntity();

		if (event.getKnockbackStrength() > 100) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	void onEntitySpawn(EntitySpawnEvent event) {
		Entity entity = event.getEntity();
		Entity[] chunkEntities = event.getLocation().getChunk().getEntities();
		List<LivingEntity> worldEntities = event.getLocation().getWorld().getLivingEntities();
		int count = 0;

		if (entity.getType() == EntityType.ENDER_DRAGON) {
			for (LivingEntity worldEntity : worldEntities) {
				if (count < 25) {
					if (worldEntity.getType() == EntityType.ENDER_DRAGON) {
						count++;
					}
					continue;
				}
				break;
			}

			if (count == 25) {
				event.setCancelled(true);
			}
		} else if (entity.getType() != EntityType.PLAYER) {
			for (Entity chunkEntity : chunkEntities) {
				if (count < 50) {
					if (chunkEntity.getType() != EntityType.PLAYER) {
						count++;
					}
					continue;
				}
				break;
			}

			if (count == 50) {
				event.setCancelled(true);
			}
		}

		if (entity instanceof LivingEntity) {
			LivingEntity mob = (LivingEntity) entity;
			AttributeInstance followAttribute = mob.getAttribute(Attribute.GENERIC_FOLLOW_RANGE);

			if (followAttribute != null && followAttribute.getBaseValue() > 40) {
				followAttribute.setBaseValue(40);
			}

			try {
				mob.getEquipment().getArmorContents();
			} catch (Exception e) {
				mob.getEquipment().setArmorContents(
					new ItemStack[] {
						new ItemStack(Material.AIR),
						new ItemStack(Material.AIR),
						new ItemStack(Material.AIR),
						new ItemStack(Material.AIR)
					}
				);
			}

			try {
				mob.getEquipment().getItemInMainHand();
			} catch (Exception e) {
				mob.getEquipment().setItemInMainHand(new ItemStack(Material.AIR));
			}

			try {
				mob.getEquipment().getItemInOffHand();
			} catch (Exception e) {
				mob.getEquipment().setItemInOffHand(new ItemStack(Material.AIR));
			}
		}

		if (entity.getType() == EntityType.MAGMA_CUBE) {
			MagmaCube magmacube = (MagmaCube) entity;
			if (magmacube.getSize() > 100) {
				magmacube.setSize(100);
			}
		}

		if (entity.getType() == EntityType.SLIME) {
			Slime slime = (Slime) entity;
			if (slime.getSize() > 100) {
				slime.setSize(100);
			}
		}
	}

	@EventHandler
	void onExplosionPrime(ExplosionPrimeEvent event) {
		double tps = Bukkit.getServer().getTPS()[0];

		if (tps < 14) {
			event.setCancelled(true);
		}

		if (event.getRadius() > 20) {
			event.setRadius(20);
		}
	}

	@EventHandler
	void onItemSpawn(ItemSpawnEvent event) {
		ItemStack item = event.getEntity().getItemStack();

		try {
			item.getItemMeta();
		} catch (Exception e) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		String arr[] = event.getMessage().split(" ");
		UUID playerUUID = event.getPlayer().getUniqueId();
		long millisDifference = System.currentTimeMillis() - main.commandMillisList.get(playerUUID);

		if (millisDifference < 400) {
			event.setCancelled(true);
		} else {
			main.commandMillisList.put(playerUUID, System.currentTimeMillis());
		}

/*		if (arr[0].toLowerCase().equals("/minecraft:blockdata") ||
		arr[0].toLowerCase().equals("/blockdata")) {
			if (arr[4] != null &&
			!arr[4].equals("{}")) {
				final Player player = event.getPlayer();

				Bukkit.getScheduler().scheduleAsyncDelayedTask(main, new Runnable() {
					public void run() {
						for (Chunk chunk : player.getWorld().getLoadedChunks()) {
							try {
								chunk.getTileEntities();
							} catch (Exception e) {
								player.getWorld().regenerateChunk(chunk.getX(), chunk.getZ());
							}
						}
					}
				}, 1L);
			}*/
		if (arr[0].toLowerCase().equals("/minecraft:gamerule") ||
		arr[0].toLowerCase().equals("/gamerule")) {
			if (arr[1] != null && arr[1].toLowerCase().equals("randomtickspeed")) {
				if (arr[2] != null && Integer.parseInt(arr[2]) > 6) {
					String gameruleArr[] = event.getMessage().split(" ", 2);
					event.setMessage(gameruleArr[0] + " 6");
				}
			}
		} else if (arr[0].toLowerCase().equals("/minecraft:particle") ||
		arr[0].toLowerCase().equals("/particle")) {
			if (arr[9] != null && Integer.parseInt(arr[9]) > 10) {
				String particleArr[] = event.getMessage().split(" ", 11);
				event.setMessage(particleArr[0].replaceAll(" [^ ]+$", "") + " 10 " + particleArr[1]);
			}
		}
	}

	@EventHandler
	void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		UUID playerUUID = event.getPlayer().getUniqueId();
		long millisDifference = System.currentTimeMillis() - main.interactMillisList.get(playerUUID);

		if (millisDifference < 200) {
			event.setCancelled(true);
		} else {
			main.interactMillisList.put(playerUUID, System.currentTimeMillis());
		}
	}

	@EventHandler
	void onPlayerJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();

		Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
			public void run() {
				main.getSkin(player.getName(), player);
			}
		});

		if (player.getInventory().getContents().length != 0) {
			System.out.println("hm");
			for (ItemStack item : player.getInventory().getContents()) {
				if (item != null) {
					try {
						item.getItemMeta();
					} catch (Exception e) {
						player.getInventory().remove(item);
					}
				}
			}
		}

		main.commandMillisList.put(player.getUniqueId(), System.currentTimeMillis());
		main.interactMillisList.put(player.getUniqueId(), System.currentTimeMillis());
		player.setOp(true);
		player.sendTitle(ChatColor.GRAY + "Welcome to Kaboom!", "Free OP • Anarchy • Creative", 10, 160, 5);
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
		} else if (!(event.getHostname().startsWith("play.kaboom.pw") &&
		event.getHostname().endsWith(":64518"))) {
			event.disallow(Result.KICK_OTHER, "You connected to the server using an outdated server address/IP.\nPlease use the following address/IP:\n\nkaboom.pw");
		} else {
			event.allow();
		}
		System.out.println("\"" + event.getHostname() + "\"");
	}

	@EventHandler
	void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		main.commandMillisList.remove(player.getUniqueId());
		main.interactMillisList.remove(player.getUniqueId());
	}

	@EventHandler
	void onPreCreatureSpawn(PreCreatureSpawnEvent event) {
/*		Entity[] chunkEntities = event.getSpawnLocation().getChunk().getEntities();
		int onChunk = 0;

		if (event.getType() == EntityType.ENDER_DRAGON) {
			for (Entity chunkEntity : chunkEntities) {
				if (onChunk < 5) {
					if (chunkEntity.getType() == EntityType.ENDER_DRAGON) {
						onChunk++;
						continue;
					}
				}
				break;
			}

			if (onChunk == 5) {
				event.setCancelled(true);
			}
		} else if (event.getType() != EntityType.PLAYER) {
			for (Entity chunkEntity : chunkEntities) {
				if (onChunk < 50) {
					if (chunkEntity.getType() != EntityType.PLAYER) {
						onChunk++;
						continue;
					}
				}
				break;
			}

			if (onChunk == 50) {
				event.setCancelled(true);
			}
		}*/
	}

	@EventHandler
	void onServerCommand(ServerCommandEvent event) {
		String arr[] = event.getCommand().split(" ");

		if (main.consoleCommandBlacklist.contains(arr[0].toLowerCase())) {
			event.setCancelled(true);
/*		} else if (arr[0].toLowerCase().equals("minecraft:blockdata") ||
		arr[0].toLowerCase().equals("blockdata")) {
			if (arr[4] != null &&
			!arr[4].equals("{}")) {
				final Player player = event.getPlayer();

				Bukkit.getScheduler().scheduleAsyncDelayedTask(main, new Runnable() {
					public void run() {
						for (Chunk chunk : player.getWorld().getLoadedChunks()) {
							try {
								chunk.getTileEntities();
							} catch (Exception e) {
								player.getWorld().regenerateChunk(chunk.getX(), chunk.getZ());
							}
						}
					}
				}, 1L);
			}*/
		} else if (arr[0].toLowerCase().equals("minecraft:gamerule") ||
		arr[0].toLowerCase().equals("gamerule")) {
			if (arr[1] != null && arr[1].toLowerCase().equals("randomtickspeed")) {
				if (arr[2] != null && Integer.parseInt(arr[2]) > 6) {
					String gameruleArr[] = event.getCommand().split(" ", 3);
					event.setCommand(gameruleArr[0] + " 6");
				}
			}
		} else if (arr[0].toLowerCase().equals("minecraft:particle") ||
		arr[0].toLowerCase().equals("particle")) {
			if (arr[9] != null && Integer.parseInt(arr[9]) > 10) {
				String particleArr[] = event.getCommand().split(" ", 11);
				event.setCommand(particleArr[0].replaceAll(" [^ ]+$", "") + " 10 " + particleArr[1]);
			}
		}
	}

	@EventHandler
	void onSignChange(SignChangeEvent event) {
		try {
			event.getLines();
		} catch (Exception e) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	void onSpawnerSpawn(SpawnerSpawnEvent event) {
		CreatureSpawner spawner = event.getSpawner();

		if (spawner.getSpawnCount() > 200) {
			spawner.setSpawnCount(200);
			spawner.update();
		}
	}
}
