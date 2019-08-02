package pw.kaboom.extras;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;

import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import org.bukkit.scheduler.BukkitRunnable;

import com.destroystokyo.paper.event.player.PlayerConnectionCloseEvent;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.bukkit.event.world.WorldSaveEvent;

class PlayerConnection implements Listener {
	private Main main;
	public PlayerConnection(Main main) {
		this.main = main;
	}

	@EventHandler
	void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
		main.commandMillisList.put(event.getUniqueId(), System.currentTimeMillis());
		main.interactMillisList.put(event.getUniqueId(), System.currentTimeMillis());

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
	void onPlayerConnectionClose(final PlayerConnectionCloseEvent event) {
		main.commandMillisList.remove(event.getPlayerUniqueId());
		main.interactMillisList.remove(event.getPlayerUniqueId());

		new BukkitRunnable() {
			public void run() {
				for (final World world : Bukkit.getWorlds()) {
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
		}.runTaskAsynchronously(main);
	}

	@EventHandler
	void onPlayerJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		final int fadeIn = 10;
		final int stay = 160;
		final int fadeOut = 5;

		if (player.hasPlayedBefore() == true) {
			try {
				player.getInventory().getContents();
			} catch (Exception exception) {
				player.getInventory().clear();
			}
		}

		player.sendTitle(
			ChatColor.GRAY + "Welcome to Kaboom!",
			"Free OP • Anarchy • Creative",
			fadeIn,
			stay,
			fadeOut
		);
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
			return;
		}

		final Player player = event.getPlayer();

		event.allow();
		player.setOp(true);
		try {
			player.setPlayerProfile(main.playerProfile.get(player.getName()));
		} catch (Exception exception) {
		}
		main.playerProfile.remove(player.getName());
	}

	@EventHandler
	void onWorldSave(WorldSaveEvent event) {
		System.out.println("1");
	}
}
