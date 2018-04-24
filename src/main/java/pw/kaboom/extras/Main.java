package pw.kaboom.extras;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.material.Observer;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.WorldCreator;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Slime;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.server.ServerCommandEvent;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.util.eventbus.EventHandler.Priority;
import com.sk89q.worldedit.util.eventbus.Subscribe;

public class Main extends JavaPlugin implements Listener {
	Set<String> blacklist = new HashSet<>();
	public void onEnable() {
		saveConfig();
		this.getServer().getPluginManager().registerEvents(this, this);
		this.getCommand("end").setExecutor(new CommandEnd());
		this.getCommand("flatlands").setExecutor(new CommandFlatlands());
		this.getCommand("hub").setExecutor(new CommandHub());
		this.getCommand("nether").setExecutor(new CommandNether());
		this.getCommand("overworld").setExecutor(new CommandOverworld());
		this.getCommand("spawn").setExecutor(new CommandSpawn());
		this.getCommand("console").setExecutor(new CommandConsole());
		this.getCommand("vote").setExecutor(new CommandVote());
		this.getCommand("discord").setExecutor(new CommandDiscord());
		this.getCommand("clearchat").setExecutor(new CommandClearChat());
		this.getCommand("tellraw").setExecutor(new CommandTellraw());
		this.getCommand("prefix").setExecutor(new CommandPrefix(this));

		WorldEdit.getInstance().getEventBus().register(new WorldEditEvent());

		/*new TpsMeter().runTaskTimer(this, 0, 1);*/
		new EntityKiller().runTaskTimer(this, 0, 30);

		blacklist.add("essentials:ci");
		blacklist.add("essentials:clean");
		blacklist.add("essentials:clear");
		blacklist.add("essentials:clearinvent");
		blacklist.add("essentials:clearinventory");
		blacklist.add("essentials:eci");
		blacklist.add("essentials:eco");
		blacklist.add("essentials:economy");
		blacklist.add("essentials:eclean");
		blacklist.add("essentials:eclear");
		blacklist.add("essentials:eclearinvent");
		blacklist.add("essentials:eclearinventory");
		blacklist.add("essentials:eeco");
		blacklist.add("essentials:eeconomy");
		blacklist.add("essentials:ejail");
		blacklist.add("essentials:ekill");
		blacklist.add("essentials:emute");
		blacklist.add("essentials:esilence");
		blacklist.add("essentials:etjail");
		blacklist.add("essentials:etogglejail");
		blacklist.add("essentials:jail");
		blacklist.add("essentials:kill");
		blacklist.add("essentials:mute");
		blacklist.add("essentials:esudo");
		blacklist.add("essentials:silence");
		blacklist.add("essentials:sudo");
		blacklist.add("essentials:tjail");
		blacklist.add("essentials:togglejail");
		blacklist.add("minecraft:clear");
		blacklist.add("minecraft:execute");
		blacklist.add("minecraft:gamemode");
		blacklist.add("minecraft:gamerule");
		blacklist.add("minecraft:kill");
		blacklist.add("minecraft:tellraw");
		blacklist.add("minecraft:title");
		blacklist.add("minecraft:tp");
		blacklist.add("ci");
		blacklist.add("clean");
		blacklist.add("clear");
		blacklist.add("clearinvent");
		blacklist.add("clearinventory");
		blacklist.add("eci");
		blacklist.add("eco");
		blacklist.add("economy");
		blacklist.add("eclean");
		blacklist.add("eclear");
		blacklist.add("eclearinvent");
		blacklist.add("eclearinventory");
		blacklist.add("eeco");
		blacklist.add("eeconomy");
		blacklist.add("ejail");
		blacklist.add("ekill");
		blacklist.add("emute");
		blacklist.add("esilence");
		blacklist.add("esudo");
		blacklist.add("etjail");
		blacklist.add("etogglejail");
		blacklist.add("execute");
		blacklist.add("gamemode");
		blacklist.add("gamerule");
		blacklist.add("jail");
		blacklist.add("kill");
		blacklist.add("mute");
		blacklist.add("silence");
		blacklist.add("sudo");
		blacklist.add("title");
		blacklist.add("tjail");
		blacklist.add("togglejail");
		blacklist.add("tp");

		blacklist.add("essentials:action");
		blacklist.add("essentials:afk");
		blacklist.add("essentials:amsg");
		blacklist.add("essentials:away");
		blacklist.add("essentials:ban");
		blacklist.add("essentials:banip");
		blacklist.add("essentials:bc");
		blacklist.add("essentials:bcast");
		blacklist.add("essentials:bcastw");
		blacklist.add("essentials:bcw");
		blacklist.add("essentials:broadcast");
		blacklist.add("essentials:describe");
		blacklist.add("essentials:eaction");
		blacklist.add("essentials:eafk");
		blacklist.add("essentials:eat");
		blacklist.add("essentials:eamsg");
		blacklist.add("essentials:eaway");
		blacklist.add("essentials:eban");
		blacklist.add("essentials:ebanip");
		blacklist.add("essentials:ebc");
		blacklist.add("essentials:ebcast");
		blacklist.add("essentials:ebcastw");
		blacklist.add("essentials:ebcw");
		blacklist.add("essentials:ebroadcast");
		blacklist.add("essentials:ebroadcastworld");
		blacklist.add("essentials:edescribe");
		blacklist.add("essentials:eeat");
		blacklist.add("essentials:eemail");
		blacklist.add("essentials:efeed");
		blacklist.add("essentials:eheal");
		blacklist.add("essentials:ehelpop");
		blacklist.add("essentials:ekick");
		blacklist.add("essentials:email");
		blacklist.add("essentials:eme");
		blacklist.add("essentials:ememo");
		blacklist.add("essentials:emsg");
		blacklist.add("essentials:enuke");
		blacklist.add("essentials:epardon");
		blacklist.add("essentials:epardonip");
		blacklist.add("essentials:epm");
		blacklist.add("essentials:eshoutworld");
		blacklist.add("essentials:etell");
		blacklist.add("essentials:etempban");
		blacklist.add("essentials:etptoggle");
		blacklist.add("essentials:eunban");
		blacklist.add("essentials:eunbanip");
		blacklist.add("essentials:ev");
		blacklist.add("essentials:evanish");
		blacklist.add("essentials:ewarp");
		blacklist.add("essentials:ewarps");
		blacklist.add("essentials:ewhisper");
		blacklist.add("essentials:feed");
		blacklist.add("essentials:heal");
		blacklist.add("essentials:helpop");
		blacklist.add("essentials:kick");
		blacklist.add("essentials:m");
		blacklist.add("essentials:mail");
		blacklist.add("essentials:me");
		blacklist.add("essentials:memo");
		blacklist.add("essentials:msg");
		blacklist.add("essentials:nuke");
		blacklist.add("essentials:pardon");
		blacklist.add("essentials:pardonip");
		blacklist.add("essentials:pm");
		blacklist.add("essentials:shoutworld");
		blacklist.add("essentials:t");
		blacklist.add("essentials:tell");
		blacklist.add("essentials:tempban");
		blacklist.add("essentials:tptoggle");
		blacklist.add("essentials:unban");
		blacklist.add("essentials:unbanip");
		blacklist.add("essentials:v");
		blacklist.add("essentials:vanish");
		blacklist.add("essentials:w");
		blacklist.add("essentials:warp");
		blacklist.add("essentials:warps");
		blacklist.add("essentials:whisper");
		blacklist.add("extras:cc");
		blacklist.add("extras:clearchat");
		blacklist.add("minecraft:me");
		blacklist.add("minecraft:say");
		blacklist.add("minecraft:tell");
		blacklist.add("action");
		blacklist.add("afk");
		blacklist.add("amsg");
		blacklist.add("away");
		blacklist.add("ban");
		blacklist.add("banip");
		blacklist.add("bc");
		blacklist.add("bcast");
		blacklist.add("bcastw");
		blacklist.add("bcw");
		blacklist.add("broadcast");
		blacklist.add("cc");
		blacklist.add("clearchat");
		blacklist.add("describe");
		blacklist.add("eaction");
		blacklist.add("eafk");
		blacklist.add("eamsg");
		blacklist.add("eat");
		blacklist.add("eaway");
		blacklist.add("eban");
		blacklist.add("ebanip");
		blacklist.add("ebc");
		blacklist.add("ebcast");
		blacklist.add("ebcastw");
		blacklist.add("ebcw");
		blacklist.add("ebroadcastworld");
		blacklist.add("edescribe");
		blacklist.add("eeat");
		blacklist.add("eemail");
		blacklist.add("efeed");
		blacklist.add("eheal");
		blacklist.add("ehelpop");
		blacklist.add("ekick");
		blacklist.add("email");
		blacklist.add("eme");
		blacklist.add("ememo");
		blacklist.add("emsg");
		blacklist.add("enuke");
		blacklist.add("epardon");
		blacklist.add("epardonip");
		blacklist.add("epm");
		blacklist.add("eshoutworld");
		blacklist.add("etell");
		blacklist.add("etempban");
		blacklist.add("etptoggle");
		blacklist.add("eunban");
		blacklist.add("eunbanip");
		blacklist.add("ev");
		blacklist.add("evanish");
		blacklist.add("ewarp");
		blacklist.add("ewarps");
		blacklist.add("ewhisper");
		blacklist.add("feed");
		blacklist.add("heal");
		blacklist.add("helpop");
		blacklist.add("m");
		blacklist.add("mail");
		blacklist.add("me");
		blacklist.add("memo");
		blacklist.add("msg");
		blacklist.add("nuke");
		blacklist.add("paper:paper");
		blacklist.add("paper");
		blacklist.add("pardon");
		blacklist.add("pardonip");
		blacklist.add("pm");
		blacklist.add("shoutworld");
		blacklist.add("say");
		blacklist.add("spigot:spigot");
		blacklist.add("spigot");
		blacklist.add("t");
		blacklist.add("tell");
		blacklist.add("tempban");
		blacklist.add("tptoggle");
		blacklist.add("unban");
		blacklist.add("unbanip");
		blacklist.add("v");
		blacklist.add("vanish");
		blacklist.add("w");
		blacklist.add("warp");
		blacklist.add("warps");
		blacklist.add("whisper");

		getServer().createWorld(new WorldCreator("world_flatlands"));
		getServer().createWorld(new WorldCreator("world_overworld"));
	}

