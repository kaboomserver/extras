package pw.kaboom.extras.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;

import java.util.List;

import static io.papermc.paper.command.brigadier.Commands.argument;
import static pw.kaboom.extras.arguments.PlayerOrUUIDArgumentType.getPlayer;
import static pw.kaboom.extras.arguments.PlayerOrUUIDArgumentType.playerOrUUID;

public final class CommandPing implements BrigadierCommand {
    private static final SimpleCommandExceptionType EX_NOT_PLAYER =
            new SimpleCommandExceptionType(new LiteralMessage(
                    "This command must be called by a player"));

    @Override
    public String getLabel() {
        return "ping";
    }

    @Override
    public String getDescription() {
        return "Gets your ping";
    }

    @Override
    public List<String> getAliases() {
        return List.of("delay", "ms");
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder
                .requires(src -> src.getSender().hasPermission("extras.ping"))
                .executes(ctx -> {
                    if (!(ctx.getSource().getSender() instanceof final Player player)) {
                        throw EX_NOT_PLAYER.create();
                    }
                    final int ping = player.getPing();
                    ctx.getSource().getSender().sendMessage(
                            Component.empty()
                                    .append(Component.text("Your ping is "))
                                    .append(Component.text(ping + "ms.", getColor(ping)))
                    );
                    return Command.SINGLE_SUCCESS;
                })
                .then(argument("player", playerOrUUID())
                        .executes(ctx -> {
                            final Player player = getPlayer(ctx, "player");
                            final int ping = player.getPing();
                            ctx.getSource().getSender().sendMessage(
                                    Component.text(player.getName())
                                            .append(Component.text("'s ping is "))
                                            .append(Component.text(ping + "ms.",
                                                    getColor(ping)))
                            );
                            return Command.SINGLE_SUCCESS;
                        })
                );
    }

    private TextColor getColor(final int ping) {
        final int d = ping / 100;
        return switch (d) {
            case 0 -> NamedTextColor.GREEN;
            case 1, 2, 3, 4 -> NamedTextColor.YELLOW;
            case 5 -> NamedTextColor.RED;
            default -> NamedTextColor.DARK_RED;
        };
    }
}
