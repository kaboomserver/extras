package pw.kaboom.extras;

import org.bukkit.Location;
import org.bukkit.Material;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

class CommandSpidey implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		final Player player = (Player) sender;
		final Location eyePos = player.getEyeLocation();
		final Vector playerPos = new Vector(eyePos.getX(), eyePos.getY(), eyePos.getZ());
		final Vector direction = eyePos.getDirection();
		final int distance = 50;

		final BlockIterator blockIterator = new BlockIterator(player.getWorld(), playerPos, direction, 0, distance);

		while (blockIterator.hasNext() &&
			(blockIterator.next().getType() == Material.AIR ||
			blockIterator.next().getType() == Material.CAVE_AIR)) {
			blockIterator.next().setType(Material.COBWEB);
		}
		return true;
	}
}