	public static class EntityKiller extends BukkitRunnable {
		@Override
		public void run() {
			double tps = Bukkit.getServer().getTPS()[0];
			if (tps < 14) {
				for (World world : Bukkit.getServer().getWorlds()) {
					for (Chunk chunk : world.getLoadedChunks()) {
						for (Entity e : chunk.getEntities()) {
							if (e.getType().equals(EntityType.ARROW) ||
							e.getType().equals(EntityType.ARROW) ||
							e.getType().equals(EntityType.DRAGON_FIREBALL) ||
							e.getType().equals(EntityType.DROPPED_ITEM) ||
							e.getType().equals(EntityType.EGG) ||
							e.getType().equals(EntityType.ENDER_CRYSTAL) ||
							e.getType().equals(EntityType.ENDER_PEARL) ||
							e.getType().equals(EntityType.ENDER_SIGNAL) ||
							e.getType().equals(EntityType.EXPERIENCE_ORB) ||
							e.getType().equals(EntityType.FIREBALL) ||
							e.getType().equals(EntityType.FIREWORK) ||
							e.getType().equals(EntityType.MINECART_TNT) ||
							e.getType().equals(EntityType.PRIMED_TNT) ||
							e.getType().equals(EntityType.SMALL_FIREBALL) ||
							e.getType().equals(EntityType.SNOWBALL) ||
							e.getType().equals(EntityType.SPECTRAL_ARROW) ||
							e.getType().equals(EntityType.SPLASH_POTION) ||
							e.getType().equals(EntityType.TIPPED_ARROW)) {
								e.remove();
							}
						}
					}
				}
			}
			for (World world : Bukkit.getServer().getWorlds()) {
				for (Chunk chunk : world.getLoadedChunks()) {
					for (Entity e : chunk.getEntities()) {
						if (e.getWorld().getName().equals("world")) {
							if (!e.getType().equals(EntityType.PLAYER)) {
								e.remove();
							}
						}
					}
				}
			}
		}
	}

