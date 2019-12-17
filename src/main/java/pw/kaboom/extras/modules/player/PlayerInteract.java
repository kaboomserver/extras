package pw.kaboom.extras.modules.player;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteract implements Listener {
	static HashMap<UUID, Long> interactMillisList = new HashMap<UUID, Long>();

	@EventHandler
	void onPlayerInteract(PlayerInteractEvent event) {
		final UUID playerUuid = event.getPlayer().getUniqueId();

		if (interactMillisList.get(playerUuid) != null) {
			final long millisDifference = System.currentTimeMillis() - interactMillisList.get(playerUuid);

			if (millisDifference < 150) {
				event.setCancelled(true);
			}
		}

		interactMillisList.put(playerUuid, System.currentTimeMillis());
	}
}
