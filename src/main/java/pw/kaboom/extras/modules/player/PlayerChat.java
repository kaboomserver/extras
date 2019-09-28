package pw.kaboom.extras;

import java.util.UUID;

import org.bukkit.ChatColor;

import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.player.AsyncPlayerChatEvent;

import org.bukkit.plugin.java.JavaPlugin;

class PlayerChat implements Listener {
	@EventHandler
	void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		final Player player = event.getPlayer();
		final UUID playerUuid = event.getPlayer().getUniqueId();
		
		if (Main.commandMillisList.get(playerUuid) != null) {
			final long millisDifference = System.currentTimeMillis() - Main.commandMillisList.get(playerUuid);
	
			if (millisDifference < 5) {
				event.setCancelled(true);
			}
		}
		
		Main.commandMillisList.put(playerUuid, System.currentTimeMillis());
		
		if (event.isCancelled()) {
			return;
		}

		if (JavaPlugin.getPlugin(Main.class).getConfig().getString(player.getUniqueId().toString()) != null) {
			final String prefix = ChatColor.translateAlternateColorCodes(
				'&',
				JavaPlugin.getPlugin(Main.class).getConfig().getString(player.getUniqueId().toString())
			);

			event.setFormat(prefix + ChatColor.RESET + " " + player.getDisplayName().toString() + ChatColor.RESET + ": " + ChatColor.RESET + "%2$s");
		} else if (event.getPlayer().isOp()) {
			final String prefix = ChatColor.translateAlternateColorCodes(
				'&',
				"&4&l[&c&lOP&4&l]"
			);

			event.setFormat(prefix + ChatColor.RED + " " + player.getDisplayName().toString() + ChatColor.RESET + ": " + ChatColor.RESET + "%2$s");
		} else {
			final String prefix = ChatColor.translateAlternateColorCodes(
				'&',
				"&8&l[&7&lDeOP&8&l]"
			);

			event.setFormat(prefix + ChatColor.GRAY + " " + player.getDisplayName().toString() + ChatColor.RESET + ": " + ChatColor.RESET + "%2$s");
		}

		event.setMessage(ChatColor.translateAlternateColorCodes('&', event.getMessage()));
	}
}
