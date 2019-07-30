package pw.kaboom.extras;

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
		} else if (("minecraft:gamerule".equals(arr[0].toLowerCase()) ||
			"gamerule".equals(arr[0].toLowerCase())) &&
			arr.length >= 3) {
			if ("randomtickspeed".equals(arr[1].toLowerCase()) &&
				Double.parseDouble(arr[2]) > 6) {
				event.setCommand(command.replaceFirst(arr[2], "6"));
			}
		} else if (("minecraft:particle".equals(arr[0].toLowerCase()) ||
			"particle".equals(arr[0].toLowerCase())) &&
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
		}
	}
}
