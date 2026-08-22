package pw.kaboom.extras.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;

import java.util.List;

import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static io.papermc.paper.command.brigadier.Commands.argument;

public final class CommandBroadcastRaw implements BrigadierCommand {
    private static final LegacyComponentSerializer SERIALIZER =
            LegacyComponentSerializer.legacyAmpersand();


    @Override
    public String getLabel() {
        return "broadcastraw";
    }

    @Override
    public String getDescription() {
        return "Broadcasts raw text to the server";
    }

    @Override
    public List<String> getAliases() {
        return List.of("bcraw", "tellraw");
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder
                .requires(src ->
                        src.getSender().hasPermission("extras.broadcastraw")
                )
                .then(argument("message", greedyString())
                        .executes(ctx -> {
                            Bukkit.broadcast(
                                    SERIALIZER.deserialize(
                                            StringArgumentType.getString(ctx, "message")
                                    )
                            );
                            return Command.SINGLE_SUCCESS;
                        })
                );
    }
}
