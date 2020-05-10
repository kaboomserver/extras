package pw.kaboom.extras.modules.player;

import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.destroystokyo.paper.event.profile.PreLookupProfileEvent;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.common.base.Charsets;

import pw.kaboom.extras.Main;

public final class PlayerConnection implements Listener {
	@EventHandler
	void onAsyncPlayerPreLogin(final AsyncPlayerPreLoginEvent event) {
		if (Bukkit.getPlayer(event.getName()) != null
				&& Bukkit.getPlayer(event.getName()).isOnline()) {
			event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "A player with that username is already logged in");
		}

		/*try {
			final PlayerProfile profile = event.getPlayerProfile();

			UUID offlineUUID = UUID.nameUUIDFromBytes(("OfflinePlayer:" + event.getName()).getBytes(Charsets.UTF_8));

			profile.setId(offlineUUID);

			SkinDownloader skinDownloader = new SkinDownloader();
			skinDownloader.fillJoinProfile(profile, event.getName(), event.getUniqueId());
		} catch (Exception ignored) {
		}*/
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
		}

		if (!JavaPlugin.getPlugin(Main.class).getConfig().getBoolean("enableJoinRestrictions")) {
			event.allow();
		}

		if (Result.KICK_FULL.equals(event.getResult())
				&& JavaPlugin.getPlugin(Main.class).getConfig().getBoolean("allowJoinOnFullServer")) {
			event.allow();
		}

		final Player player = event.getPlayer();

		if (JavaPlugin.getPlugin(Main.class).getConfig().getBoolean("opOnJoin")
				&& !player.isOp()) {
			player.setOp(true);
		}

		/*try {
			player.setPlayerProfile(SkinDownloader.getProfile(player.getUniqueId()));
			SkinDownloader.removeProfile(player.getUniqueId());
		} catch (Exception ignored) {
		}*/
	}

	@EventHandler
	void onPlayerQuit(final PlayerQuitEvent event) {
		PlayerCommand.getCommandMillisList().remove(event.getPlayer().getUniqueId());
		//PlayerInteract.interactMillisList.remove(event.getPlayer().getUniqueId());
	}

	@EventHandler
	void onPreLookupProfile(final PreLookupProfileEvent event) {
		// Disable Mojang API calls, we don't need them
		UUID offlineUUID = UUID.nameUUIDFromBytes(("OfflinePlayer:" + event.getName()).getBytes(Charsets.UTF_8));
		event.setUUID(offlineUUID);

		event.setProfileProperties(new HashSet<ProfileProperty>());
	}
}
