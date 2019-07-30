package pw.kaboom.extras;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import org.bukkit.block.data.Levelled;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockRedstoneEvent;

class BlockPhysics implements Listener {
	private Main main;
	public BlockPhysics(Main main) {
		this.main = main;
	}

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

		if (tps < 15) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	void onBlockPhysics(BlockPhysicsEvent event) {
		final Material material = event.getChangedType();

		if (main.fallingBlockList.contains(material)) {
			main.fallingBlockCount++;

			if (main.fallingBlockCount == 10) {
				event.setCancelled(true);
				main.fallingBlockCount = 0;
			}
		} else if (material == Material.FARMLAND) {
			event.setCancelled(true);
		} else if (material == Material.WATER ||
			material == Material.LAVA) {
			final Block block = event.getBlock();
			final Levelled levelledBlock = (Levelled) block.getBlockData();

			if (levelledBlock.getLevel() <= 7) {
				if (block.getRelative(BlockFace.UP).getType() != material) {
					boolean cancel = true;
					boolean solid = false;

					for (BlockFace face : main.faces) {
						if (block.getRelative(face).getType() == Material.AIR ||
							block.getRelative(face).getType() == Material.CAVE_AIR) {
							cancel = false;
						}

						if (block.getRelative(face).getType() != Material.AIR ||
							block.getRelative(face).getType() != Material.CAVE_AIR ||
							block.getRelative(face).getType() != Material.LAVA ||
							block.getRelative(face).getType() != Material.WATER) {
							solid = true;
						}
					}


					if (block.getRelative(BlockFace.UP).getType() == Material.WATER &&
					solid != true) {
						event.setCancelled(true);
					} else if (cancel == true) {
						event.setCancelled(true);
					}
				}
			}
		} else if (main.nonSolidWallMountedBlockList.contains(material)) {
			final Block block = event.getBlock();
			final World world = block.getWorld();
			final int radius = 5;
			int blockCount = 0;

			for (int x = -radius; x <= radius; x++) {
				for (int y = -radius; y <= radius; y++) {
					for (int z = -radius; z <= radius; z++) {
						if (blockCount < 42) {
							final Location blockLocation = new Location(world, block.getX() + x, block.getY() + y, block.getZ() + z);
							final Block coordBlock = world.getBlockAt(blockLocation);

							if (coordBlock.getType() == material ||
								main.nonSolidWallMountedBlockList.contains(coordBlock.getType())) {
								blockCount++;
							}

							continue;
						}
						break;
					}
				}
			}

			if (blockCount == 42) {
				event.setCancelled(true);
			}
		} else if (main.nonSolidDoubleBlockList.contains(material)) {
			final Block block = event.getBlock();

			if (main.nonSolidDoubleBlockList.contains(block.getRelative(BlockFace.DOWN).getType())) {
				event.setCancelled(true);
			} else if (block.getRelative(BlockFace.DOWN).getType() == Material.AIR ||
				(main.nonSolidBlockList.contains(block.getRelative(BlockFace.DOWN).getType()) &&
				!main.nonSolidDoubleBlockList.contains(block.getRelative(BlockFace.DOWN).getType()))) {
 				for (int y = block.getRelative(BlockFace.UP).getY(); y <= 256; y++) {
					final World world = event.getBlock().getWorld();
					final Block coordBlock = world.getBlockAt(new Location(world, block.getX(), y, block.getZ()));

					if (main.nonSolidDoubleBlockList.contains(coordBlock.getType())) {
						coordBlock.setType(Material.AIR, false);
						continue;
					}

					break;
				}

				block.setType(Material.AIR, false);
			}
		} else if (main.nonSolidSingularBlockList.contains(material)) {
			final Block block = event.getBlock();

			if (block.getRelative(BlockFace.DOWN).getType() == Material.AIR ||
				main.nonSolidBlockList.contains(block.getRelative(BlockFace.DOWN).getType())) {
				block.setType(Material.AIR, false);
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
}
