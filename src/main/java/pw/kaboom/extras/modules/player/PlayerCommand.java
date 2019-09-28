package pw.kaboom.extras;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import org.bukkit.command.Command;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import org.bukkit.scheduler.BukkitRunnable;

class PlayerCommand implements Listener {
	@EventHandler
	void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		final String[] arr = event.getMessage().split(" ");
		final String command = event.getMessage();
		final UUID playerUuid = event.getPlayer().getUniqueId();
		
		if (Main.commandMillisList.get(playerUuid) != null) {
			final long millisDifference = System.currentTimeMillis() - Main.commandMillisList.get(playerUuid);
	
			if (millisDifference < 75) {
				event.setCancelled(true);
			}
		}
		
		Main.commandMillisList.put(playerUuid, System.currentTimeMillis());
		
		if (event.isCancelled()) {
			return;
		}

		if (("/minecraft:execute".equals(arr[0].toLowerCase()) ||
			"/execute".equals(arr[0].toLowerCase())) &&
			arr.length >= 2) {
			for (int i = 1; i < arr.length; i++) {
				if ("as".equalsIgnoreCase(arr[i]) ||
					"at".equalsIgnoreCase(arr[i])) {
					for (int i2 = i+1; i2 < arr.length; i2++) {
						if ("at".equalsIgnoreCase(arr[i2]) ||
							"as".equalsIgnoreCase(arr[i2])) {
							Command.broadcastCommandMessage(event.getPlayer(), "Forbidden execute pattern detected");
							event.setCancelled(true);
							break;
						}
					}
				} else if (i+1 < arr.length &&
					"run".equalsIgnoreCase(arr[i])) {
					if ("execute".equalsIgnoreCase(arr[i+1]) ||
						"particle".equalsIgnoreCase(arr[i+1]) ||
						"save-off".equalsIgnoreCase(arr[i+1]) ||
						"stop".equalsIgnoreCase(arr[i+1])) {
						Command.broadcastCommandMessage(event.getPlayer(), "Forbidden execute command detected");
						event.setCancelled(true);
						break;
					} else if (i+3 < arr.length &&
						"gamerule".equalsIgnoreCase(arr[i+1])) {
						if ("randomTickSpeed".equalsIgnoreCase(arr[i+2]) &&
							Double.parseDouble(arr[i+3]) > 6) {
							event.setMessage(command.replaceFirst("(?i)" + "randomTickSpeed " + arr[i+3], "randomTickSpeed 6"));
							break;
						}
					}
				}
			}
		} else if (("/minecraft:gamerule".equalsIgnoreCase(arr[0]) ||
			"/gamerule".equalsIgnoreCase(arr[0])) &&
			arr.length >= 3) {
			if ("randomTickSpeed".equalsIgnoreCase(arr[1]) &&
				Double.parseDouble(arr[2]) > 6) {
				event.setMessage(command.replaceFirst(arr[2], "6"));
			}
		} else if (("/minecraft:particle".equalsIgnoreCase(arr[0]) ||
			"/particle".equalsIgnoreCase(arr[0])) &&
			arr.length >= 10) {
			if (Double.parseDouble(arr[9]) > 10) {
				final StringBuilder stringBuilder = new StringBuilder();

				for (int i = 0; i < 9; i++) {
					stringBuilder.append(arr[i]).append(" ");
				}
				stringBuilder.append("10 ");
				for (int i = 10; i < arr.length; i++) {
					stringBuilder.append(arr[i]).append(" ");
				}

				event.setMessage(stringBuilder.toString());
			}
		} else if (("/bukkit:reload".equalsIgnoreCase(arr[0]) ||
			"/bukkit:rl".equalsIgnoreCase(arr[0]) ||
			"/reload".equalsIgnoreCase(arr[0]) || 
			"/rl".equalsIgnoreCase(arr[0])) &&
			event.getPlayer().hasPermission("bukkit.command.reload")) {
			if (arr.length >= 2 &&
				"confirm".equalsIgnoreCase(arr[1])) {
				event.setCancelled(true);
				Command.broadcastCommandMessage(event.getPlayer(), ChatColor.RED + "Please note that this command is not supported and may cause issues when using some plugins.");
				Command.broadcastCommandMessage(event.getPlayer(), ChatColor.RED + "If you encounter any issues please use the /stop command to restart your server.");
				Command.broadcastCommandMessage(event.getPlayer(), ChatColor.GREEN + "Reload complete.");
			}
		} else if (("/restart".equalsIgnoreCase(arr[0]) ||
			"/spigot:restart".equalsIgnoreCase(arr[0])) &&
			event.getPlayer().hasPermission("bukkit.command.restart")) {
			event.setCancelled(true);
		} else if (("/minecraft:save-off".equalsIgnoreCase(arr[0]) ||
			"/save-off".equalsIgnoreCase(arr[0])) &&
			event.getPlayer().hasPermission("minecraft.command.save.disable")) {
			event.setCancelled(true);
			Command.broadcastCommandMessage(event.getPlayer(), "Automatic saving is now disabled");
		} else if (("/minecraft:stop".equalsIgnoreCase(arr[0]) ||
			"/stop".equalsIgnoreCase(arr[0])) &&
			event.getPlayer().hasPermission("minecraft.command.stop")) {
			event.setCancelled(true);
			Command.broadcastCommandMessage(event.getPlayer(), "Stopping the server");
		}
	}
}
