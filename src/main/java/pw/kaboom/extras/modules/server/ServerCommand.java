package pw.kaboom.extras;

import org.bukkit.ChatColor;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.server.ServerCommandEvent;

class ServerCommand implements Listener {
	private Main main;
	public ServerCommand(Main main) {
		this.main = main;
	}

	@EventHandler
	void onServerCommand(ServerCommandEvent event) {
		final String[] arr = event.getCommand().split(" ");
		final String command = event.getCommand();

		if (main.consoleCommandBlacklist.contains(arr[0].toLowerCase())) {
			event.setCancelled(true);
		} else if (("minecraft:gamerule".equalsIgnoreCase(arr[0]) ||
			"gamerule".equalsIgnoreCase(arr[0])) &&
			arr.length >= 3) {
			if ("randomTickSpeed".equalsIgnoreCase(arr[1]) &&
				Double.parseDouble(arr[2]) > 6) {
				event.setCommand(command.replaceFirst(arr[2], "6"));
			}
		} else if (("minecraft:particle".equalsIgnoreCase(arr[0]) ||
			"particle".equalsIgnoreCase(arr[0])) &&
			arr.length >= 10) {
			if (Double.parseDouble(arr[9]) > 10) {
				final StringBuilder stringBuilder = new StringBuilder();

				for (int i = 0; i < 9; i++) {
					stringBuilder.append(arr[i] + " ");
				}
				stringBuilder.append("10 ");
				for (int i = 10; i < arr.length; i++) {
					stringBuilder.append(arr[i] + " ");
				}

				event.setCommand(stringBuilder.toString());
			}
		} else if ("bukkit:reload".equalsIgnoreCase(arr[0]) ||
			"bukkit:rl".equalsIgnoreCase(arr[0]) ||
			"reload".equalsIgnoreCase(arr[0]) || 
			"rl".equalsIgnoreCase(arr[0])) {
			if (arr.length >= 2 &&
				"confirm".equalsIgnoreCase(arr[1])) {
				event.setCancelled(true);
				Command.broadcastCommandMessage(event.getSender(), ChatColor.RED + "Please note that this command is not supported and may cause issues when using some plugins.");
				Command.broadcastCommandMessage(event.getSender(), ChatColor.RED + "If you encounter any issues please use the /stop command to restart your server.");
				Command.broadcastCommandMessage(event.getSender(), ChatColor.GREEN + "Reload complete.");
			}
		} else if ("restart".equalsIgnoreCase(arr[0]) ||
			"spigot:restart".equalsIgnoreCase(arr[0])) {
			event.setCancelled(true);
		} else if ("minecraft:save-off".equalsIgnoreCase(arr[0]) ||
			"save-off".equalsIgnoreCase(arr[0])) {
			event.setCancelled(true);
			Command.broadcastCommandMessage(event.getSender(), "Automatic saving is now disabled");
		} else if (("minecraft:stop".equalsIgnoreCase(arr[0]) ||
			"stop".equalsIgnoreCase(arr[0])) &&
			!(event.getSender() instanceof BlockCommandSender)) {
			event.setCancelled(true);
			Command.broadcastCommandMessage(event.getSender(), "Stopping the server");
		}
	}
}
