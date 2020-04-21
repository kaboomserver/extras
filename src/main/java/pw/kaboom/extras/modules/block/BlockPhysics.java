package pw.kaboom.extras.modules.block;

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;

public final class BlockPhysics implements Listener {

	// This class contains code to prevent large areas of non-solid blocks
	// from crashing the server

	private static HashSet<BlockFace> blockFaces = new HashSet<BlockFace>();

	@EventHandler
	void onBlockDestroy(final BlockDestroyEvent event) {
		try {
			if (!event.getBlock().getType().isSolid()) {
				for (BlockFace face : getBlockFaces()) {
					if (event.getBlock().getRelative(face).getType() != event.getBlock().getType()) {
						return;
					}
					if (!event.getBlock().getType().equals(Material.AIR)) {
						event.getBlock().setType(Material.AIR, false);
					}
					if (!event.isCancelled()) {
						event.setCancelled(true);
					}
				}
			}
		} catch (Exception | StackOverflowError e) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	void onBlockFade(final BlockFadeEvent event) {
		try {
			if (event.getBlock().getType() == Material.FIRE) {
				event.getBlock().setType(Material.AIR, false);
				event.setCancelled(true);
			}
		} catch (Exception | StackOverflowError e) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	void onBlockForm(final BlockFormEvent event) {
		try {
			if (event.getBlock().getType() == Material.LAVA
					|| event.getBlock().getType() == Material.WATER) {
				for (BlockFace face : getBlockFaces()) {
					if (event.getBlock().getRelative(face).getType() != Material.LAVA
							&& event.getBlock().getRelative(face).getType() != Material.WATER) {
						return;
					}
					event.setCancelled(true);
				}
			}
		} catch (Exception | StackOverflowError e) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	void onBlockFromTo(final BlockFromToEvent event) {
		try {
			if (event.getBlock().getType() == Material.LAVA
					|| event.getBlock().getType() == Material.WATER) {
				boolean lavaFound = false;
				boolean waterFound = false;

				for (BlockFace face : getBlockFaces()) {
					if (event.getBlock().getRelative(face).getType() == Material.LAVA && !lavaFound) {
						lavaFound = true;
					} else if (event.getBlock().getRelative(face).getType() == Material.WATER && !waterFound) {
						waterFound = true;
					}

					if (lavaFound && waterFound) {
						event.setCancelled(true);
						return;
					}
				}
			}
		} catch (Exception | StackOverflowError e) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	void onBlockPhysics(final BlockPhysicsEvent event) {
		try {
			switch (event.getChangedType()) {
			case ACTIVATOR_RAIL:
			case DETECTOR_RAIL:
			case POWERED_RAIL:
			case RAIL:
			case COMPARATOR:
			case REDSTONE_TORCH:
			case REDSTONE_WIRE:
			case REPEATER:
			case TRIPWIRE:
				if (!event.getBlock().getRelative(BlockFace.DOWN).getType().isSolid()
						&& !Material.AIR.equals(event.getBlock().getRelative(BlockFace.DOWN).getType())
								&& !Material.CAVE_AIR.equals(event.getBlock().getRelative(BlockFace.DOWN).getType())) {
					event.setCancelled(true);
				}
				return;
			case COMMAND_BLOCK:
			case CHAIN_COMMAND_BLOCK:
			case REPEATING_COMMAND_BLOCK:
				if (Material.STRUCTURE_BLOCK.equals(event.getSourceBlock().getType())) {
					event.setCancelled(true);
				}
			default:
				break;
			}
		} catch (Exception | StackOverflowError e) {
			event.setCancelled(true);
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

	public static HashSet<BlockFace> getBlockFaces() {
		return blockFaces;
	}
}
