package pw.kaboom.extras.modules.player;

import com.destroystokyo.paper.event.profile.PreLookupProfileEvent;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.google.common.base.Charsets;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.plugin.java.JavaPlugin;

import pw.kaboom.extras.Main;
import pw.kaboom.extras.helpers.SkinDownloader;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public final class PlayerConnection implements Listener {
	private long connectionMillis;

	private final File configFile = new File("spigot.yml");
	private final FileConfiguration spigotConfig = YamlConfiguration.loadConfiguration(configFile);
	private final ConfigurationSection configSection = spigotConfig.getConfigurationSection("commands");

	/*public static boolean isIllegalItem(ItemStack item) {
		//try {
		if (item != null &&
				item.getItemMeta() != null) {
			System.out.println("itit");
			System.out.println(item.getItemMeta().getDisplayName());
		}
		/*} catch (Exception | StackOverflowError exception) {
			System.out.println("yes");
			return true;
		}
		return false;
	}*/

	@EventHandler
	void onAsyncPlayerPreLogin(final AsyncPlayerPreLoginEvent event) {
		if (event.getName().length() > 16) {
			event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Your username can't be longer than 16 characters");
		} else {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (event.getName().equals(player.getName())) {
					event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "A player with that username is already logged in");
				}
			}

			try {
				final PlayerProfile profile = event.getPlayerProfile();

				SkinDownloader skinDownloader = new SkinDownloader();
				skinDownloader.fillJoinProfile(profile, event.getName(), event.getUniqueId());
			} catch (Exception ignored) {
			}
		}
	}

	/*@EventHandler
	void onInventoryClose(InventoryCloseEvent event) {
		for (ItemStack item : event.getInventory().getContents()) {
			if (isIllegalItem(item)) {
				event.getInventory().clear();
			}
		}
	}*/

	/*@EventHandler
	void onPlayerCommandSend(final PlayerCommandSendEvent event) {
		if (event.getPlayer().isOnline()) {
			event.getCommands().clear();
		}
	}*/

	@EventHandler
	void onPlayerCommandSend2(final PlayerStatisticIncrementEvent event) {
		//if (event.getPlayer().isOnline()) {
		event.setCancelled(true);
		//}
	}

	@EventHandler
	void onPlayerJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		final String title = JavaPlugin.getPlugin(Main.class).getConfig().getString("playerJoinTitle");
		final String subtitle = JavaPlugin.getPlugin(Main.class).getConfig().getString("playerJoinSubtitle");
		final int fadeIn = 10;
		final int stay = 160;
		final int fadeOut = 5;

		if (title != null
				|| subtitle != null) {
				player.sendTitle(
				title,
				subtitle,
				fadeIn,
				stay,
				fadeOut
			);
		}
	}

	@EventHandler
	void onPlayerKick(final PlayerKickEvent event) {
		if (!JavaPlugin.getPlugin(Main.class).getConfig().getBoolean("enableKick")) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	void onPlayerLogin(final PlayerLoginEvent event) {
		if (event.getHostname().startsWith("play.flame.ga")
				&& event.getHostname().endsWith(":25565")) {
			event.disallow(Result.KICK_OTHER, "You connected to the server using an outdated server address/IP.\nPlease use the following address/IP:\n\nkaboom.pw");
			return;
		} else if (System.currentTimeMillis() - connectionMillis < 2000) {
			if (!configSection.getString("tab-complete").equals("-1")) {
				configSection.set("tab-complete", -1);
				try {
					spigotConfig.save(configFile);

					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spigot reload");
				} catch (IOException ignored) {
				}
			}
		} else if (configSection.getString("tab-complete").equals("-1")) {
			configSection.set("tab-complete", 0);
			try {
				spigotConfig.save(configFile);

				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spigot reload");
			} catch (IOException ignored) {
			}
		}

		connectionMillis = System.currentTimeMillis();

		if (!JavaPlugin.getPlugin(Main.class).getConfig().getBoolean("enableJoinRestrictions")) {
			event.allow();
		}

		if (Result.KICK_FULL.equals(event.getResult())
				&& JavaPlugin.getPlugin(Main.class).getConfig().getBoolean("allowJoinOnFullServer")) {
			event.allow();
		}

		final Player player = event.getPlayer();

		try {
			player.setPlayerProfile(SkinDownloader.getProfile(player.getUniqueId()));
			SkinDownloader.removeProfile(player.getUniqueId());
		} catch (Exception ignored) {
		}

		if (JavaPlugin.getPlugin(Main.class).getConfig().getBoolean("opOnJoin")
				&& !player.isOp()) {
			player.setOp(true);
		}
	}

	@EventHandler
	void onPlayerQuit(PlayerQuitEvent event) {
		PlayerCommand.commandMillisList.remove(event.getPlayer().getUniqueId());
		PlayerInteract.interactMillisList.remove(event.getPlayer().getUniqueId());
		SkinDownloader.skinInProgress.remove(event.getPlayer().getUniqueId());

		/*final World world = event.getPlayer().getWorld();

		for (final Chunk chunk : world.getLoadedChunks()) {
			try {
				int data = 0;
				for (BlockState block : chunk.getTileEntities()) {
					data = data + block.getBlockData().getAsString().length();
				}

				if (data > 1285579) {
					world.regenerateChunk(chunk.getX(), chunk.getZ());
				}
			} catch (Exception exception) {
				world.regenerateChunk(chunk.getX(), chunk.getZ());
			}
		}*/
	}

	@EventHandler
	void onPreLookupProfile(final PreLookupProfileEvent event) {
		UUID offlineUUID = UUID.nameUUIDFromBytes(("OfflinePlayer:" + event.getName()).getBytes(Charsets.UTF_8));
		event.setUUID(offlineUUID);
	}
}
