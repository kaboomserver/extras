package pw.kaboom.extras.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pw.kaboom.extras.modules.player.skin.SkinManager;

import java.util.List;

import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;

public final class CommandSkin implements BrigadierCommand {
    private static final SimpleCommandExceptionType EX_NOT_PLAYER =
            new SimpleCommandExceptionType(
                    new LiteralMessage("This command must be called by a player"));

    @Override
    public String getLabel() {
        return "skin";
    }

    @Override
    public String getDescription() {
        return "Changes your skin";
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder.requires(src ->
                src.getSender().hasPermission("extras.skin")
                        && src.getSender() instanceof Player
        );

        for (final String alias : List.of("off", "remove", "disable")) {
            builder
                    .then(literal(alias).executes(ctx -> {
                        SkinManager.removeSkin(player(ctx), true);
                        return Command.SINGLE_SUCCESS;
                    }));
        }
        for (final String alias : List.of("auto", "default", "reset")) {
            builder
                    .then(literal(alias).executes(ctx -> {
                        final Player player = player(ctx);
                        SkinManager.requestSkin(player, player.getName(), true);
                        return Command.SINGLE_SUCCESS;
                    }));
        }

        builder.then(argument("username", greedyString())
                .suggests((ctx, sb) -> {
                    final String remaining = sb.getRemaining();
                    Bukkit.getOnlinePlayers().stream()
                            .map(Player::getName)
                            .filter(ply -> ply.startsWith(remaining))
                            .forEach(sb::suggest);
                    return sb.buildFuture();
                })
                .executes(ctx -> {
                    SkinManager.requestSkin(
                            player(ctx), ctx.getArgument("username", String.class), true);
                    return Command.SINGLE_SUCCESS;
                }));
    }

    private static Player player(final CommandContext<CommandSourceStack> ctx)
            throws CommandSyntaxException {
        if (ctx.getSource().getSender() instanceof final Player player) {
            return player;
        }
        throw EX_NOT_PLAYER.create();
    }
}
