package pw.kaboom.extras;

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
					if (blockCount < 300) {
						final Location blockLocation = new Location(world, block.getX() + x, block.getY() + y, block.getZ() + z);
						final Block coordBlock = world.getBlockAt(blockLocation);

						if ((coordBlock.getType() == Material.LAVA ||
							coordBlock.getType() == Material.WATER ||
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
	}

	@EventHandler
	void onBlockPhysics(BlockPhysicsEvent event) {
		final Material material = event.getSourceBlock().getType();

		if (material == Material.FARMLAND) {
			event.setCancelled(true);
		} else if (material == Material.LAVA ||
			material == Material.WATER) {
			final Block block = event.getSourceBlock();
			final World world = block.getWorld();
			final int radius = 5;
			int blockCount = 0;

			for (int x = -radius; x <= radius; x++) {
				for (int y = -radius; y <= radius; y++) {
					for (int z = -radius; z <= radius; z++) {
						if (blockCount < 300) {
							final Location blockLocation = new Location(world, block.getX() + x, block.getY() + y, block.getZ() + z);
							final Block coordBlock = world.getBlockAt(blockLocation);
	
							if ((coordBlock.getType() == Material.LAVA ||
								coordBlock.getType() == Material.WATER ||
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
			final Block block = event.getSourceBlock();
			final World world = block.getWorld();
			final int radius = 5;
			int blockCount = 0;

			for (int x = -radius; x <= radius; x++) {
				for (int y = -radius; y <= radius; y++) {
					for (int z = -radius; z <= radius; z++) {
						if (blockCount < 42) {
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
		} /*else if (Main.nonSolidDoubleBlockList.contains(material)) {
			final Block block = event.getSourceBlock();	

			if (Main.nonSolidDoubleBlockList.contains(block.getRelative(BlockFace.DOWN).getType())) {
				event.setCancelled(true);
			} else if (block.getRelative(BlockFace.DOWN).getType() == Material.AIR ||
				(Main.nonSolidWaterBlockList.contains(material) &&
				block.getRelative(BlockFace.DOWN).getType() == Material.WATER) ||
				(Main.nonSolidBlockList.contains(block.getRelative(BlockFace.DOWN).getType()) &&
				!Main.nonSolidDoubleBlockList.contains(block.getRelative(BlockFace.DOWN).getType()))) {
				Material materialReplacement = Material.AIR;
				
				if (Main.nonSolidWaterBlockList.contains(material)) {
					materialReplacement = Material.WATER;
				}

 				for (int y = block.getRelative(BlockFace.UP).getY(); y <= 256; y++) {
					final World world = event.getBlock().getWorld();
					final Block coordBlock = world.getBlockAt(new Location(world, block.getX(), y, block.getZ()));

					if (Main.nonSolidDoubleBlockList.contains(coordBlock.getType()) ||
						(block.getType() == Material.KELP_PLANT &&
						coordBlock.getType() == Material.KELP)) {
						coordBlock.setType(materialReplacement, false);
						continue;
					}

					break;
				}

				event.setCancelled(true);
				block.setType(materialReplacement, false);
			}
		} else if (Main.nonSolidSingularBlockList.contains(material)) {
			final Block block = event.getSourceBlock();

			if (block.getRelative(BlockFace.DOWN).getType() == Material.AIR ||
				(Main.nonSolidWaterBlockList.contains(material) &&
				block.getRelative(BlockFace.DOWN).getType() == Material.WATER) ||
				Main.nonSolidBlockList.contains(block.getRelative(BlockFace.DOWN).getType())) {
				if (block.getType() == Material.KELP &&
					block.getRelative(BlockFace.DOWN).getType() == Material.KELP_PLANT) {
					return;
				}

				Material materialReplacement = Material.AIR;
				
				if (Main.nonSolidWaterBlockList.contains(material)) {
					materialReplacement = Material.WATER;
				}

				event.setCancelled(true);
				block.setType(materialReplacement, false);
			}
		}*/
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
