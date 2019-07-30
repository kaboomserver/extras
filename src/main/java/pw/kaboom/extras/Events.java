package pw.kaboom.extras;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.data.Levelled;

import org.bukkit.command.BlockCommandSender;

import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.SignChangeEvent;

import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;

import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

import org.bukkit.event.server.ServerCommandEvent;

import org.bukkit.inventory.ItemStack;

import org.bukkit.scheduler.BukkitRunnable;

import com.destroystokyo.paper.event.block.TNTPrimeEvent;
import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;
import com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent;
import com.destroystokyo.paper.event.entity.PreSpawnerSpawnEvent;
import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

class AutosaveCheck extends BukkitRunnable {
	public void run() {
		for (final World world : Bukkit.getServer().getWorlds()) {
			if (world.isAutoSave() == false) {
				world.setAutoSave(true);
			}
		}
	}
}

/*class TileEntityCheck extends BukkitRunnable {
	Main main;
	TileEntityCheck(Main main) {
		this.main = main;
	}

	public void run() {
		for (final World world : Bukkit.getServer().getWorlds()) {
			for (final Chunk chunk : world.getLoadedChunks()) {
				try {
					chunk.getTileEntities();
				} catch (Exception e) {
					new BukkitRunnable() {
						public void run() {
							world.regenerateChunk(chunk.getX(), chunk.getZ());
						}
					}.runTask(main);
				}
			}
		}
	}
}*/

class Events implements Listener {
	Main main;
	Events(Main main) {
		this.main = main;
	}

	@EventHandler
	void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		final Player player = event.getPlayer();

		if (main.getConfig().getString(player.getUniqueId().toString()) != null) {
			final String prefix = ChatColor.translateAlternateColorCodes(
				'&',
				main.getConfig().getString(player.getUniqueId().toString())
			);

			event.setFormat(prefix + ChatColor.RESET + " " + player.getDisplayName().toString() + ChatColor.RESET + ": " + ChatColor.RESET + "%2$s");
		} else if (event.getPlayer().isOp()) {
			final String prefix = ChatColor.translateAlternateColorCodes(
				'&',
				"&4&l[&c&lOP&4&l]"
			);

			event.setFormat(prefix + ChatColor.RED + " " + player.getDisplayName().toString() + ChatColor.RESET + ": " + ChatColor.RESET + "%2$s");
		} else {
			final String prefix = ChatColor.translateAlternateColorCodes(
				'&',
				"&8&l[&7&lDeOP&8&l]"
			);

			event.setFormat(prefix + ChatColor.GRAY + " " + player.getDisplayName().toString() + ChatColor.RESET + ": " + ChatColor.RESET + "%2$s");
		}

