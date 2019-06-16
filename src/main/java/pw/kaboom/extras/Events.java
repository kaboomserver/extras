package pw.kaboom.extras;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.net.ssl.HttpsURLConnection;

import java.util.concurrent.TimeUnit;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;
import com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent;
import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.Server;
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

import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandSender.Spigot;

import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Slime;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.SignChangeEvent;

import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;

import org.bukkit.event.inventory.InventoryClickEvent;

import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
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

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

import org.bukkit.plugin.Plugin;

import org.bukkit.scheduler.BukkitRunnable;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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

/*class CommandSenderOutput implements CommandSender {
	String lastOutput;
    @Override
    public void sendMessage(String output) {
        lastOutput = output;
    }
   
    @Override
    public void sendMessage(String[] strings) {
        String string = "";
        for(String s : strings) {
            string += s;
            string += " ";
        }
        lastOutput = string;
    }

    @Override
    public Server getServer() {
        return Bukkit.getServer();
    }


    @Override
    public String getName() {
        return "CommandSender";
    }

   @Override
    public boolean isPermissionSet(Permission permission) {
		return true;
	}

   @Override
    public boolean isPermissionSet(String s) {
		return true;
	}

    @Override
    public boolean hasPermission(Permission permission) {
		return true;
	}

    @Override
    public boolean hasPermission(String s) {
		return true;
	}

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
		return addAttachment(plugin);
	}

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int i) {
		return addAttachment(plugin, i);
	}

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b, int i) {
		return addAttachment(plugin, s, b, i);
	}

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b) {
		return addAttachment(plugin, s, b);
	}

    @Override
    public void removeAttachment(PermissionAttachment permissionAttachment) {}

    @Override
    public void recalculatePermissions() {}

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		return getEffectivePermissions();
	}

    @Override
    public boolean isOp() {
		return true;
	}

    @Override
    public void setOp(boolean b) {}

    @Override
    public Spigot spigot() {
		return null;
	}
}*/

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
	void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
		try {
			URL nameUrl = new URL("https://api.mojang.com/users/profiles/minecraft/" + event.getName());
			HttpsURLConnection nameConnection = (HttpsURLConnection) nameUrl.openConnection();

			if (nameConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
				InputStreamReader nameStream = new InputStreamReader(nameConnection.getInputStream());
				String uuid = new JsonParser().parse(nameStream).getAsJsonObject().get("id").getAsString();
				main.playerPremiumUUID.put(event.getName(), uuid);
				nameStream.close();
				nameConnection.disconnect();
			} else {
				nameConnection.disconnect();
			}
		} catch (Exception exception) {
		}
	}

	@EventHandler
	void onBlockFromTo(BlockFromToEvent event) {
		try {
			event.getBlock().getState();
			event.getToBlock().getState();
		} catch (Exception e) {
			event.setCancelled(true);
		}
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
		} else if (block.getType() == Material.STATIONARY_WATER ||
		block.getType() == Material.STATIONARY_LAVA) {
			if (block.getRelative(BlockFace.UP).getType() == block.getType()) {
				if (block.getRelative(BlockFace.DOWN).getType() != Material.AIR &&
				block.getRelative(BlockFace.NORTH).getType() != Material.AIR &&
				block.getRelative(BlockFace.SOUTH).getType() != Material.AIR &&
				block.getRelative(BlockFace.WEST).getType() != Material.AIR &&
				block.getRelative(BlockFace.EAST).getType() != Material.AIR) {
					event.setCancelled(true);
				}
			}
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

		if (entity instanceof LivingEntity) {
			LivingEntity mob = (LivingEntity) entity;
			AttributeInstance followAttribute = mob.getAttribute(Attribute.GENERIC_FOLLOW_RANGE);

			if (followAttribute != null && followAttribute.getBaseValue() > 40) {
				followAttribute.setBaseValue(40);
			}
		} else {
			Entity[] chunkEntities = entity.getLocation().getChunk().getEntities();
			double tps = Bukkit.getServer().getTPS()[0];
			int count = 0;

			for (Entity chunkEntity : chunkEntities) {
				if (chunkEntity.getType() != EntityType.PLAYER) {
					if (count < 50) {
						count++;
					} else {
						entity.remove();
						break;
					}
				}
			}

			if (tps < 14 && entity.getType() == EntityType.PRIMED_TNT) {
				entity.remove();
			}
		}
	}

	@EventHandler
	void onEntityDamage(EntityDamageEvent event) {
		Entity entity = event.getEntity();

		if (entity.getType() == EntityType.PLAYER) {
			if (event.getCause() == DamageCause.VOID && entity.getLocation().getY() > -64) {
				event.setDamage(0);
			}
		}
	}

	@EventHandler
	void onEntityKnockbackByEntity(EntityKnockbackByEntityEvent event) {
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
				if (worldEntity.getType() == EntityType.ENDER_DRAGON) {
					if (count < 25) {
						count++;
					} else {
						event.setCancelled(true);
						break;
					}
				}
			}
		} else if (entity.getType() != EntityType.PLAYER) {
			for (Entity chunkEntity : chunkEntities) {
				if (chunkEntity.getType() != EntityType.PLAYER) {
					if (count < 50) {
						count++;
					} else {
						event.setCancelled(true);
						break;
					}
				}
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
		try {
			event.getEntity().getItemStack().getItemMeta();
		} catch (Exception | StackOverflowError e) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		String arr[] = event.getMessage().split(" ");
		String command = event.getMessage();
		UUID playerUUID = event.getPlayer().getUniqueId();

		if (main.commandMillisList.containsKey(playerUUID)) {
			long millisDifference = System.currentTimeMillis() - main.commandMillisList.get(playerUUID);

			if (millisDifference < 400) {
				event.setCancelled(true);
			}
		}

		main.commandMillisList.put(playerUUID, System.currentTimeMillis());

		if (arr[0].toLowerCase().equals("/minecraft:gamerule") ||
		arr[0].toLowerCase().equals("/gamerule")) {
			if (arr[1] != null && arr[1].toLowerCase().equals("randomtickspeed")) {
				if (arr[2] != null && Integer.parseInt(arr[2]) > 6) {
					event.setMessage(command.replaceFirst(arr[2], "6"));
				}
			}
		} else if (arr[0].toLowerCase().equals("/minecraft:particle") ||
		arr[0].toLowerCase().equals("/particle")) {
			if (arr[9] != null && Integer.parseInt(arr[9]) > 10) {
				String particleArr[] = event.getMessage().split(" ", 11);
				event.setMessage(particleArr[0].replaceAll(" [^ ]+$", "") + " 10 " + particleArr[1]);
			}
		} else if (arr[0].toLowerCase().equals("/minecraft:blockdata") ||
		arr[0].toLowerCase().equals("/minecraft:clone") ||
		arr[0].toLowerCase().equals("/minecraft:fill") ||
		arr[0].toLowerCase().equals("/minecraft:setblock") ||
		arr[0].toLowerCase().equals("/blockdata") ||
		arr[0].toLowerCase().equals("/clone") ||
		arr[0].toLowerCase().equals("/fill") ||
		arr[0].toLowerCase().equals("/setblock")) {
			if (event.getMessage().contains("translation.test.invalid")) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();

		player.setHealth(20);
		player.setFoodLevel(20);
		player.setFireTicks(0);
		player.setRemainingAir(player.getMaximumAir());
		player.getActivePotionEffects().clear();

		if (player.getLastDamageCause().getCause() != DamageCause.CUSTOM &&
		player.getLastDamageCause().getCause() != DamageCause.SUICIDE) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
				public void run() {
					if (player.getBedSpawnLocation() != null) {
						player.teleport(player.getBedSpawnLocation());
					} else {
						World world = Bukkit.getWorld("world");
						Location spawnLoc = world.getSpawnLocation();

						for (double y = spawnLoc.getY(); y <= 256; y++) {
							Location yLoc = new Location(world, spawnLoc.getX(), y, spawnLoc.getZ());
							Block coordBlock = world.getBlockAt(yLoc);

							if (coordBlock.getType().isTransparent() &&
							coordBlock.getRelative(BlockFace.UP).getType().isTransparent()) {
								player.teleport(yLoc);
								break;
							}
						}
					}
				}
			});
		}
	}

	@EventHandler
	void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		UUID playerUUID = event.getPlayer().getUniqueId();

		if (main.interactMillisList.containsKey(playerUUID)) {
			long millisDifference = System.currentTimeMillis() - main.interactMillisList.get(playerUUID);

			if (millisDifference < 200) {
				event.setCancelled(true);
			}
		}

		main.interactMillisList.put(playerUUID, System.currentTimeMillis());
	}

	@EventHandler
	void onPlayerJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();

		if (player.hasPlayedBefore() == true) {
			player.getInventory().clear();
		}

		player.sendTitle(ChatColor.GRAY + "Welcome to Kaboom!", "Free OP • Anarchy • Creative", 10, 160, 5);
	}

	@EventHandler
	void onPlayerKick(PlayerKickEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	void onPlayerLogin(PlayerLoginEvent event) {
		if (!(event.getHostname().startsWith("play.kaboom.pw") &&
		event.getHostname().endsWith(":53950"))) {
			event.disallow(Result.KICK_OTHER, "You connected to the server using an outdated server address/IP.\nPlease use the following address/IP:\n\nkaboom.pw");
		} else {
			final Player player = event.getPlayer();

			event.allow();
			player.setOp(true);

			if (main.playerPremiumUUID.containsKey(player.getName())) {
				System.out.println(main.playerPremiumUUID.get(player.getName()));
				Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
					public void run() {
						try {
							URL uuidUrl = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + main.playerPremiumUUID.get(player.getName()) + "?unsigned=false");
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

								Bukkit.getScheduler().runTask(main, new Runnable() {
									@Override
				    					public void run() {
										player.setPlayerProfile(textureProfile);
									}
								});
							} else {
								uuidConnection.disconnect();
							}
							main.playerPremiumUUID.remove(player.getName());
						} catch (Exception exception) {
						}
					}
				});
			}
		}
	}

	@EventHandler
	void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		main.commandMillisList.remove(player.getUniqueId());
		main.interactMillisList.remove(player.getUniqueId());
	}

	@EventHandler
	void onPreCreatureSpawn(PreCreatureSpawnEvent event) {
		Entity[] chunkEntities = event.getSpawnLocation().getChunk().getEntities();
		List<LivingEntity> worldEntities = event.getSpawnLocation().getWorld().getLivingEntities();
		int count = 0;

		if (event.getType() == EntityType.ENDER_DRAGON) {
			for (LivingEntity worldEntity : worldEntities) {
				if (worldEntity.getType() == EntityType.ENDER_DRAGON) {
					if (count < 25) {
						count++;
					} else {
						event.setCancelled(true);
						break;
					}
				}
			}
		} else if (event.getType() != EntityType.PLAYER) {
			for (Entity chunkEntity : chunkEntities) {
				if (chunkEntity.getType() != EntityType.PLAYER) {
					if (count < 50) {
						count++;
					} else {
						event.setCancelled(true);
						break;
					}
				}
			}
		}
	}

	@EventHandler
	void onServerCommand(ServerCommandEvent event) {
		String arr[] = event.getCommand().split(" ");
		String command = event.getCommand();

		if (main.consoleCommandBlacklist.contains(arr[0].toLowerCase())) {
			event.setCancelled(true);
		} else if (arr[0].toLowerCase().equals("minecraft:gamerule") ||
		arr[0].toLowerCase().equals("gamerule")) {
			if (arr[1] != null && arr[1].toLowerCase().equals("randomtickspeed")) {
				if (arr[2] != null && Integer.parseInt(arr[2]) > 6) {
					event.setCommand(command.replaceFirst(arr[2], "6"));
				}
			}
		} else if (arr[0].toLowerCase().equals("minecraft:particle") ||
		arr[0].toLowerCase().equals("particle")) {
			if (arr[9] != null && Integer.parseInt(arr[9]) > 10) {
				String particleArr[] = event.getCommand().split(" ", 11);
				event.setCommand(particleArr[0].replaceAll(" [^ ]+$", "") + " 10 " + particleArr[1]);
			}
		} else if (arr[0].toLowerCase().equals("minecraft:blockdata") ||
		arr[0].toLowerCase().equals("minecraft:clone") ||
		arr[0].toLowerCase().equals("minecraft:fill") ||
		arr[0].toLowerCase().equals("minecraft:setblock") ||
		arr[0].toLowerCase().equals("blockdata") ||
		arr[0].toLowerCase().equals("clone") ||
		arr[0].toLowerCase().equals("fill") ||
		arr[0].toLowerCase().equals("setblock")) {
			if (event.getCommand().contains("translation.test.invalid")) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	void onServerListPing(PaperServerListPingEvent event) {
		if (event.getClient().getProtocolVersion() != -1) {
			event.setProtocolVersion(event.getClient().getProtocolVersion());
		} else {
			event.setProtocolVersion(485);
		}
		event.setVersion("1.14.2");
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
