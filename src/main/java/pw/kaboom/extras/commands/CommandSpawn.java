package pw.kaboom.extras.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import org.bukkit.entity.Player;

public final class CommandSpawn implements CommandExecutor {
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage("Command has to be run by a player");
        } else {
            final Player player = (Player) sender;
            final World world = Bukkit.getWorld("world");
            final Location spawnLocation = world.getSpawnLocation();
            final int maxWorldHeight = 256;

            for (double y = spawnLocation.getY(); y <= maxWorldHeight; y++) {
                final Location yLocation = new Location(world, spawnLocation.getX(), y, spawnLocation.getZ());
                final Block coordBlock = world.getBlockAt(yLocation);

                if (!coordBlock.getType().isSolid()
                        && !coordBlock.getRelative(BlockFace.UP).getType().isSolid()) {
                    player.teleportAsync(yLocation);
                    break;
                }
            }

            player.sendMessage("Successfully moved to spawn");
        }
        return true;
    }
}
