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
			location.setX(maxValue);
		}
		if (x < minValue) {
			location.setX(minValue);
		}
		if (y > maxValue) {
			location.setY(maxValue);
		}
		if (y < minValue) {
			location.setY(minValue);
		}
		if (z > maxValue) {
			location.setZ(maxValue);
		}
		if (z < minValue) {
			location.setZ(minValue);
		}

		return location;
	}

	@EventHandler
	void onEntityTeleport(final EntityTeleportEvent event) {
		event.setTo(limitLocation(event.getTo()));
	}
}