	/* Hub Protection */

	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent event) {
		Block block = event.getBlock();
		if (block.getWorld().getName().equals("world")) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockFallEvent(EntityChangeBlockEvent event) {
		Block block = event.getBlock();
		if (block.getWorld().getName().equals("world")) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockPlaceEvent(BlockPlaceEvent event) {
		Block block = event.getBlock();
		if (block.getWorld().getName().equals("world")) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockExplode(BlockExplodeEvent event) {
		Block block = event.getBlock();
		if (block.getWorld().getName().equals("world")) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBucketEmpty(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();
		if (player.getWorld().getName().equals("world")) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPotionSplash(PotionSplashEvent event) {
		Entity entity = event.getEntity();
		if (entity.getWorld().getName().equals("world")) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onAreaEffectCloudEffect(LingeringPotionSplashEvent event) {
		Entity entity = event.getEntity();
		if (entity.getWorld().getName().equals("world")) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent event) {
		Entity entity = event.getEntity();
		Location entityLocation = event.getLocation();

		int onChunk = 0;
		for (Entity chunkEntity : entityLocation.getWorld().getEntities()) {
			if (entityLocation.getChunk().equals(chunkEntity.getLocation().getChunk())) {
				onChunk++;
			}
		}

		if (onChunk >= 50) {
			event.setCancelled(true);
		}

		if (entity.getType().equals(EntityType.MAGMA_CUBE)) {
			MagmaCube magmacube = (MagmaCube) event.getEntity();
			if (magmacube.getSize() > 100) {
				magmacube.setSize(100);
			}
		}
		
		if (entity.getType().equals(EntityType.SLIME)) {
			Slime slime = (Slime) event.getEntity();
			if (slime.getSize() > 100) {
				slime.setSize(100);
			}
		}
	}

	@EventHandler
	public void onHangingSpawn(HangingPlaceEvent event) {
		Entity entity = event.getEntity();
		if (entity.getWorld().getName().equals("world")) {
			event.setCancelled(true);
		}
	}

	/* Other */

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		String message = event.getMessage().substring(0, Math.min(256, event.getMessage().length()));
		if (getConfig().getString(player.getName().toString()) != null) {
			String prefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString(player.getName().toString()));
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
	public void onPrimeExplode(ExplosionPrimeEvent event) {
		Entity entity = event.getEntity();
		if (entity.getWorld().getName().equals("world")) {
			event.setCancelled(true);
		}

		if (event.getRadius() > 20) {
			event.setRadius(20);
		}

		double tps = Bukkit.getServer().getTPS()[0];
		if (tps < 14) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onExplode(EntityExplodeEvent event) {
		/*Iterator<Block> iter = event.blockList().iterator();
		while (iter.hasNext()) {
			Block block = iter.next();
			double x = block.getX();
			double y = block.getY();
			double z = block.getZ();
			double spawnx = block.getWorld().getSpawnLocation().getX();
			double spawnz = block.getWorld().getSpawnLocation().getZ();
			if (block.getWorld().getName().equals("world")) {
				if ((x > spawnx - 50 && x < spawnx + 50) && (y > 0 && y < 9999) && (z > spawnz - 50 && z < spawnz + 50)) {
					iter.remove();
				}
			}
		}*/
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		player.sendTitle(ChatColor.GRAY + "Kaboom.pw", "Free OP • Anarchy • Creative", 10, 160, 5);
		player.setOp(true);
	}

	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		String world = player.getLocation().getWorld().getName();
		if (world.equals("world_the_end")) {
			player.sendTitle(ChatColor.GRAY + "The End", "Return to the hub by typing /hub in chat", 10, 160, 5);
		} else if (world.equals("world_flatlands")) {
			player.sendTitle(ChatColor.GREEN + "Flatlands", "Return to the hub by typing /hub in chat", 10, 160, 5);
		} else if (world.equals("world_nether")) {
			player.sendTitle(ChatColor.RED + "Nether", "Return to the hub by typing /hub in chat", 10, 160, 5);
		} else if (world.equals("world_overworld")) {
			player.sendTitle(ChatColor.DARK_GREEN + "Overworld", "Return to the hub by typing /hub in chat", 10, 160, 5);
		}

	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		World world = event.getRespawnLocation().getWorld();
		if (world.getName().equals("world")) {
			if (player.getBedSpawnLocation() != null) {
				event.setRespawnLocation(player.getBedSpawnLocation());
			} else {
				event.setRespawnLocation(new Location(world, 0, 63, -22));
			}
		}
	}

	@EventHandler
	public void onPlayerSpawn(PlayerSpawnLocationEvent event) {
		Player player = event.getPlayer();
		if (!player.hasPlayedBefore()) {
			event.setSpawnLocation(new Location(event.getSpawnLocation().getWorld(), 0, 63, -22));
		}
	}

	HashMap<Player, Boolean> enteredPortal = new HashMap<Player, Boolean>();
	HashMap<Player, Boolean> enteredTitle = new HashMap<Player, Boolean>();

	@EventHandler
	public void PlayerMoveEvent(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		double x = player.getLocation().getX();
		double y = player.getLocation().getY();
		double z = player.getLocation().getZ();
		if ((player.getWorld().getName().equals("world"))) {
			enteredPortal.put(player, false);
			if ((x > -19 && x < -13) && (y > 62 && y < 72) && (z > -1 && z < 2)) {
				if (!enteredPortal.get(player)) {
					player.teleport(Bukkit.getWorld("world_the_end").getSpawnLocation());
					enteredPortal.put(player, true);
				}
			} else if ((x > 14 && x < 20) && (y > 62 && y < 72) && (z > -1 && z < 2)) {
				if (!enteredPortal.get(player)) {
					player.teleport(Bukkit.getWorld("world_flatlands").getSpawnLocation());
					enteredPortal.put(player, true);
				}
			} else if ((x > -8 && x < -2) && (y > 62 && y < 72) && (z > -1 && z < 2)) {
				if (!enteredPortal.get(player)) {
					player.teleport(Bukkit.getWorld("world_nether").getSpawnLocation());
					enteredPortal.put(player, true);
				}
			} else if ((x > 3 && x < 9) && (y > 62 && y < 72) && (z > -1 && z < 2)) {
				if (!enteredPortal.get(player)) {
					player.teleport(Bukkit.getWorld("world_overworld").getSpawnLocation());
					enteredPortal.put(player, true);
				}
			} else {
				enteredPortal.put(player, false);
			}

			if ((x > -21 && x < -11) && (y > 62 && y < 84) && (z > -17 && z < 0)) {
				if (!enteredTitle.get(player)) {
					player.sendTitle("", ChatColor.GRAY + "" + ChatColor.BOLD + "The End", 10, 160, 5);
					enteredTitle.put(player, true);
				}
			} else if ((x > 12 && x < 22) && (y > 62 && y < 84) && (z > -17 && z < 0)) {
				if (!enteredTitle.get(player)) {
					player.sendTitle("", ChatColor.GREEN + "" + ChatColor.BOLD + "Flatlands", 10, 160, 5);
					enteredTitle.put(player, true);
				}
			} else if ((x > -10 && x < 0) && (y > 62 && y < 84) && (z > -17 && z < 0)) {
				if (!enteredTitle.get(player)) {
					player.sendTitle("", ChatColor.RED + "" + ChatColor.BOLD + "Nether", 10, 160, 5);
					enteredTitle.put(player, true);
				}
			} else if ((x > 1 && x < 11) && (y > 62 && y < 84) && (z > -17 && z < 0)) {
				if (!enteredTitle.get(player)) {
					player.sendTitle("", ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Overworld", 10, 160, 5);
					enteredTitle.put(player, true);
				}
			} else {
				enteredTitle.put(player, false);
			}
		}
	}

	@EventHandler
	public void onPortal(PlayerPortalEvent event) {
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

		if (event.getTo().getWorld().getName().equals("world")) {
			event.setTo(new Location(Bukkit.getWorld("world_overworld"), x, y, z));
		}
        }

	@EventHandler
	public void onPhysics(BlockPhysicsEvent event) {
		/*double tps = Bukkit.getServer().getTPS()[0];
		if (tps < 12) {
			if (event.getBlock().getType() == Material.OBSERVER) {
				Observer obs = (Observer) event.getBlock().getState().getData();
				if (obs.isPowered()) {
					event.getBlock().setType(Material.OBSERVER, true);
				}
			}
			event.setCancelled(true);
		}*/
		Block block = event.getBlock();
		if (block.getWorld().getName().equals("world")) {
			event.setCancelled(true);
		}
		if (block.getType() == Material.SOIL) {
			event.setCancelled(true);
		}
        }

	@EventHandler
	public void onRedstone(BlockRedstoneEvent event) {
		double tps = Bukkit.getServer().getTPS()[0];
		if (tps < 14) {
			event.setNewCurrent(0);
		}
		Block block = event.getBlock();
		if (block.getWorld().getName().equals("world")) {
			event.setNewCurrent(0);
		}
        }

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		if (event.getPlayer().getWorld().getName().equals("world")) {
			if ((event.getMessage().toLowerCase().startsWith("/minecraft:blockdata")) ||
			(event.getMessage().toLowerCase().startsWith("/minecraft:clone")) ||
			(event.getMessage().toLowerCase().startsWith("/minecraft:fill")) ||
			(event.getMessage().toLowerCase().startsWith("/minecraft:setblock")) ||
			(event.getMessage().toLowerCase().startsWith("/bigtree")) ||
			(event.getMessage().toLowerCase().startsWith("/blockdata")) ||
			(event.getMessage().toLowerCase().startsWith("/clone")) ||
			(event.getMessage().toLowerCase().startsWith("/ebigtree")) ||
			(event.getMessage().toLowerCase().startsWith("/essentials:bigtree")) ||
			(event.getMessage().toLowerCase().startsWith("/essentials:ebigtree")) ||
			(event.getMessage().toLowerCase().startsWith("/essentials:etree")) ||
			(event.getMessage().toLowerCase().startsWith("/essentials:tree")) ||
			(event.getMessage().toLowerCase().startsWith("/etree")) ||
			(event.getMessage().toLowerCase().startsWith("/fill")) ||
			(event.getMessage().toLowerCase().startsWith("/setblock")) ||
			(event.getMessage().toLowerCase().startsWith("/tree"))) {
				event.setCancelled(true);
				event.getPlayer().sendMessage("Please move to another world to build");
			}
		}
		if (event.getPlayer().hasPermission("worldedit.schematics.delete")) {
			if ((event.getMessage().toLowerCase().startsWith("//schematic delete")) ||
			(event.getMessage().toLowerCase().startsWith("//schem delete")) ||
			(event.getMessage().toLowerCase().startsWith("/schematic delete")) ||
			(event.getMessage().toLowerCase().startsWith("/schem delete")) ||
			(event.getMessage().toLowerCase().startsWith("/worldedit:/schematic delete")) ||
			(event.getMessage().toLowerCase().startsWith("/worldedit:/schem delete")) ||
			(event.getMessage().toLowerCase().startsWith("/worldedit:schematic delete")) ||
			(event.getMessage().toLowerCase().startsWith("/worldedit:schem delete"))) {
				event.setCancelled(true);
			}
		}
		if (event.getMessage().toLowerCase().startsWith("/essentials:esudo") ||
		event.getMessage().toLowerCase().startsWith("/essentials:sudo") ||
		event.getMessage().toLowerCase().startsWith("/esudo") ||
		event.getMessage().toLowerCase().startsWith("/sudo")) {
			if (event.getMessage().toLowerCase().contains("essentials:kill") ||
			event.getMessage().toLowerCase().contains("essentials:ekill") ||
			event.getMessage().toLowerCase().contains("ekill") ||
			event.getMessage().toLowerCase().contains("kill") ||
			event.getMessage().toLowerCase().contains("minecraft:kill") ||
			event.getMessage().toLowerCase().contains("essentials:suicide") ||
			event.getMessage().toLowerCase().contains("essentials:esuicide") ||
			event.getMessage().toLowerCase().contains("esuicide") ||
			event.getMessage().toLowerCase().contains("suicide")) {
				event.setCancelled(true);
			}
		}
		/*if (event.getMessage().toLowerCase().startsWith("/minecraft:stop") ||
		event.getMessage().toLowerCase().startsWith("/stop")) {
			event.setCancelled(true);
			Command.broadcastCommandMessage(event.getPlayer(), "Stopping the server..");
		}
		if (event.getMessage().toLowerCase().startsWith("/restart") ||
		event.getMessage().toLowerCase().startsWith("/spigot:restart")) {
			event.setCancelled(true);
			Command.broadcastCommandMessage(event.getPlayer(), "Restarting the server..");
		}
		if (event.getMessage().toLowerCase().startsWith("/spigot reload") ||
		event.getMessage().toLowerCase().startsWith("/spigot:spigot reload")) {
			event.setCancelled(true);
			Command.broadcastCommandMessage(event.getPlayer(), ChatColor.RED + "Please note that this command is not supported and may cause issues when using some plugins.");
			Command.broadcastCommandMessage(event.getPlayer(), ChatColor.RED + "If you encounter any issues please use the /stop command to restart your server.");
			Command.broadcastCommandMessage(event.getPlayer(), ChatColor.GREEN + "Reload complete.");
		}*/
		event.setMessage(event.getMessage().substring(0, Math.min(256, event.getMessage().length())));
	}

	@EventHandler
	public void onServerCommand(ServerCommandEvent event) {
		String arr[] = event.getCommand().split(" ", 2);
		if (blacklist.contains(arr[0])) {
			event.setCancelled(true);
		}
		if ((event.getCommand().toLowerCase().startsWith("//schematic delete")) ||
		(event.getCommand().toLowerCase().startsWith("//schem delete")) ||
		(event.getCommand().toLowerCase().startsWith("/schematic delete")) ||
		(event.getCommand().toLowerCase().startsWith("/schem delete")) ||
		(event.getCommand().toLowerCase().startsWith("/worldedit:/schematic delete")) ||
		(event.getCommand().toLowerCase().startsWith("/worldedit:/schem delete")) ||
		(event.getCommand().toLowerCase().startsWith("/worldedit:schematic delete")) ||
		(event.getCommand().toLowerCase().startsWith("/worldedit:schem delete"))) {
			event.setCancelled(true);
		}
		if (event.getCommand().toLowerCase().startsWith("/essentials:esudo") ||
		event.getCommand().toLowerCase().startsWith("/essentials:sudo") ||
		event.getCommand().toLowerCase().startsWith("/esudo") ||
		event.getCommand().toLowerCase().startsWith("/sudo")) {
			if (event.getCommand().toLowerCase().contains("essentials:kill") ||
			event.getCommand().toLowerCase().contains("essentials:ekill") ||
			event.getCommand().toLowerCase().contains("ekill") ||
			event.getCommand().toLowerCase().contains("kill") ||
			event.getCommand().toLowerCase().contains("minecraft:kill") ||
			event.getCommand().toLowerCase().contains("essentials:suicide") ||
			event.getCommand().toLowerCase().contains("essentials:esuicide") ||
			event.getCommand().toLowerCase().contains("esuicide") ||
			event.getCommand().toLowerCase().contains("suicide")) {
				event.setCancelled(true);
			}
		}
		double tps = Bukkit.getServer().getTPS()[0];
		if (event.getSender() instanceof BlockCommandSender) {
			if (tps < 14) {
				event.setCancelled(true);
			}
		} else {
			Player player = (Player) event.getSender();
			if (player.getLocation().getWorld().getName().equals("world")) {
				if ((event.getCommand().toLowerCase().startsWith("/minecraft:blockdata")) ||
				(event.getCommand().toLowerCase().startsWith("/minecraft:clone")) ||
				(event.getCommand().toLowerCase().startsWith("/minecraft:fill")) ||
				(event.getCommand().toLowerCase().startsWith("/minecraft:setblock")) ||
				(event.getCommand().toLowerCase().startsWith("/bigtree")) ||
				(event.getCommand().toLowerCase().startsWith("/blockdata")) ||
				(event.getCommand().toLowerCase().startsWith("/clone")) ||
				(event.getCommand().toLowerCase().startsWith("/ebigtree")) ||
				(event.getCommand().toLowerCase().startsWith("/essentials:bigtree")) ||
				(event.getCommand().toLowerCase().startsWith("/essentials:ebigtree")) ||
				(event.getCommand().toLowerCase().startsWith("/essentials:etree")) ||
				(event.getCommand().toLowerCase().startsWith("/essentials:tree")) ||
				(event.getCommand().toLowerCase().startsWith("/etree")) ||
				(event.getCommand().toLowerCase().startsWith("/fill")) ||
				(event.getCommand().toLowerCase().startsWith("/setblock")) ||
				(event.getCommand().toLowerCase().startsWith("/tree"))) {
					event.setCancelled(true);
					event.getSender().sendMessage("Please move to another world to build");
				}
			}
			/*if (event.getCommand().toLowerCase().startsWith("/minecraft:stop") ||
			event.getCommand().toLowerCase().startsWith("/stop")) {
				event.setCancelled(true);
				Command.broadcastCommandMessage(event.getSender(), "Stopping the server..");
			}
			if (event.getCommand().toLowerCase().startsWith("/restart") ||
			event.getCommand().toLowerCase().startsWith("/spigot:restart")) {
				event.setCancelled(true);
				Command.broadcastCommandMessage(event.getSender(), "Restarting the server..");
			}
			if (event.getCommand().toLowerCase().startsWith("/spigot reload") ||
			event.getCommand().toLowerCase().startsWith("/spigot:spigot reload")) {
				event.setCancelled(true);
				Command.broadcastCommandMessage(event.getSender(), ChatColor.RED + "Please note that this command is not supported and may cause issues when using some plugins.");
				Command.broadcastCommandMessage(event.getSender(), ChatColor.RED + "If you encounter any issues please use the /stop command to restart your server.");
				Command.broadcastCommandMessage(event.getSender(), ChatColor.GREEN + "Reload complete.");
			}*/
		}
	}

	@EventHandler
	public void onKick(PlayerKickEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onLogin(PlayerLoginEvent event) {
		event.allow();
	}

	@EventHandler
	public void onServerListPing(ServerListPingEvent event) {
		event.setMaxPlayers(event.getNumPlayers() + 1);
	}
}

class WorldEditEvent {
	@Subscribe(priority = Priority.VERY_EARLY)
	public void onEditSessionEvent(EditSessionEvent event) {
		if (event.getWorld().getName().equals("world")) {
			event.setCancelled(true);
		}
	}
}

class CommandSpawn implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player)sender;
		World world = player.getLocation().getWorld();
		if (world.getName().equals("world")) {
			player.teleport(new Location(world, 0, 63, -22));
		} else {
			player.teleport(player.getWorld().getSpawnLocation());
		}
		player.sendMessage("Successfully moved to the spawn");
		return true;
	}
}

class CommandEnd implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player)sender;
		player.teleport(Bukkit.getWorld("world_the_end").getSpawnLocation());
		player.sendMessage("Successfully moved to the End");
		return true;
	}
}

