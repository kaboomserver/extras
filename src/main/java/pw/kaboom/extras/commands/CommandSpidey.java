package pw.kaboom.extras.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import static io.papermc.paper.command.brigadier.Commands.restricted;

public final class CommandSpidey implements BrigadierCommand {
    @Override
    public String getLabel() {
        return "spidey";
    }

    @Override
    public String getDescription() {
        return "Annoying little spider...";
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder
                .requires(restricted(src ->
                        src.getSender().hasPermission("extras.spidey")
                                && src.getSender() instanceof Player
                ))
                .executes(ctx -> {
                    final Player player = (Player) ctx.getSource().getSender();

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

                        if (block.getType() != Material.COBWEB && !block.getType().isAir()) break;
                        block.setType(Material.COBWEB);
                    }
                    return Command.SINGLE_SUCCESS;
                });
    }
}
