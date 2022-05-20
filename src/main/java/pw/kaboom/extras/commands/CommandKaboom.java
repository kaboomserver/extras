package pw.kaboom.extras.commands;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class CommandKaboom implements CommandExecutor {

    public boolean onCommand(final CommandSender sender, final Command command, final String label,
                             final String[] args) {
        final Player player = (Player) sender;
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

            player.sendMessage("Forgive me :c");
        } else {
            player.getInventory().setItemInMainHand(new ItemStack(Material.CAKE));
            player.sendMessage("Have a nice day :)");
        }
        return true;
    }
}
