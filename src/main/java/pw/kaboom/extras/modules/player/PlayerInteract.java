package pw.kaboom.extras;

import java.util.UUID;

import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.player.PlayerInteractEvent;

class PlayerInteract implements Listener {
	private Main main;
	public PlayerInteract(Main main) {
		this.main = main;
	}

	@EventHandler
	void onPlayerInteract(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		final UUID playerUuid = event.getPlayer().getUniqueId();
		
		if (main.interactMillisList.get(playerUuid) != null) {
			final long millisDifference = System.currentTimeMillis() - main.interactMillisList.get(playerUuid);
	
			if (millisDifference < 150) {
				event.setCancelled(true);
			}
		}
		
		main.interactMillisList.put(playerUuid, System.currentTimeMillis());
	}
}
