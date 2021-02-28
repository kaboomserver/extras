package pw.kaboom.extras.modules.player;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public final class PlayerCommand implements Listener {
	private static HashMap<UUID, Long> commandMillisList = new HashMap<UUID, Long>();

	@EventHandler
	void onPlayerCommandPreprocess(final PlayerCommandPreprocessEvent event) {
		final UUID playerUuid = event.getPlayer().getUniqueId();

		if (getCommandMillisList().get(playerUuid) != null) {
			final long millisDifference = System.currentTimeMillis() - getCommandMillisList().get(playerUuid);

			if (millisDifference < 75) {
				event.setCancelled(true);
			}
		}

		getCommandMillisList().put(playerUuid, System.currentTimeMillis());
		event.setCancelled(true);
	}

	public static HashMap<UUID, Long> getCommandMillisList() {
		return commandMillisList;
	}
}
