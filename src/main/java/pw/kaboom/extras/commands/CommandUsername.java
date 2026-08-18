package pw.kaboom.extras.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import pw.kaboom.extras.modules.player.username.UsernameManager;
import pw.kaboom.extras.util.Utility;

import java.util.List;

import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;

public final class CommandUsername implements BrigadierCommand {
    private static final SimpleCommandExceptionType EX_NOT_PLAYER =
            new SimpleCommandExceptionType(
                    new LiteralMessage("This command must be called by a player")
            );
    private static final SimpleCommandExceptionType EX_RATELIMIT =
            new SimpleCommandExceptionType(
                    new LiteralMessage("Please wait a few seconds before changing your username.")
            );
    private static final SimpleCommandExceptionType EX_TAKEN =
            new SimpleCommandExceptionType(
                    new LiteralMessage("A player with that username is already logged in.")
            );
    private static final SimpleCommandExceptionType EX_DEFAULT =
            new SimpleCommandExceptionType(
                    new LiteralMessage("You already have your default username.")
            );
    private static final DynamicCommandExceptionType EX_SELF =
            new DynamicCommandExceptionType(name ->
                    new LiteralMessage("You already have the username \""+name+"\"")
            );

    @Override
    public String getLabel() {
        return "username";
    }

    @Override
    public String getDescription() {
        return "Changes your username on the server";
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder.requires(src ->
                src.getSender().hasPermission("extras.username")
                        && src.getSender() instanceof Player
        );

        for (final String alias : List.of("auto", "default", "reset")) {
            builder.requires(src ->
                    src.getSender().hasPermission("extras.username")
                            && src.getSender() instanceof Player
            ).then(literal(alias).executes(ctx -> {
                final Player player = player(ctx);
                final String original = UsernameManager.getOriginalName(player);

                if (original.equals(player.getName())) {
                    throw EX_DEFAULT.create();
                }

                checkUsername(player, original);
                UsernameManager.resetUsername(player);
                sendSuccess(player, original);
                return Command.SINGLE_SUCCESS;
            }));
        }

        builder.then(argument("username", greedyString())
                .executes(ctx -> {
                    final Player player = player(ctx);

                    final String nameColor =
                            Utility.translateLegacyColors(
                                    StringArgumentType.getString(
                                            ctx,
                                            "username"
                                    )
                            );
                    final String name = nameColor.substring(0, Math.min(16,
                            nameColor.length()));

                    if (name.equals(player.getName())) {
                        throw EX_SELF.create(name);
                    }

                    checkUsername(player, name);
                    UsernameManager.setUsername(player, name);
                    sendSuccess(player, name);
                    return Command.SINGLE_SUCCESS;
                }));
    }

    private static void checkUsername(final Player player, final String name)
            throws CommandSyntaxException {
        if (UsernameManager.isRateLimited(player)) {
            throw EX_RATELIMIT.create();
        }

        if (UsernameManager.isNameTaken(player, name)) {
            throw EX_TAKEN.create();
        }
    }

    private static void sendSuccess(final Player player, final String name) {
        player.sendMessage(
                Component.text("Successfully set your username to \"")
                        .append(Component.text(name))
                        .append(Component.text("\""))
        );
    }

    private static Player player(final CommandContext<CommandSourceStack> ctx)
            throws CommandSyntaxException {
        if (ctx.getSource().getSender() instanceof final Player player) {
            return player;
        }
        throw EX_NOT_PLAYER.create();
    }
}
