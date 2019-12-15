package pw.kaboom.extras;

import org.bukkit.Location;
import org.bukkit.World;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.player.PlayerTeleportEvent;

class PlayerTeleport implements Listener {
	@EventHandler
	void onPlayerTeleport(PlayerTeleportEvent event) {
		final World world = event.getTo().getWorld();
		int X = (int) Math.round(event.getTo().getX());
		int Y = (int) Math.round(event.getTo().getY());
		int Z = (int) Math.round(event.getTo().getZ());
		
		event.setTo(
			new Location(world, X, Y, Z)
		);
	}
}
