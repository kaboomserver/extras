package pw.kaboom.extras.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pw.kaboom.extras.Main;
import pw.kaboom.extras.platform.PlatformScheduler;

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
        final World defaultWorld = Bukkit.getWorld("world");
        final World world = (defaultWorld == null) ? Bukkit.getWorlds().get(0) : defaultWorld;
        final Location spawnLocation = world.getSpawnLocation();
        final Main plugin = JavaPlugin.getPlugin(Main.class);

        PlatformScheduler.executeOnGlobalRegion(plugin, () -> {
            final Chunk chunk = spawnLocation.getChunk();

            PlatformScheduler.executeOnChunk(plugin, chunk, () -> {
                final Location safeSpawnLocation = world.getHighestBlockAt(spawnLocation)
                        .getLocation()
                        .add(0, 20, 0);
                player.teleportAsync(safeSpawnLocation);
                player.sendMessage(Component
                        .text("Successfully moved to spawn"));
            });
        });

        return true;
    }
}
