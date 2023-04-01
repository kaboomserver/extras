package pw.kaboom.extras.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadLocalRandom;

public final class CommandKaboom implements CommandExecutor {

    public boolean onCommand(final @Nonnull CommandSender sender,
                             final @Nonnull Command command,
                             final @Nonnull String label,
                             final String[] args) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Component
                    .text("Command has to be run by a player"));
            return true;
        }

        boolean explode = ThreadLocalRandom.current().nextBoolean();

        if (explode) {
            final Location location = player.getLocation();
            final World world = player.getWorld();
            final int explosionCount = 20;
            final int power = 8;

            world.createExplosion(location, power, true, true);

            for (int i = 0; i < explosionCount; i++) {
                final double posX = location.getX() + ThreadLocalRandom.current().nextInt(-15, 15);
                final double posY = location.getY() + ThreadLocalRandom.current().nextInt(-6, 6);
                final double posZ = location.getZ() + ThreadLocalRandom.current().nextInt(-15, 15);

                final Location explodeLocation = new Location(world, posX, posY, posZ);
                final int power2 = 4;

                world.createExplosion(explodeLocation, power2, true, true);
                explodeLocation.getBlock().setType(Material.LAVA);
            }

            player.sendMessage(Component.text("Forgive me :c"));
            return true;
        }

        player.getInventory().setItemInMainHand(new ItemStack(Material.CAKE));
        player.sendMessage(Component.text("Have a nice day :)"));
        return true;
    }
}
