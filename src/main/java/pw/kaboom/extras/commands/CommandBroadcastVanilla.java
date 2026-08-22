package pw.kaboom.extras.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;

import java.util.List;

import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static io.papermc.paper.command.brigadier.Commands.argument;

public final class CommandBroadcastVanilla implements BrigadierCommand {
    private static final LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER
            = LegacyComponentSerializer.legacyAmpersand();

    @Override
    public String getLabel() {
        return "broadcastvanilla";
    }

    @Override
    public String getDescription() {
        return "Broadcasts text in vanilla style";
    }

    @Override
    public List<String> getAliases() {
        return List.of("bcv");
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder
                .requires(src ->
                        src.getSender().hasPermission("extras.broadcastraw")
                )
                .then(argument("message", greedyString())
                        .executes(ctx -> {
                            final Component senderName = ctx.getSource().getSender().name();
                            final String input = StringArgumentType.getString(ctx, "message");
                            final Component component =
                                    LEGACY_COMPONENT_SERIALIZER.deserialize(input);
                            final Component broadcastComponent =
                                    Component.translatable("chat.type.admin",
                                                    senderName,
                                                    component
                                            )
                                            .decorate(TextDecoration.ITALIC)
                                            .color(NamedTextColor.GRAY);
                            Bukkit.broadcast(broadcastComponent);
                            return Command.SINGLE_SUCCESS;
                        })
                );
    }
}