class CommandFlatlands implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player)sender;
		player.teleport(Bukkit.getWorld("world_flatlands").getSpawnLocation());
		player.sendMessage("Successfully moved to the Flatlands");
		return true;
	}
}

class CommandHub implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player)sender;
		player.teleport(new Location(Bukkit.getWorld("world"), 0, 63, -22));
		player.sendMessage("Successfully moved to the Hub");
		return true;
	}
}

class CommandNether implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player)sender;
		player.teleport(Bukkit.getWorld("world_nether").getSpawnLocation());
		player.sendMessage("Successfully moved to the Nether");
		return true;
	}
}

class CommandOverworld implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player)sender;
		player.teleport(Bukkit.getWorld("world_overworld").getSpawnLocation());
		player.sendMessage("Successfully moved to the Overworld");
		return true;
	}
}

class CommandConsole implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player)sender;
		if (args.length == 0) {
			player.sendMessage(ChatColor.RED + "Usage: /" + label + " <message ..>");
		} else {
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "minecraft:say " + ChatColor.translateAlternateColorCodes('&', String.join(" ", args)));
		}
		return true;
	}
}

class CommandVote implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player)sender;
		player.sendMessage("Feel free to vote for the server to help it grow");
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "minecraft:tellraw " + player.getName() + " [\"\",{\"text\":\"[1] \",\"color\":\"dark_green\"},{\"text\":\"TopG.org\",\"color\":\"green\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://topg.org/Minecraft/in-414108\"}}]");
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "minecraft:tellraw " + player.getName() + " [\"\",{\"text\":\"[2] \",\"color\":\"dark_green\"},{\"text\":\"MinecraftServers.org\",\"color\":\"green\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"http://minecraftservers.org/vote/153833\"}}]");
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "minecraft:tellraw " + player.getName() + " [\"\",{\"text\":\"[3] \",\"color\":\"dark_green\"},{\"text\":\"Minecraft Multiplayer\",\"color\":\"green\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"http://minecraft-mp.com/server/155223/vote/\"}}]");
		return true;
	}
}

