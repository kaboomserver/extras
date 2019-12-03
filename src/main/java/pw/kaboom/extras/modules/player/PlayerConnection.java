package pw.kaboom.extras;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;

import org.bukkit.block.BlockState;
import org.bukkit.block.banner.Pattern;

import org.bukkit.entity.Player;

import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import org.bukkit.plugin.java.JavaPlugin;

import com.destroystokyo.paper.event.player.PlayerConnectionCloseEvent;

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
		final String title = JavaPlugin.getPlugin(Main.class).getConfig().getString("playerJoinTitle");
		final String subtitle = JavaPlugin.getPlugin(Main.class).getConfig().getString("playerJoinSubtitle");
		final int fadeIn = 10;
		final int stay = 160;
		final int fadeOut = 5;

		if (title != null ||
			subtitle != null) {
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
	void onPlayerKick(PlayerKickEvent event) {
		if (!JavaPlugin.getPlugin(Main.class).getConfig().getBoolean("enableKick")) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	void onPlayerLogin(PlayerLoginEvent event) {
		if (event.getHostname().startsWith("play.flame.ga") &&
			event.getHostname().endsWith(":25565")) {
			event.disallow(Result.KICK_OTHER, "You connected to the server using an outdated server address/IP.\nPlease use the following address/IP:\n\nkaboom.pw");
			return;
		}

		final Player player = event.getPlayer();

		if (!JavaPlugin.getPlugin(Main.class).getConfig().getBoolean("enableJoinRestrictions")) {
			event.allow();
		}
		
		if (event.getResult() == Result.KICK_FULL &&
			JavaPlugin.getPlugin(Main.class).getConfig().getBoolean("allowJoinOnFullServer")) {
			event.allow();
		}
		
		if (JavaPlugin.getPlugin(Main.class).getConfig().getBoolean("opOnJoin")) {
			player.setOp(true);
		}

		final boolean shouldChangeUsername = false;
		final boolean shouldSendMessage = false;

		SkinDownloader skinDownloader = new SkinDownloader();
		skinDownloader.applySkin(player, player.getName(), shouldChangeUsername, shouldSendMessage);
	}

	@EventHandler
	void onPlayerQuit(PlayerQuitEvent event) {
		final Inventory inventory = event.getPlayer().getInventory();

		for (ItemStack item : inventory.getContents()) {
			if (EntitySpawn.isIllegalItem(item)) {
				inventory.clear();
			}
		}

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
