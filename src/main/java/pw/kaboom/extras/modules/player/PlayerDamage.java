package pw.kaboom.extras;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;

class PlayerDamage implements Listener {
	@EventHandler
	void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntityType() == EntityType.PLAYER) {
			if (((event.getCause() == DamageCause.CUSTOM ||
				event.getCause() == DamageCause.SUICIDE) &&
				event.getDamage() == Short.MAX_VALUE) ||
				(event.getCause() == DamageCause.VOID &&
				event.getDamage() == Float.MAX_VALUE)) {
				event.setDamage(Float.MAX_VALUE);
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	void onPlayerDeath(PlayerDeathEvent event) {
		final Player player = event.getEntity();
		final AttributeInstance maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
		boolean maxHealthLow = false;

		maxHealth.setBaseValue(20);
		try {
			player.setHealth(20);
		} catch (Exception exception) {
			maxHealth.setBaseValue(Double.POSITIVE_INFINITY);
			player.setHealth(20);
			maxHealth.setBaseValue(20);
			maxHealthLow = true;
		}
		player.setFoodLevel(20);
		player.setFireTicks(0);
		player.setRemainingAir(player.getMaximumAir());
		player.getActivePotionEffects().clear();
		event.setCancelled(true);

		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			onlinePlayer.sendMessage(event.getDeathMessage());
		}

		if ((player.getLastDamageCause() != null &&
			player.getLastDamageCause().getCause() == DamageCause.SUICIDE &&
			player.getLastDamageCause().getDamage() == Float.MAX_VALUE) ||
			maxHealthLow == true) {
			return;
		}

		if (player.getBedSpawnLocation() != null) {
			player.teleportAsync(player.getBedSpawnLocation());
		} else {
			final World world = Bukkit.getWorld("world");
			final Location spawnLoc = world.getSpawnLocation();

			for (double y = spawnLoc.getY(); y <= 256; y++) {
				final Location yLocation = new Location(world, spawnLoc.getX(), y, spawnLoc.getZ());
				final Block coordBlock = world.getBlockAt(yLocation);

				if (!coordBlock.getType().isSolid() &&
					!coordBlock.getRelative(BlockFace.UP).getType().isSolid()) {
					player.teleportAsync(yLocation);
					return;
				}
			}
		}
	}
}
