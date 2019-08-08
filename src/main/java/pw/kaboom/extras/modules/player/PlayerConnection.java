package pw.kaboom.extras;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;

import org.bukkit.block.banner.Pattern;

import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import org.bukkit.scheduler.BukkitRunnable;

import com.destroystokyo.paper.event.player.PlayerConnectionCloseEvent;

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
			final URL skinUrl = new URL("https://api.ashcon.app/mojang/v2/user/" + event.getName());
			final HttpsURLConnection premiumCheck = (HttpsURLConnection) skinUrl.openConnection();
			premiumCheck.setConnectTimeout(0);
			premiumCheck.setRequestMethod("HEAD");
			premiumCheck.setDefaultUseCaches(false);
			premiumCheck.setUseCaches(false);
			System.out.println(premiumCheck.getResponseCode());

			if (premiumCheck.getResponseCode() == HttpsURLConnection.HTTP_OK) {
				final HttpsURLConnection skinConnection = (HttpsURLConnection) skinUrl.openConnection();
				skinConnection.setConnectTimeout(0);
				skinConnection.setDefaultUseCaches(false);
				skinConnection.setUseCaches(false);
				final InputStreamReader skinStream = new InputStreamReader(skinConnection.getInputStream());
				final JsonObject response = new JsonParser().parse(skinStream).getAsJsonObject();
				final String uuid = response.get("uuid").getAsString();
				final JsonObject rawSkin = response.getAsJsonObject("textures").getAsJsonObject("raw");
				final String texture = rawSkin.get("value").getAsString();
				final String signature = rawSkin.get("signature").getAsString();
				skinStream.close();
				skinConnection.disconnect();

				final PlayerProfile textureProfile = event.getPlayerProfile();
				textureProfile.clearProperties();
				textureProfile.setProperty(new ProfileProperty("textures", texture, signature));

				main.playerProfile.put(event.getName(), textureProfile);
			}

			premiumCheck.disconnect();
		} catch (Exception exception) {
		}

		for (final World world : Bukkit.getWorlds()) {
			for (final Chunk chunk : world.getLoadedChunks()) {
				try {
					chunk.getTileEntities(false);
				} catch (Exception exception) {
					new BukkitRunnable() {
						public void run() {
							world.regenerateChunk(chunk.getX(), chunk.getZ());
						}
					}.runTask(main);
				}
			}
		}
	}

	@EventHandler
	void onPlayerConnectionClose(final PlayerConnectionCloseEvent event) {
		main.commandMillisList.remove(event.getPlayerUniqueId());
		main.interactMillisList.remove(event.getPlayerUniqueId());

		/*new BukkitRunnable() {
			public void run() {
				for (final World world : Bukkit.getWorlds()) {
					for (final Chunk chunk : world.getLoadedChunks()) {
						try {
							chunk.getTileEntities(false);
						} catch (Exception exception) {
							new BukkitRunnable() {
								public void run() {
									world.regenerateChunk(chunk.getX(), chunk.getZ());
								}
							}.runTask(main);
						}
					}
				}
			}
		}.runTaskAsynchronously(main);*/
	}

	@EventHandler
	void onPlayerJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		final int fadeIn = 10;
		final int stay = 160;
		final int fadeOut = 5;

		if (player.hasPlayedBefore()) {
			try {
				for (ItemStack item : player.getInventory().getContents()) {
					if (item.getItemMeta() instanceof BannerMeta) {
						final BannerMeta banner = (BannerMeta) item.getItemMeta();

						for (Pattern pattern : banner.getPatterns()) {
							if (pattern.getColor() == null) {
								player.getInventory().clear();
							}
						}
					}
				}
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
		if (event.getHostname().startsWith("play.flame.ga") &&
			event.getHostname().endsWith(":53950")) {
			event.disallow(Result.KICK_OTHER, "You connected to the server using an outdated server address/IP.\nPlease use the following address/IP:\n\nkaboom.pw");
			return;
		}

		final Player player = event.getPlayer();

		main.commandMillisList.put(player.getUniqueId(), System.currentTimeMillis());
		main.interactMillisList.put(player.getUniqueId(), System.currentTimeMillis());

		event.allow();
		player.setOp(true);
		try {
			player.setPlayerProfile(main.playerProfile.get(player.getName()));
		} catch (Exception exception) {
		}
		main.playerProfile.remove(player.getName());
	}
}
