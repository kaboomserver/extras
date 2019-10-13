package pw.kaboom.extras;

import java.io.InputStreamReader;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.URI;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;

import org.bukkit.block.BlockState;
import org.bukkit.block.banner.Pattern;

import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.scheduler.BukkitRunnable;

import com.destroystokyo.paper.event.player.PlayerConnectionCloseEvent;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

class PlayerConnection implements Listener {
	@EventHandler
	void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
		if (event.getName().length() > 16) {
			event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Your username can't be longer than 16 characters");
			return;
		}
	}

	@EventHandler
	void onPlayerConnectionClose(final PlayerConnectionCloseEvent event) {
		Main.commandMillisList.remove(event.getPlayerUniqueId());
		Main.interactMillisList.remove(event.getPlayerUniqueId());
		Main.usernameInProgress.remove(event.getPlayerUniqueId());
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
					if (item != null &&
						item.hasItemMeta()) {
						if (item.getItemMeta() instanceof BannerMeta) {
							final BannerMeta banner = (BannerMeta) item.getItemMeta();
	
							for (Pattern pattern : banner.getPatterns()) {
								if (pattern.getColor() == null) {
									player.getInventory().clear();
								}
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

		event.allow();
		player.setOp(true);

		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
			.uri(URI.create("https://api.ashcon.app/mojang/v2/user/" + player.getName().replace(" ", "%20")))
			.build();
		client.sendAsync(request, BodyHandlers.ofInputStream())
			.thenAccept(response -> {
			if (response.statusCode() == 200) {
				final InputStreamReader skinStream = new InputStreamReader(response.body());
				final JsonObject responseJson = new JsonParser().parse(skinStream).getAsJsonObject();
				final JsonObject rawSkin = responseJson.getAsJsonObject("textures").getAsJsonObject("raw");
				final String texture = rawSkin.get("value").getAsString();
				final String signature = rawSkin.get("signature").getAsString();
				try {
					skinStream.close();
				} catch (Exception exception) {
					System.out.println(exception);
				}

				final PlayerProfile profile = player.getPlayerProfile();
				profile.setProperty(new ProfileProperty("textures", texture, signature));

				new BukkitRunnable() {
					public void run() {
						player.setPlayerProfile(profile);
					}
				}.runTask(JavaPlugin.getPlugin(Main.class));
			}
		});
	}

	@EventHandler
	void onPlayerQuit(PlayerQuitEvent event) {
		final World world = event.getPlayer().getWorld();

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
		}
	}
}
