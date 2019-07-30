package pw.kaboom.extras;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.player.PlayerCommandPreprocessEvent;

class PlayerCommand implements Listener {
	private Main main;
	public PlayerCommand(Main main) {
		this.main = main;
	}

	@EventHandler
	void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		final String[] arr = event.getMessage().split(" ");
		final String command = event.getMessage();
		final UUID playerUuid = event.getPlayer().getUniqueId();
		final long millisDifference = System.currentTimeMillis() - main.commandMillisList.get(playerUuid);

		main.commandMillisList.put(playerUuid, System.currentTimeMillis());

		if (millisDifference < 400) {
			event.setCancelled(true);
			return;
		}

		if (("/minecraft:gamerule".equals(arr[0].toLowerCase()) ||
			"/gamerule".equals(arr[0].toLowerCase())) &&
			arr.length >= 3) {
			if ("randomtickspeed".equals(arr[1].toLowerCase()) &&
				Double.parseDouble(arr[2]) > 6) {
				event.setMessage(command.replaceFirst(arr[2], "6"));
			}
		} else if (("/minecraft:particle".equals(arr[0].toLowerCase()) ||
			"/particle".equals(arr[0].toLowerCase())) &&
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

				event.setMessage(stringBuilder.toString());
			}
		}
	}
}
