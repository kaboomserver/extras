package pw.kaboom.extras;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.boydti.fawe.FaweAPI;

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
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.CreatureSpawner;

import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Slime;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.BlockSpreadEvent;

import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;

import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerRespawnEvent;

import org.bukkit.event.server.ServerCommandEvent;

import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;

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
		try {
			boolean allowUndo = false;
			boolean noAir = false;
			Vector position = new Vector(0, 100, 0);
			EditSession editSession = ClipboardFormat.findByFile(main.spawnSchematic).load(main.spawnSchematic).paste(FaweAPI.getWorld("world"), position, allowUndo, !noAir, (Transform) null);
		} catch (Exception exception) {
			exception.printStackTrace();
 		}
	}
}

class TickAsync extends BukkitRunnable {
	Main main;
	TickAsync(Main main) {
		this.main = main;
	}

	public void run() {
		for (World world : Bukkit.getServer().getWorlds()) {
			WorldBorder worldborder = world.getWorldBorder();
			if (worldborder.getSize() != 60000000) {
				worldborder.setSize(60000000);
			} else if (worldborder.getCenter().getX() != 0 || worldborder.getCenter().getZ() != 0) {
				worldborder.setCenter(0, 0);
			}

			if (world.isAutoSave() == false) {
				world.setAutoSave(true);
			}

			for (Entity entity : world.getEntities()) {
				if (entity instanceof LivingEntity) {
					LivingEntity mob = (LivingEntity) entity;
					AttributeInstance followAttribute = mob.getAttribute(Attribute.GENERIC_FOLLOW_RANGE);

					if (followAttribute != null && followAttribute.getBaseValue() > 40) {
						followAttribute.setBaseValue(32);
					}
				}
			}

			for (Chunk chunk : world.getLoadedChunks()) {
				int sizeCount = 0;
				for (BlockState block : chunk.getTileEntities()) {
					if (block instanceof Container) {
						Container container = (Container) block;

						for (ItemStack item : container.getInventory().getContents()) {
							if (item != null) {
								try {
									sizeCount = sizeCount + item.toString().length();

									if (sizeCount > 200000) {
										for (BlockState chunkBlock : chunk.getTileEntities()) {
											if (chunkBlock instanceof Container) {
												Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
													chunkBlock.getBlock().getDrops().clear();
													chunkBlock.getBlock().setType(Material.AIR);
												});
											}
										}
									}
								} catch (Exception e) {
								}
							}
						}
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
	void onBlockExplode(BlockExplodeEvent event) {
		double tps = Bukkit.getServer().getTPS()[0];

		if (tps < 14) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	void onBlockPhysics(BlockPhysicsEvent event) {
		Block block = event.getBlock();

		if (block.getType() == Material.SOIL) {
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
			if (onChunk < 50 && chunkEntity.getType() != EntityType.PLAYER) {
				onChunk++;
			}
		}

		if (onChunk == 50 && !(entity instanceof LivingEntity) ||
		tps < 14 && entity.getType() == EntityType.PRIMED_TNT) {
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
		int onChunk = 0;

		for (Entity chunkEntity : chunkEntities) {
			if (onChunk < 50 && chunkEntity.getType() != EntityType.PLAYER) {
				onChunk++;
			}
		}

		if (onChunk == 50 && entity.getType() != EntityType.PLAYER) {
			event.setCancelled(true);
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
	void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		String arr[] = event.getMessage().split(" ");

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
	void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
			main.getSkin(player.getName(), player);
		});

		for (ItemStack item : player.getInventory().getContents()) {
			if (item != null) {
				try {
					item.getItemMeta();
				} catch (Exception e) {
					player.getInventory().remove(item);
				}
			}
		}

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
	void onSpawnerSpawn(SpawnerSpawnEvent event) {
		CreatureSpawner spawner = event.getSpawner();

		if (spawner.getSpawnCount() > 200) {
			spawner.setSpawnCount(200);
			spawner.update();
		}
	}
}
