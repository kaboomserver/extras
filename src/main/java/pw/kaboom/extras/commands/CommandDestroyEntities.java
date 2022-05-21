package pw.kaboom.extras.commands;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public final class CommandDestroyEntities implements CommandExecutor {
    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label,
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

        sender.sendMessage("Successfully destroyed " + entityCount + " entities in "
                           + worldCount + " worlds");
        return true;
    }
}
