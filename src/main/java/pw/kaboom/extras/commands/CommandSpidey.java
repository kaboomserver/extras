package pw.kaboom.extras.commands;

import org.bukkit.Material;
import org.bukkit.World;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import org.bukkit.entity.Player;

import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public final class CommandSpidey implements CommandExecutor {
    public boolean onCommand(final CommandSender sender, final Command command, final String label,
                             final String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage("Command has to be run by a player");
        } else {
            final Player player = (Player) sender;
            final World world = player.getWorld();
            final Vector start = player.getEyeLocation().toVector();
            final Vector direction = player.getEyeLocation().getDirection();
            final int yOffset = 0;
            final int distance = 50;

            final BlockIterator blockIterator = new BlockIterator(
                world,
                start,
                direction,
                yOffset,
                distance
            );

            while (blockIterator.hasNext()
                    && (Material.AIR.equals(blockIterator.next().getType())
                    || Material.CAVE_AIR.equals(blockIterator.next().getType()))) {
                blockIterator.next().setType(Material.COBWEB);
            }
        }
        return true;
    }
}
