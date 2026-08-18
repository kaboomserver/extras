package pw.kaboom.extras.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import pw.kaboom.extras.Main;
import pw.kaboom.extras.modules.player.PlayerPrefix;

import java.io.IOException;
import java.util.List;

import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static io.papermc.paper.command.brigadier.Commands.*;

public final class CommandPrefix implements BrigadierCommand {
    private static final int MAX_PREFIX_LENGTH = 1024;
    private static final SimpleCommandExceptionType EX_SOMETHING =
            new SimpleCommandExceptionType(new LiteralMessage(
                    "Something went wrong while saving the prefix. Please check console."));

    private static final Logger log = JavaPlugin.getPlugin(Main.class).getSLF4JLogger();

    @Override
    public String getLabel() {
        return "prefix";
    }

    @Override
    public String getDescription() {
        return "Changes your tag";
    }

    @Override
    public List<String> getAliases() {
        return List.of("rank", "tag");
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder
                .requires(restricted(src ->
                        src.getSender().hasPermission("extras.prefix")
                                && src.getSender() instanceof Player
                ))
                .then(literal("off")
                        .executes(ctx -> {
                            final Player player = (Player) ctx.getSource().getSender();
                            setPrefix(player, null);
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .then(argument("prefix", greedyString())
                        .executes(ctx -> {
                            final Player player = (Player) ctx.getSource().getSender();
                            String legacyPrefix = StringArgumentType.getString(ctx, "prefix");
                            if (legacyPrefix.length() > MAX_PREFIX_LENGTH) {
                                legacyPrefix = legacyPrefix.substring(0, MAX_PREFIX_LENGTH);
                            }
                            setPrefix(player, legacyPrefix);
                            return Command.SINGLE_SUCCESS;
                        })
                );
    }

    private static void setPrefix(final Player player, final @Nullable String prefix)
            throws CommandSyntaxException {
        try {
            if (prefix == null) {
                PlayerPrefix.removePrefix(player);
                player.sendMessage(
                        Component.text("You no longer have a tag")
                );
                return;
            }
            final Component finalPrefix = PlayerPrefix.setPrefix(player, prefix);
            player.sendMessage(Component.text("You now have the tag: ")
                    .append(finalPrefix));
        } catch (final IOException e) {
            log.error("Exception while saving prefix", e);
            throw EX_SOMETHING.create();
        }
    }
}
