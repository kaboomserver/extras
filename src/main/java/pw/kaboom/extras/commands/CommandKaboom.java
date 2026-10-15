package pw.kaboom.extras.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

import static io.papermc.paper.command.brigadier.Commands.restricted;

public final class CommandKaboom implements BrigadierCommand {
    @Override
    public String getLabel() {
        return "kaboom";
    }

    @Override
    public String getDescription() {
        return "I wonder...";
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder
                .requires(restricted(src ->
                        src.getSender().hasPermission("extras.kaboom")
                                && src.getSender() instanceof Player
                ))
                .executes(ctx -> {
                    final Player player = (Player) ctx.getSource().getSender();
                    final boolean explode = ThreadLocalRandom.current().nextBoolean();

                    if (explode) {
                        final Location location = player.getLocation();
                        final World world = player.getWorld();
                        final int explosionCount = 20;
                        final int power = 8;

                        world.createExplosion(location, power, true, true);

                        for (int i = 0; i < explosionCount; i++) {
                            final double posX =
                                    location.getX() + ThreadLocalRandom.current().nextInt(-15
                                            , 15);
                            final double posY =
                                    location.getY() + ThreadLocalRandom.current().nextInt(-6,
                                            6);
                            final double posZ =
                                    location.getZ() + ThreadLocalRandom.current().nextInt(-15
                                            , 15);

                            final Location explodeLocation = new Location(world, posX, posY, posZ);
                            final int power2 = 4;

                            world.createExplosion(explodeLocation, power2, true, true);
                            explodeLocation.getBlock().setType(Material.LAVA);
                        }

                        player.sendMessage(Component.text("Forgive me :c"));
                        return Command.SINGLE_SUCCESS;
                    }

                    player.getInventory().setItemInMainHand(new ItemStack(Material.CAKE));
                    player.sendMessage(Component.text("Have a nice day :)"));
                    return Command.SINGLE_SUCCESS;
                });
    }
}
