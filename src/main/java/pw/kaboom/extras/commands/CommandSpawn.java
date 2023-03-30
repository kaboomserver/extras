package pw.kaboom.extras.commands;

import net.kyori.adventure.text.Component;
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
import org.bukkit.plugin.java.JavaPlugin;
import pw.kaboom.extras.Main;

import javax.annotation.Nonnull;

public final class CommandSpawn implements CommandExecutor {
    public boolean onCommand(final @Nonnull CommandSender sender,
                             final @Nonnull Command command,
                             final @Nonnull String label,
                             final String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(Component
                    .text("Command has to be run by a player"));
            return true;
        }

        final Player player = (Player) sender;
        final World playerWorld = player.getWorld();
        final World defaultWorld = Bukkit.getWorld("world");
        final World world = (defaultWorld == null) ? Bukkit.getWorlds().get(0) : defaultWorld;
        final Main plugin = JavaPlugin.getPlugin(Main.class);


        if (!world.equals(playerWorld) && plugin.isFolia()) {
            sender.sendMessage(Component
                    .text("Cross-dimensional teleports are currently unsupported on Folia."));
            return true;
        }

        final Location spawnLocation = world.getSpawnLocation();
        final int maxWorldHeight = 256;

        for (double y = spawnLocation.getY(); y <= maxWorldHeight; y++) {
            final Location yLocation = new Location(world, spawnLocation.getX(), y,
                    spawnLocation.getZ());
            final Block coordBlock = world.getBlockAt(yLocation);

            if (!coordBlock.getType().isSolid()
                    && !coordBlock.getRelative(BlockFace.UP).getType().isSolid()) {
                player.teleportAsync(yLocation);
                break;
            }
        }

        player.sendMessage(Component
                .text("Successfully moved to spawn"));
        return true;
    }
}
