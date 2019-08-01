package pw.kaboom.extras;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import org.bukkit.ChatColor;

import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

class PlayerConnection implements Listener {
	private Main main;
	public PlayerConnection(Main main) {
		this.main = main;
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
		/*if (!(event.getHostname().startsWith("play.kaboom.pw") &&
			event.getHostname().endsWith(":53950"))) {
			event.disallow(Result.KICK_OTHER, "You connected to the server using an outdated server address/IP.\nPlease use the following address/IP:\n\nkaboom.pw");
		} else {*/
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
		/*}*/
	}

	@EventHandler
	void onPlayerQuit(PlayerQuitEvent event) {
		final Player player = event.getPlayer();

		main.commandMillisList.remove(player.getUniqueId());
		main.interactMillisList.remove(player.getUniqueId());
	}
}
