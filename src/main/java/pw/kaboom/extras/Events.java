package pw.kaboom.extras;

import java.io.File;

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
		Vector position = new Vector(0, 85, 0);
		try {
			EditSession editSession = ClipboardFormat.SCHEMATIC.load(file).paste(FaweAPI.getWorld("world"), position, allowUndo, noAir, (Transform) null);
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
		} else {
			if (event.getPlayer().isOp()) {
				String prefix = ChatColor.translateAlternateColorCodes('&', "&4&l[&c&lOP&4&l]");
				event.setFormat(prefix + ChatColor.RED + " " + player.getDisplayName().toString() + ChatColor.RESET + ": " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', message));
			} else {
				String prefix = ChatColor.translateAlternateColorCodes('&', "&8&l[&7&lDeOP&8&l]");
				event.setFormat(prefix + ChatColor.GRAY + " " + player.getDisplayName().toString() + ChatColor.RESET + ": " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', message));
			}
		}
	}

	@EventHandler
	void onBlockBreakEvent(BlockBreakEvent event) {
		Block block = event.getBlock();
		double x = block.getLocation().getX();
		double y = block.getLocation().getY();
		double z = block.getLocation().getZ();

		if (block.getWorld().getName() == "world") {
			if ((x > -20 && x < 20) && (z > -20 && z < 20)) {
				event.setCancelled(true);
			}
		}
	}


	@EventHandler
	void onBlockBurnEvent(BlockBurnEvent event) {
		Block block = event.getBlock();
		double x = block.getLocation().getX();
		double y = block.getLocation().getY();
		double z = block.getLocation().getZ();

		if (block.getWorld().getName() == "world") {
			if ((x > -20 && x < 20) && (z > -20 && z < 20)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	void onBlockDamage(BlockDamageEvent event) {
		Block block = event.getBlock();
		double x = block.getLocation().getX();
		double y = block.getLocation().getY();
		double z = block.getLocation().getZ();

		if (block.getWorld().getName() == "world") {
			if ((x > -20 && x < 20) && (z > -20 && z < 20)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	void onBlockExplode(BlockExplodeEvent event) {
		Block block = event.getBlock();
		double x = block.getLocation().getX();
		double y = block.getLocation().getY();
		double z = block.getLocation().getZ();
		double tps = Bukkit.getServer().getTPS()[0];

		if (block.getWorld().getName() == "world") {
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
		double y = block.getLocation().getY();
		double z = block.getLocation().getZ();

		if (block.getWorld().getName() == "world") {
			if ((x > -20 && x < 20) && (z > -20 && z < 20)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	void onBlockFromTo(BlockFromToEvent event) {
		Block block = event.getBlock();
		double x = block.getLocation().getX();
		double y = block.getLocation().getY();
		double z = block.getLocation().getZ();

		if (block.getWorld().getName() == "world") {
			if ((x > -20 && x < 20) && (z > -20 && z < 20)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	void onBlockGrow(BlockGrowEvent event) {
		Block block = event.getBlock();
		double x = block.getLocation().getX();
		double y = block.getLocation().getY();
		double z = block.getLocation().getZ();

		if (block.getWorld().getName() == "world") {
			if ((x > -20 && x < 20) && (z > -20 && z < 20)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	void onBlockPhysics(BlockPhysicsEvent event) {
		Block block = event.getBlock();
		double x = block.getLocation().getX();
		double y = block.getLocation().getY();
		double z = block.getLocation().getZ();

		if (block.getWorld().getName() == "world") {
			if ((x > -20 && x < 20) && (z > -20 && z < 20)) {
				event.setCancelled(true);
			}
		}
		if (block.getType() == Material.LAVA ||
		block.getType() == Material.SOIL ||
		block.getType() == Material.STATIONARY_LAVA ||
		block.getType() == Material.STATIONARY_WATER ||
		block.getType() == Material.WATER) {
			event.setCancelled(true);
		}
        }

	@EventHandler
	void onBlockPlaceEvent(BlockPlaceEvent event) {
		Block block = event.getBlock();
		double x = block.getLocation().getX();
		double y = block.getLocation().getY();
		double z = block.getLocation().getZ();

		if (block.getWorld().getName() == "world") {
			if ((x > -20 && x < 20) && (z > -20 && z < 20)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	void onBlockRedstone(BlockRedstoneEvent event) {
		Block block = event.getBlock();
		double x = block.getLocation().getX();
		double y = block.getLocation().getY();
		double z = block.getLocation().getZ();
		double tps = Bukkit.getServer().getTPS()[0];

		if (block.getWorld().getName() == "world" && (x > -20 && x < 20) && (z > -20 && z < 20) ||
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
		} else if (entity.getType() == EntityType.MAGMA_CUBE) {
			MagmaCube magmacube = (MagmaCube) event.getEntity();
			if (magmacube.getSize() > 100) {
				magmacube.setSize(100);
			}
		} else if (entity.getType() == EntityType.SLIME) {
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
		double y = block.getLocation().getY();
		double z = block.getLocation().getZ();

		if (block.getWorld().getName() == "world") {
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
		Entity entity = event.getEntity();
		double x = entity.getLocation().getX();
		double y = entity.getLocation().getY();
		double z = entity.getLocation().getZ();
		double tps = Bukkit.getServer().getTPS()[0];

		event.setYield(0);

		if (entity.getWorld().getName() == "world") {
			if ((x > -20 && x < 20) && (z > -20 && z < 20)) {
				event.blockList().clear();
			}
		}

		if (tps < 14) {
			event.setCancelled(true);
		}
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
		} else if (event.getRadius() > 20) {
			event.setRadius(20);
		}
	}

	@EventHandler
	void onLingeringPotionSplash(LingeringPotionSplashEvent event) {
		Entity entity = event.getEntity();
		double x = entity.getLocation().getX();
		double y = entity.getLocation().getY();
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
		double y = entity.getLocation().getY();
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
		double y = entity.getLocation().getY();
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
		double y = player.getLocation().getY();
		double z = player.getLocation().getZ();

		if (player.getWorld().getName() == "world") {
			if ((x > -20 && x < 20) && (z > -20 && z < 20)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		String world = player.getLocation().getWorld().getName();
		/*PermissionAttachment p = main.permissionList.get(player.getUniqueId());*/

		if (world == "world_the_end") {
			player.sendTitle(ChatColor.GRAY + "The End", "Return to the hub by typing /hub in chat", 10, 160, 5);
		} else if (world == "world_nether") {
			player.sendTitle(ChatColor.RED + "Nether", "Return to the hub by typing /hub in chat", 10, 160, 5);
		} else if (world == "world_overworld") {
			player.sendTitle(ChatColor.DARK_GREEN + "Overworld", "Return to the hub by typing /hub in chat", 10, 160, 5);
		}

/*		if (world == "world") {
			p.unsetPermission("DoBlockImage.doblockimage");
			p.unsetPermission("essentials.bigtree");
			p.unsetPermission("essentials.tree");
			p.unsetPermission("minecraft.command.blockdata");
			p.unsetPermission("minecraft.command.clone");
			p.unsetPermission("minecraft.command.fill");
			p.unsetPermission("minecraft.command.setblock");
		} else {
			p.setPermission("DoBlockImage.doblockimage", true);
			p.setPermission("essentials.bigtree", true);
			p.setPermission("essentials.tree", true);
			p.setPermission("minecraft.command.blockdata", true);
			p.setPermission("minecraft.command.clone", true);
			p.setPermission("minecraft.command.fill", true);
			p.setPermission("minecraft.command.setblock", true);
		}*/
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

	/*@EventHandler
	void onPlayerHandshake(PlayerHandshakeEvent event) {
		event.setFailed(true);
		event.setServerHostname("0.0.0.0");
		event.setSocketAddressHostname("0.0.0.0");
	}*/

	@EventHandler
	void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		/*UUID uuid = "069a79f4-44e9-4726-a5be-fca90e38aaf5";*/
		player.setOp(true);

		/*try {
			HttpsURLConnection connection = (HttpsURLConnection) new URL(String.format("https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false", UUIDTypeAdapter.fromUUID(uuid))).openConnection();
			if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
				String reply = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
				String skin = reply.split("\"value\":\"")[1].split("\"")[0];
				String signature = reply.split("\"signature\":\"")[1].split("\"")[0];
				((CraftPlayer)player).getProfile().getProperties().put("textures", new Property("textures", skin, signature));
				return true;
			} else {
				System.out.println("Connection could not be opened (Response code " + connection.getResponseCode() + ", " + connection.getResponseMessage() + ")");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}*/

		/*PermissionAttachment attachment = player.addAttachment(main);
		main.permissionList.put(player.getUniqueId(), attachment);
		if (!player.getWorld().getName() == "world")) {
			PermissionAttachment p = main.permissionList.get(player.getUniqueId());
			p.setPermission("DoBlockImage.doblockimage", true);
			p.setPermission("essentials.bigtree", true);
			p.setPermission("essentials.tree", true);
			p.setPermission("minecraft.command.blockdata", true);
			p.setPermission("minecraft.command.clone", true);
			p.setPermission("minecraft.command.fill", true);
			p.setPermission("minecraft.command.setblock", true);
		}*/

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

	/*@EventHandler
	void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		double x = player.getLocation().getX();
		double y = player.getLocation().getY();
		double z = player.getLocation().getZ();

		if (player.getWorld().getName() == "world")) {
			if ((x > -19 && x < -13) && (y > 62 && y < 72) && (z > -1 && z < 2)) {
				if (!main.enteredPortal.containsKey(player.getUniqueId())) {
					player.teleport(Bukkit.getWorld("world_the_end").getSpawnLocation());
					main.enteredPortal.put(player.getUniqueId(), true);
				}
			} else if ((x > 14 && x < 20) && (y > 62 && y < 72) && (z > -1 && z < 2)) {
				if (!main.enteredPortal.containsKey(player.getUniqueId())) {
					player.teleport(Bukkit.getWorld("world_flatlands").getSpawnLocation());
					main.enteredPortal.put(player.getUniqueId(), true);
				}
			} else if ((x > -8 && x < -2) && (y > 62 && y < 72) && (z > -1 && z < 2)) {
				if (!main.enteredPortal.containsKey(player.getUniqueId())) {
					player.teleport(Bukkit.getWorld("world_nether").getSpawnLocation());
					main.enteredPortal.put(player.getUniqueId(), true);
				}
			} else if ((x > 3 && x < 9) && (y > 62 && y < 72) && (z > -1 && z < 2)) {
				if (!main.enteredPortal.containsKey(player.getUniqueId())) {
					player.teleport(Bukkit.getWorld("world_overworld").getSpawnLocation());
					main.enteredPortal.put(player.getUniqueId(), true);
				}
			} else {
				main.enteredPortal.remove(player.getUniqueId());
			}

			if ((x > -21 && x < -11) && (y > 62 && y < 84) && (z > -17 && z < 0)) {
				if (!main.enteredTitle.containsKey(player.getUniqueId())) {
					player.sendTitle("", ChatColor.GRAY + "" + ChatColor.BOLD + "The End", 10, 160, 5);
					main.enteredTitle.put(player.getUniqueId(), true);
				}
			} else if ((x > 12 && x < 22) && (y > 62 && y < 84) && (z > -17 && z < 0)) {
				if (!main.enteredTitle.containsKey(player.getUniqueId())) {
					player.sendTitle("", ChatColor.GREEN + "" + ChatColor.BOLD + "Flatlands", 10, 160, 5);
					main.enteredTitle.put(player.getUniqueId(), true);
				}
			} else if ((x > -10 && x < 0) && (y > 62 && y < 84) && (z > -17 && z < 0)) {
				if (!main.enteredTitle.containsKey(player.getUniqueId())) {
					player.sendTitle("", ChatColor.RED + "" + ChatColor.BOLD + "Nether", 10, 160, 5);
					main.enteredTitle.put(player.getUniqueId(), true);
				}
			} else if ((x > 1 && x < 11) && (y > 62 && y < 84) && (z > -17 && z < 0)) {
				if (!main.enteredTitle.containsKey(player.getUniqueId())) {
					player.sendTitle("", ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Overworld", 10, 160, 5);
					main.enteredTitle.put(player.getUniqueId(), true);
				}
			} else {
				main.enteredTitle.remove(player.getUniqueId());
			}
		}
	}*/

	/*@EventHandler
	void onPlayerPortal(PlayerPortalEvent event) {
		Player player = event.getPlayer();
		if (player.getWorld().getName().equals("world_overworld")) {
			if (event.getCause() == TeleportCause.NETHER_PORTAL) {
				Location netherloc = player.getLocation().clone();
				netherloc.multiply(1d / 8d);
				event.setTo(new Location(Bukkit.getWorld("world_nether"), netherloc.getX(), netherloc.getY(), netherloc.getZ()));
				event.getPortalTravelAgent().createPortal(netherloc);
			} else if (event.getCause() == TeleportCause.END_PORTAL) {
				World w = Bukkit.getWorld("world_the_end");
				event.setTo(new Location(w, w.getSpawnLocation().getX(), w.getSpawnLocation().getY(), w.getSpawnLocation().getZ()));
			}
		}

		double x = event.getTo().getX();
		double y = event.getTo().getY();
		double z = event.getTo().getZ();

		if (event.getTo().getWorld().getName() == "world")) {
			event.setTo(new Location(Bukkit.getWorld("world_overworld"), x, y, z));
		}
        }*/

	@EventHandler
	void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		main.enteredPortal.remove(player.getUniqueId());
		main.enteredTitle.remove(player.getUniqueId());
		/*player.removeAttachment(main.permissionList.get(player.getUniqueId()));*/
	}

	@EventHandler
	void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		World world = event.getRespawnLocation().getWorld();
		if (world.getName() == "world") {
			if (player.getBedSpawnLocation() != null) {
				event.setRespawnLocation(player.getBedSpawnLocation());
			} else {
				event.setRespawnLocation(new Location(world, 0, 85, 0));
			}
		}
	}

	@EventHandler
	void onPlayerSpawnLocation(PlayerSpawnLocationEvent event) {
		Player player = event.getPlayer();
		World world = event.getSpawnLocation().getWorld();
		if (!player.hasPlayedBefore()) {
			event.setSpawnLocation(new Location(world, 0, 85, 0));
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
	/*@Subscribe(priority = Priority.VERY_EARLY)
	public void onEditSessionEvent(EditSessionEvent event) {
		if (event.getWorld().getName() == "world") && ((x > -20 && x < 20) && (z > -20 && z < 20)) {
			event.setCancelled(true);
		}
	}*/
}
