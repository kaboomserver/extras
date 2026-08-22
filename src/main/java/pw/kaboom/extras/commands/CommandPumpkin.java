package pw.kaboom.extras.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static io.papermc.paper.command.brigadier.Commands.argument;
import static pw.kaboom.extras.arguments.PlayersOrUUIDArgumentType.getPlayers;
import static pw.kaboom.extras.arguments.PlayersOrUUIDArgumentType.playersOrUUID;

public final class CommandPumpkin implements BrigadierCommand {
    @Override
    public String getLabel() {
        return "pumpkin";
    }

    @Override
    public String getDescription() {
        return "Places a pumpkin on a player's head";
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder
                .requires(src -> src.getSender().hasPermission("extras.pumpkin"))
                .then(argument("players", playersOrUUID())
                        .executes(ctx -> {
                            final List<Player> targets = getPlayers(ctx, "players");
                            for (final Player target : targets) {
                                placePumpkin(target);
                            }
                            if (targets.size() == 1) {
                                ctx.getSource().getSender().sendMessage(
                                        Component.text("\"")
                                                .append(Component.text(
                                                        targets.getFirst().getName())
                                                )
                                                .append(Component.text("\" is now a pumpkin"))
                                );
                            } else {
                                ctx.getSource().getSender().sendMessage(
                                        Component.text(targets.size()
                                                + " players are now pumpkins")
                                );
                            }
                            return targets.size();
                        })
                );
    }

    private void placePumpkin(final Player player) {
        player.getInventory().setHelmet(new ItemStack(Material.CARVED_PUMPKIN));
    }
}
