package pw.kaboom.extras;

import java.util.UUID;

import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.player.PlayerInteractEvent;

class PlayerInteract implements Listener {
	@EventHandler
	void onPlayerInteract(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		final UUID playerUuid = event.getPlayer().getUniqueId();
		
		if (Main.interactMillisList.get(playerUuid) != null) {
			final long millisDifference = System.currentTimeMillis() - Main.interactMillisList.get(playerUuid);
	
			if (millisDifference < 150) {
				event.setCancelled(true);
			}
		}
		
		Main.interactMillisList.put(playerUuid, System.currentTimeMillis());
	}
}
