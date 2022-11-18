package pw.kaboom.extras.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import javax.annotation.Nonnull;

public final class CommandDestroyEntities implements CommandExecutor {
    @Override
    public boolean onCommand(final @Nonnull CommandSender sender,
                             final @Nonnull Command command,
                             final @Nonnull String label,
                             final String[] args) {
        int entityCount = 0;
        int worldCount = 0;

        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (!EntityType.PLAYER.equals(entity.getType())) {
                    try {
                        entity.remove();
                        entityCount++;
                    } catch (Exception ignored) {
                        // Broken entity
                        continue;
                    }
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