class CommandDiscord implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player)sender;
		player.sendMessage("Join the Kaboom.pw Discord server to chat with other users");
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "minecraft:tellraw " + player.getName() + " [\"\",{\"text\":\"https://discord.gg/UMGbMsU\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://discord.gg/UMGbMsU\"}}]");
		return true;
	}
}

class CommandClearChat implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		for (int i = 0; i < 100; ++i) {
			Bukkit.broadcastMessage("");
		}
		Bukkit.broadcastMessage(ChatColor.DARK_GREEN + "The chat has been cleared");
		return true;
	}
}

class CommandTellraw implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player)sender;
		if (args.length == 0) {
			player.sendMessage(ChatColor.RED + "Usage: /" + label + " <message ..>");
		} else {
			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', String.join(" ", args)));
		}
		return true;
	}
}

class CommandPrefix implements CommandExecutor {
	private Main main;
	public CommandPrefix(Main main) {
		this.main = main;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player)sender;
		if (args.length == 0) {
			player.sendMessage(ChatColor.RED + "Usage: /" + label + " <prefix|off>");
		} else if (args[0].equalsIgnoreCase("off")) {
			main.getConfig().set(player.getName().toString(), null);
			main.saveConfig();
			player.sendMessage("You no longer have a tag");
		} else {
			main.getConfig().set(player.getName().toString(), String.join(" ", args));
			main.saveConfig();
			player.sendMessage("You now have the tag: " + ChatColor.translateAlternateColorCodes('&', String.join(" ", args)));
		}
		return true;
	}
}
