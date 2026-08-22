package pw.kaboom.extras.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;

import java.util.List;

import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static io.papermc.paper.command.brigadier.Commands.argument;

public final class CommandBroadcastMiniMessage implements BrigadierCommand {
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    @Override
    public String getLabel() {
        return "broadcastminimessage";
    }

    @Override
    public String getDescription() {
        return "Broadcasts a deserialized MiniMessage component";
    }

    @Override
    public List<String> getAliases() {
        return List.of("broadcastmm", "bcmm");
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder
                .requires(src -> src.getSender().hasPermission("extras.broadcastminimessage"))
                .then(argument("message", greedyString())
                        .executes(ctx -> {
                            final String mm = StringArgumentType.getString(ctx, "message");
                            final Component component = MINI_MESSAGE.deserialize(mm);
                            Bukkit.broadcast(component);
                            return Command.SINGLE_SUCCESS;
                        })
                );
    }
}