		event.setMessage(ChatColor.translateAlternateColorCodes('&', event.getMessage()));
	}

	@EventHandler
	void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
		try {
			final URL nameUrl = new URL("https://api.mojang.com/users/profiles/minecraft/" + event.getName());
			final HttpsURLConnection nameConnection = (HttpsURLConnection) nameUrl.openConnection();

			if (nameConnection != null &&
				nameConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
				final InputStreamReader nameStream = new InputStreamReader(nameConnection.getInputStream());
				final String uuid = new JsonParser().parse(nameStream).getAsJsonObject().get("id").getAsString();
				nameStream.close();
				nameConnection.disconnect();

				final URL uuidUrl = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
				final HttpsURLConnection uuidConnection = (HttpsURLConnection) uuidUrl.openConnection();

				if (uuidConnection != null &&
					uuidConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
					final InputStreamReader uuidStream = new InputStreamReader(uuidConnection.getInputStream());
					final JsonObject response = new JsonParser().parse(uuidStream).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
					final String texture = response.get("value").getAsString();
					final String signature = response.get("signature").getAsString();
					uuidStream.close();
					uuidConnection.disconnect();

					final PlayerProfile textureProfile = event.getPlayerProfile();
					textureProfile.clearProperties();
					textureProfile.setProperty(new ProfileProperty("textures", texture, signature));

					main.playerProfile.put(event.getName(), textureProfile);
				}
			}
		} catch (Exception exception) {
		}
	}

	@EventHandler
	void onBlockFromTo(BlockFromToEvent event) {
		try {
			event.getBlock().getState();
			event.getToBlock().getState();
		} catch (Exception exception) {
			event.setCancelled(true);
			return;
		}

		final double tps = Bukkit.getServer().getTPS()[0];

		if (tps < 15) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	void onBlockPhysics(BlockPhysicsEvent event) {
		final Material material = event.getChangedType();

		if (main.fallingBlockList.contains(material)) {
			main.fallingBlockCount++;

			if (main.fallingBlockCount == 10) {
				event.setCancelled(true);
				main.fallingBlockCount = 0;
			}
		} else if (material == Material.FARMLAND) {
			event.setCancelled(true);
		} else if (material == Material.WATER ||
			material == Material.LAVA) {
			final Block block = event.getBlock();
			final Levelled levelledBlock = (Levelled) block.getBlockData();

			if (levelledBlock.getLevel() <= 7) {
				if (block.getRelative(BlockFace.UP).getType() != material) {
					boolean cancel = true;
					boolean solid = false;

					for (BlockFace face : main.faces) {
						if (block.getRelative(face).getType() == Material.AIR ||
							block.getRelative(face).getType() == Material.CAVE_AIR) {
							cancel = false;
						}

						if (block.getRelative(face).getType() != Material.AIR ||
							block.getRelative(face).getType() != Material.CAVE_AIR ||
							block.getRelative(face).getType() != Material.LAVA ||
							block.getRelative(face).getType() != Material.WATER) {
							solid = true;
						}
					}


					if (block.getRelative(BlockFace.UP).getType() == Material.WATER &&
					solid != true) {
						event.setCancelled(true);
					} else if (cancel == true) {
						event.setCancelled(true);
					}
				}
			}
		} else if (main.nonSolidWallMountedBlockList.contains(material)) {
			final Block block = event.getBlock();
			final World world = block.getWorld();
			final int radius = 5;
			int blockCount = 0;

			for (int x = -radius; x <= radius; x++) {
				for (int y = -radius; y <= radius; y++) {
					for (int z = -radius; z <= radius; z++) {
						if (blockCount < 42) {
							final Location blockLocation = new Location(world, block.getX() + x, block.getY() + y, block.getZ() + z);
							final Block coordBlock = world.getBlockAt(blockLocation);

							if (coordBlock.getType() == material ||
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
		} else if (main.nonSolidDoubleBlockList.contains(material)) {
			final Block block = event.getBlock();

			if (main.nonSolidDoubleBlockList.contains(block.getRelative(BlockFace.DOWN).getType())) {
				event.setCancelled(true);
			} else if (block.getRelative(BlockFace.DOWN).getType() == Material.AIR ||
				(main.nonSolidBlockList.contains(block.getRelative(BlockFace.DOWN).getType()) &&
				!main.nonSolidDoubleBlockList.contains(block.getRelative(BlockFace.DOWN).getType()))) {
 				for (int y = block.getRelative(BlockFace.UP).getY(); y <= 256; y++) {
					final World world = event.getBlock().getWorld();
					final Block coordBlock = world.getBlockAt(new Location(world, block.getX(), y, block.getZ()));

					if (main.nonSolidDoubleBlockList.contains(coordBlock.getType())) {
						coordBlock.setType(Material.AIR, false);
						continue;
					}

					break;
				}

				block.setType(Material.AIR, false);
			}
		} else if (main.nonSolidSingularBlockList.contains(material)) {
			final Block block = event.getBlock();

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
		} catch (Exception exception) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	void onBlockRedstone(BlockRedstoneEvent event) {
		final double tps = Bukkit.getServer().getTPS()[0];

		if (tps < 10) {
			event.setNewCurrent(0);
		}
	}

	@EventHandler
	void onCreatureSpawn(CreatureSpawnEvent event) {
		if (event.getSpawnReason() == SpawnReason.CUSTOM ||
			event.getSpawnReason() == SpawnReason.DEFAULT ||
			event.getSpawnReason() == SpawnReason.DISPENSE_EGG ||
			event.getSpawnReason() == SpawnReason.SPAWNER ||
			event.getSpawnReason() == SpawnReason.SPAWNER_EGG) {
			final LivingEntity mob = event.getEntity();
			final AttributeInstance followAttribute = mob.getAttribute(Attribute.GENERIC_FOLLOW_RANGE);

			if (followAttribute != null &&
				followAttribute.getBaseValue() > 40) {
				followAttribute.setBaseValue(40);
			}
		}

		if (event.getEntityType() == EntityType.DROWNED ||
			event.getEntityType() == EntityType.GIANT ||
			event.getEntityType() == EntityType.HUSK ||
			event.getEntityType() == EntityType.PIG_ZOMBIE ||
			event.getEntityType() == EntityType.PLAYER ||
			event.getEntityType() == EntityType.SKELETON ||
			event.getEntityType() == EntityType.STRAY ||
			event.getEntityType() == EntityType.WITHER_SKELETON ||
			event.getEntityType() == EntityType.ZOMBIE ||
			event.getEntityType() == EntityType.ZOMBIE_VILLAGER) {
			final LivingEntity mob = event.getEntity();

			try {
				mob.getEquipment().getArmorContents();
			} catch (Exception exception) {
				mob.getEquipment().setArmorContents(
					new ItemStack[] {
						new ItemStack(Material.AIR),
						new ItemStack(Material.AIR),
						new ItemStack(Material.AIR),
						new ItemStack(Material.AIR),
					}
				);
			}

			try {
				mob.getEquipment().getItemInMainHand();
			} catch (Exception exception) {
				mob.getEquipment().setItemInMainHand(new ItemStack(Material.AIR));
			}

			try {
				mob.getEquipment().getItemInOffHand();
			} catch (Exception exception) {
				mob.getEquipment().setItemInOffHand(new ItemStack(Material.AIR));
			}
		} else if (event.getEntityType() == EntityType.ENDER_DRAGON) {
			final int dragonCount = event.getLocation().getWorld().getEntitiesByClass(EnderDragon.class).size();

			if (dragonCount > 25) {
				event.setCancelled(true);
				return;
			}
		} else if (event.getEntityType() == EntityType.MAGMA_CUBE) {
			final MagmaCube magmacube = (MagmaCube) event.getEntity();

			if (magmacube.getSize() > 100) {
				magmacube.setSize(100);
			}
		} else if (event.getEntityType() == EntityType.SLIME) {
			final Slime slime = (Slime) event.getEntity();

			if (slime.getSize() > 100) {
				slime.setSize(100);
			}
		}
	}

	@EventHandler
	void onEntityAddToWorld(EntityAddToWorldEvent event) {
		if (event.getEntityType() != EntityType.PLAYER) {
			final Entity entity = event.getEntity();
			final int count = entity.getLocation().getChunk().getEntities().length;

			if (count > 50) {
				entity.remove();
			}
		}
	}

	@EventHandler
	void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntityType() == EntityType.PLAYER) {
			if (((event.getCause() == DamageCause.CUSTOM ||
				event.getCause() == DamageCause.SUICIDE) &&
				event.getDamage() == Short.MAX_VALUE) ||
				(event.getCause() == DamageCause.VOID &&
				event.getDamage() == Float.MAX_VALUE)) {
				event.setDamage(Float.MAX_VALUE);
				event.setCancelled(true);
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
		if (event.getEntityType() != EntityType.PLAYER) {
			final int entityCount = event.getLocation().getChunk().getEntities().length;

			if (entityCount > 50) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	void onExplosionPrime(ExplosionPrimeEvent event) {
		if (event.getRadius() > 20) {
			event.setRadius(20);
		}
	}

	@EventHandler
	void onItemSpawn(ItemSpawnEvent event) {
		try {
			event.getEntity().getItemStack().getItemMeta();
		} catch (Exception | StackOverflowError exception) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		final String[] arr = event.getMessage().split(" ");
		final String command = event.getMessage();
		final UUID playerUuid = event.getPlayer().getUniqueId();
		final long millisDifference = System.currentTimeMillis() - main.commandMillisList.get(playerUuid);

		main.commandMillisList.put(playerUuid, System.currentTimeMillis());

		if (millisDifference < 400) {
			event.setCancelled(true);
			return;
		}

		if (("/minecraft:gamerule".equals(arr[0].toLowerCase()) ||
			"/gamerule".equals(arr[0].toLowerCase())) &&
			arr.length >= 3) {
			if ("randomtickspeed".equals(arr[1].toLowerCase()) &&
				Double.parseDouble(arr[2]) > 6) {
				event.setMessage(command.replaceFirst(arr[2], "6"));
			}
		} else if (("/minecraft:particle".equals(arr[0].toLowerCase()) ||
			"/particle".equals(arr[0].toLowerCase())) &&
			arr.length >= 10) {
			if (Double.parseDouble(arr[9]) > 10) {
				final StringBuilder stringBuilder = new StringBuilder();

				for (int i = 0; i < 9; i++) {
					stringBuilder.append(arr[i] + " ");
				}
				stringBuilder.append("10 ");
				for (int i = 10; i < arr.length; i++) {
					stringBuilder.append(arr[i] + " ");
				}

				event.setMessage(stringBuilder.toString());
			}
		}
	}

	@EventHandler
	void onPlayerDeath(PlayerDeathEvent event) {
		final Player player = event.getEntity();
		final AttributeInstance maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
		boolean maxHealthLow = false;

		maxHealth.setBaseValue(20);
		try {
			player.setHealth(20);
		} catch (Exception exception) {
			maxHealth.setBaseValue(Double.POSITIVE_INFINITY);
			player.setHealth(20);
			maxHealth.setBaseValue(20);
			maxHealthLow = true;
		}
		player.setFoodLevel(20);
		player.setFireTicks(0);
		player.setRemainingAir(player.getMaximumAir());
		player.getActivePotionEffects().clear();
		event.setCancelled(true);

		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			onlinePlayer.sendMessage(event.getDeathMessage());
		}

		if ((player.getLastDamageCause() != null &&
			player.getLastDamageCause().getCause() == DamageCause.SUICIDE &&
			player.getLastDamageCause().getDamage() == Float.MAX_VALUE) ||
			maxHealthLow == true) {
			return;
		}

		if (player.getBedSpawnLocation() != null) {
			player.teleport(player.getBedSpawnLocation());
		} else {
			final World world = Bukkit.getWorld("world");
			final Location spawnLoc = world.getSpawnLocation();

			for (double y = spawnLoc.getY(); y <= 256; y++) {
				final Location yLocation = new Location(world, spawnLoc.getX(), y, spawnLoc.getZ());
				final Block coordBlock = world.getBlockAt(yLocation);

				if (!coordBlock.getType().isSolid() &&
					!coordBlock.getRelative(BlockFace.UP).getType().isSolid()) {
					player.teleport(yLocation);
					return;
				}
			}
		}
	}

	@EventHandler
	void onPlayerInteract(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		final UUID playerUuid = event.getPlayer().getUniqueId();
		final long millisDifference = System.currentTimeMillis() - main.interactMillisList.get(playerUuid);

		main.interactMillisList.put(playerUuid, System.currentTimeMillis());

		if (millisDifference < 200) {
			event.setCancelled(true);
		}
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
			main.commandMillisList.put(player.getUniqueId(), System.currentTimeMillis());
			main.interactMillisList.put(player.getUniqueId(), System.currentTimeMillis());
			try {
				player.setPlayerProfile(main.playerProfile.get(player.getName()));
			} catch (Exception exception) {
			}
			main.playerProfile.remove(player.getName());
		}
	}

	@EventHandler
	void onPlayerQuit(PlayerQuitEvent event) {
		final Player player = event.getPlayer();

		main.commandMillisList.remove(player.getUniqueId());
		main.interactMillisList.remove(player.getUniqueId());
	}

	@EventHandler
	void onPreCreatureSpawn(PreCreatureSpawnEvent event) {
		if (event.getType() != EntityType.PLAYER) {
			final int entityCount = event.getSpawnLocation().getChunk().getEntities().length;

			if (entityCount > 50) {
				event.setCancelled(true);
				return;
			} else if (event.getType() == EntityType.ENDER_DRAGON) {
				final int dragonCount = event.getSpawnLocation().getWorld().getEntitiesByClass(EnderDragon.class).size();

				if (dragonCount > 25) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}

	@EventHandler
	void onPreSpawnerSpawn(PreSpawnerSpawnEvent event) {
		try {
			final CreatureSpawner spawner = (CreatureSpawner) event.getSpawnerLocation().getBlock().getState();

			if (event.getType() == EntityType.MINECART_MOB_SPAWNER) {
				event.setCancelled(true);
				return;
			} else if (event.getType() == EntityType.FALLING_BLOCK) {
				if (spawner.getDelay() > 100) {
					spawner.setMaxSpawnDelay(100);
					spawner.setDelay(100);
					spawner.update();
				}
			}

			if (spawner.getSpawnCount() > 200) {
				spawner.setSpawnCount(200);
				spawner.update();
			}

			if (spawner.getSpawnRange() > 50) {
				spawner.setSpawnRange(50);
				spawner.update();
			}
		} catch (Exception exception) {
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler
	void onServerCommand(ServerCommandEvent event) {
		final String[] arr = event.getCommand().split(" ");
		final String command = event.getCommand();

		if (event.getSender() instanceof BlockCommandSender) {
			try {
				((BlockCommandSender)event.getSender()).getBlock().getState();
			} catch (Exception exception) {
				event.setCancelled(true);
				return;
			}
		}

		if (main.consoleCommandBlacklist.contains(arr[0].toLowerCase())) {
			event.setCancelled(true);
		} else if (("minecraft:gamerule".equals(arr[0].toLowerCase()) ||
			"gamerule".equals(arr[0].toLowerCase())) &&
			arr.length >= 3) {
			if ("randomtickspeed".equals(arr[1].toLowerCase()) &&
				Double.parseDouble(arr[2]) > 6) {
				event.setCommand(command.replaceFirst(arr[2], "6"));
			}
		} else if (("minecraft:particle".equals(arr[0].toLowerCase()) ||
			"particle".equals(arr[0].toLowerCase())) &&
			arr.length >= 10) {
			if (Double.parseDouble(arr[9]) > 10) {
				final StringBuilder stringBuilder = new StringBuilder();

				for (int i = 0; i < 9; i++) {
					stringBuilder.append(arr[i] + " ");
				}
				stringBuilder.append("10 ");
				for (int i = 10; i < arr.length; i++) {
					stringBuilder.append(arr[i] + " ");
				}

				event.setCommand(stringBuilder.toString());
			}
		}
	}

	@EventHandler
	void onServerListPing(PaperServerListPingEvent event) {
		if (event.getClient().getProtocolVersion() != -1) {
			event.setProtocolVersion(event.getClient().getProtocolVersion());
		} else {
			event.setProtocolVersion(498);
		}
		event.setVersion("1.14.4");
	}

	@EventHandler
	void onSignChange(SignChangeEvent event) {
		try {
			event.getLines();
		} catch (Exception exception) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	void onSpawnerSpawn(SpawnerSpawnEvent event) {
		if (event.getEntity().getType() == EntityType.FALLING_BLOCK) {
			final FallingBlock block = (FallingBlock) event.getEntity();

			if (block.getBlockData().getMaterial() == Material.SPAWNER) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler
	void onTNTPrime(TNTPrimeEvent event) {
		final double tps = Bukkit.getServer().getTPS()[0];

		if (tps < 10) {
			event.setCancelled(true);
		}
	}
}
