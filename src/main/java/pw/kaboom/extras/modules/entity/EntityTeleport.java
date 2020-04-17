package pw.kaboom.extras.modules.entity;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTeleportEvent;

public final class EntityTeleport implements Listener {
	public static Location limitLocation(final Location location) {
		double x = location.getX();
		double y = location.getY();
		double z = location.getZ();

		final int maxValue = 30000000;
		final int minValue = -30000000;

		if (x > maxValue) {
			x = maxValue;
		}
		if (x < minValue) {
			x = minValue;
		}
		if (y > maxValue) {
			y = maxValue;
		}
		if (y < minValue) {
			y = minValue;
		}
		if (z > maxValue) {
			z = maxValue;
		}
		if (z < minValue) {
			z = minValue;
		}

		return new Location(location.getWorld(), x, y, z);
	}

	@EventHandler
	void onEntityTeleport(final EntityTeleportEvent event) {
		event.setTo(limitLocation(event.getTo()));
	}
}
