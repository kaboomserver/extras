package pw.kaboom.extras.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;
import pw.kaboom.extras.Main;
import pw.kaboom.extras.platform.PlatformScheduler;

import javax.annotation.Nonnull;

public final class CommandDestroyEntities implements CommandExecutor {
    @Override
    public boolean onCommand(final @Nonnull CommandSender sender,
                             final @Nonnull Command command,
                             final @Nonnull String label,
                             final String[] args) {
        int entityCount = 0;
        int worldCount = 0;

        final Main plugin = JavaPlugin.getPlugin(Main.class);

        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (!EntityType.PLAYER.equals(entity.getType())) {
                    PlatformScheduler.executeOnEntity(plugin, entity, entity::remove);
                    entityCount++;
                }
            }
            worldCount++;
        }

        sender.sendMessage(
            Component.text("Successfully destroyed ")
                .append(Component.text(entityCount))
                .append(Component.text(" entities in "))
                .append(Component.text(worldCount))
                .append(Component.text(" worlds"))
        );
        return true;
    }
}
