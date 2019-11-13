package pw.kaboom.extras;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import org.bukkit.block.data.Levelled;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockRedstoneEvent;

import org.bukkit.event.entity.EntityChangeBlockEvent;

class BlockPhysics implements Listener {
	@EventHandler
	void onBlockFromTo(BlockFromToEvent event) {
		try {
			event.getBlock().getState();
			event.getToBlock().getState();
		} catch (Exception exception) {
			event.setCancelled(true);
			return;
		}
		
		final double tps = Bukkit.getServer().getTPS()[0];

		if (tps < 17) {
			event.setCancelled(true);
			return;
		}
		
		final Block block = event.getBlock();
		final World world = block.getWorld();
		final int radius = 5;
		int blockCount = 0;

		for (int x = -radius; x <= radius; x++) {
			for (int y = -radius; y <= radius; y++) {
				for (int z = -radius; z <= radius; z++) {
					if (blockCount < 200) {
						final Location blockLocation = new Location(world, block.getX() + x, block.getY() + y, block.getZ() + z);
						final Block coordBlock = world.getBlockAt(blockLocation);

						if (coordBlock.isLiquid() ||
							coordBlock.getType() == Material.OBSIDIAN) {
							blockCount++;
						}

						continue;
					} else {
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler
	void onBlockPhysics(BlockPhysicsEvent event) {
		final Material material = event.getChangedType();

		if (material == Material.FARMLAND) {
			event.setCancelled(true);
		} else if (event.getBlock().isLiquid()) {
			final Block block = event.getBlock();
			final World world = block.getWorld();
			final int radius = 5;
			int blockCount = 0;

			for (int x = -radius; x <= radius; x++) {
				for (int y = -radius; y <= radius; y++) {
					for (int z = -radius; z <= radius; z++) {
						if (blockCount < 200) {
							final Location blockLocation = new Location(world, block.getX() + x, block.getY() + y, block.getZ() + z);
							final Block coordBlock = world.getBlockAt(blockLocation);
	
							if ((coordBlock.isLiquid() ||
								coordBlock.getType() == Material.OBSIDIAN) &&
								block.getType() != coordBlock.getType()) {
								blockCount++;
							}
	
							continue;
						} else {
							event.setCancelled(true);
							return;
						}
					}
				}
			}
		} else if (Main.nonSolidBlockList.contains(material) ||
			material == Material.AIR ||
			material == Material.CAVE_AIR) {
			final Block block = event.getBlock();
			final World world = block.getWorld();
			final int radius = 5;
			int blockCount = 0;

			for (int x = -radius; x <= radius; x++) {
				for (int y = -radius; y <= radius; y++) {
					for (int z = -radius; z <= radius; z++) {
						if (blockCount < 100) {
							final Location blockLocation = new Location(world, block.getX() + x, block.getY() + y, block.getZ() + z);
							final Block coordBlock = world.getBlockAt(blockLocation);

							if (Main.nonSolidBlockList.contains(coordBlock.getType())) {
								blockCount++;
							}

							continue;
						} else {
							for (BlockFace face : BlockFace.values()) {
								if (Main.nonSolidBlockList.contains(block.getRelative(face).getType())) {
									event.setCancelled(true);
									return;
								}
							}
							return;
						}
					}
				}
			}
		}
	}

	@EventHandler
	void onBlockRedstone(BlockRedstoneEvent event) {
		final double tps = Bukkit.getServer().getTPS()[0];

		if (tps < 10) {
			event.setNewCurrent(0);
		}
	}
	
	@EventHandler
	void onEntityChangeBlock(EntityChangeBlockEvent event) {
		if (event.getEntityType() == EntityType.FALLING_BLOCK &&
			event.getTo() == Material.AIR) {
			Main.fallingBlockCount++;

			if (Main.fallingBlockCount == 10) {
				event.setCancelled(true);
				Main.fallingBlockCount = 0;
			}
		}
	}
}
