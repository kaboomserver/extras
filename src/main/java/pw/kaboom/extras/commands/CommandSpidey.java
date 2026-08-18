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

public final class CommandSpidey implements BrigadierCommand {
    private static final SimpleCommandExceptionType ERR_NOT_PLAYER =
            new SimpleCommandExceptionType(MessageComponentSerializer.message().serialize(
                    Component.text("This command must be called by a player")));

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
                .requires(src ->
                        src.getSender().hasPermission("extras.spidey")
                                && src.getSender() instanceof Player
                )
                .executes(ctx -> {
                    if (!(ctx.getSource().getSender() instanceof final Player player)) {
                        throw ERR_NOT_PLAYER.create();
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

                        if (block.getType() != Material.COBWEB && !block.getType().isAir()) break;
                        block.setType(Material.COBWEB);
                    }
                    return Command.SINGLE_SUCCESS;
                });
    }
}
