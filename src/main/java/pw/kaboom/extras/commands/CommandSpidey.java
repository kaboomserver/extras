package pw.kaboom.extras.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public final class CommandSpidey implements CommandExecutor {
    public boolean onCommand(final @Nonnull CommandSender sender,
                             final @Nonnull Command command,
                             final @Nonnull String label,
                             final String[] args) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Component
                    .text("Command has to be run by a player"));
            return true;
        }

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

        while (blockIterator.hasNext()) {
            final Block block = blockIterator.next();

            if (!block.getType().isAir()) break;
            block.setType(Material.COBWEB);
        }
        return true;
    }
}
