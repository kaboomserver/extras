package pw.kaboom.extras.modules.block;

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;

public final class BlockPhysics implements Listener {
	public static HashSet<BlockFace> blockFaces = new HashSet<BlockFace>();

	@EventHandler
	void onBlockForm(final BlockFormEvent event) {
		if (event.getBlock().getType() == Material.LAVA
				|| event.getBlock().getType() == Material.WATER) {
			for (BlockFace face : blockFaces) {
				if (event.getBlock().getRelative(face).getType() != Material.LAVA
						&& event.getBlock().getRelative(face).getType() != Material.WATER) {
					return;
				}
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	void onBlockFromTo(final BlockFromToEvent event) {
		if (event.getBlock().getType() == Material.LAVA
				|| event.getBlock().getType() == Material.WATER) {
			boolean lavaFound = false;
			boolean waterFound = false;

			for (BlockFace face : blockFaces) {
				if (event.getBlock().getRelative(face).getType() == Material.LAVA) {
					lavaFound = true;
				} else if (event.getBlock().getRelative(face).getType() == Material.WATER) {
					waterFound = true;
				}

				if (lavaFound && waterFound) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	void onBlockDestroy(final BlockDestroyEvent event) {
		if (!event.getBlock().getType().isSolid()) {
			for (BlockFace face : blockFaces) {
				if (event.getBlock().getRelative(face).getType() != event.getBlock().getType()) {
					return;
				}
				event.getBlock().setType(Material.AIR, false);
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	void onBlockPhysics(final BlockPhysicsEvent event) {
		switch (event.getChangedType()) {
		case COMPARATOR:
		case REDSTONE_TORCH:
		case REDSTONE_WIRE:
		case REPEATER:
			/*for (BlockFace face : blockFaces) {
				if (event.getBlock().getRelative(face).getType() != event.getChangedType()) {
					return;
				}
				event.setCancelled(true);
			}*/
			event.setCancelled(true);
			break;
		case ACTIVATOR_RAIL:
		case DETECTOR_RAIL:
		case POWERED_RAIL:
		case RAIL:
			for (BlockFace face : blockFaces) {
				if (event.getBlock().getRelative(face).getType() != event.getChangedType()) {
					return;
				}
				event.getBlock().setType(Material.AIR, false);
				event.setCancelled(true);
			}
		default:
			break;
		}
	}

	@EventHandler
	void onBlockRedstone(final BlockRedstoneEvent event) {
		final double tps = Bukkit.getServer().getTPS()[0];
		final int maxTps = 10;

		if (tps < maxTps) {
			event.setNewCurrent(0);
		}
	}

	private int fallingBlockCount;

	@EventHandler
	void onEntityChangeBlock(final EntityChangeBlockEvent event) {
		if (event.getEntityType() == EntityType.FALLING_BLOCK
				&& event.getTo() == Material.AIR) {
			fallingBlockCount++;

			final int maxFallingBlockCount = 10;

			if (fallingBlockCount == maxFallingBlockCount) {
				event.setCancelled(true);
				fallingBlockCount = 0;
			}
		}
	}
}
