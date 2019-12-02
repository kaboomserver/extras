package pw.kaboom.extras;

import java.io.File;
import java.util.UUID;

import org.bukkit.ChatColor;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

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
		
		final File configFile = new File(JavaPlugin.getPlugin(Main.class).getDataFolder(), "prefixes.yml");
		final FileConfiguration prefixConfig = YamlConfiguration.loadConfiguration(configFile);
		final String prefix;
		final String name = player.getDisplayName().toString();

		if (prefixConfig.getString(player.getUniqueId().toString()) != null) {
			prefix = ChatColor.translateAlternateColorCodes(
				'&',
				prefixConfig.getString(player.getUniqueId().toString()) +  " " + ChatColor.RESET
			);
		} else if (event.getPlayer().isOp()) {
			prefix = JavaPlugin.getPlugin(Main.class).getConfig().getString("opTag");
		} else {
			prefix = JavaPlugin.getPlugin(Main.class).getConfig().getString("deOpTag");
		}

		event.setFormat(prefix + name + ChatColor.RESET + ": " + ChatColor.RESET + "%2$s");
		event.setMessage(
			ChatColor.translateAlternateColorCodes('&', event.getMessage())
		);
	}
}