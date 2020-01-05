package pw.kaboom.extras.modules.player;

import com.destroystokyo.paper.event.profile.PreLookupProfileEvent;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.common.base.Charsets;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.scheduler.BukkitRunnable;
import pw.kaboom.extras.Main;
import pw.kaboom.extras.helpers.SkinDownloader;

import javax.net.ssl.HttpsURLConnection;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

public final class PlayerConnection implements Listener {
	private PlayerProfile profile;
	private String texture;
	private String signature;

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
				if (player.getName().equals(event.getName())) {
					event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "A player with that username is already logged in");
				}
			}

			try {
				profile = event.getPlayerProfile();
				profile.clearProperties();

				fetchSkinData(event.getName());
				profile.setProperty(new ProfileProperty("textures", texture, signature));
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

	@EventHandler
	void onPlayerCommandSend(final PlayerCommandSendEvent event) {
		if (event.getPlayer().isOnline()) {
			event.getCommands().clear();
		}
	}

	@EventHandler
	void onPlayerCommandSend2(final PreLookupProfileEvent event) {
		UUID offlineUUID = UUID.nameUUIDFromBytes(("OfflinePlayer:" + event.getName()).getBytes(Charsets.UTF_8));
		event.setUUID(offlineUUID);
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

		if (event.getResult() == Result.KICK_FULL
				&& JavaPlugin.getPlugin(Main.class).getConfig().getBoolean("allowJoinOnFullServer")) {
			event.allow();
		}

		final Player player = event.getPlayer();

		try {
			player.setPlayerProfile(profile);
			profile = null;
		} catch (Exception ignored) {
		}

		if (JavaPlugin.getPlugin(Main.class).getConfig().getBoolean("opOnJoin")
				&& !player.isOp()) {
			player.setOp(true);
		}
	}

	@SuppressWarnings("deprecation")
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

	private void fetchSkinData(final String playerName) throws IOException {
		final URL skinUrl = new URL("https://api.ashcon.app/mojang/v2/user/" + playerName);
		HttpsURLConnection skinConnection = (HttpsURLConnection) skinUrl.openConnection();
		skinConnection.setConnectTimeout(0);

		InputStreamReader skinStream = new InputStreamReader(skinConnection.getInputStream());
		final JsonObject responseJson = new JsonParser().parse(skinStream).getAsJsonObject();
		final JsonObject rawSkin = responseJson.getAsJsonObject("textures").getAsJsonObject("raw");
		texture = rawSkin.get("value").getAsString();
		signature = rawSkin.get("signature").getAsString();

		skinStream.close();
		skinConnection.disconnect();
	}
}
