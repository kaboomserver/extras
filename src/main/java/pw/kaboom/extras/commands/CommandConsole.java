package pw.kaboom.extras.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import pw.kaboom.extras.util.Utility;

import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static io.papermc.paper.command.brigadier.Commands.argument;

public final class CommandConsole implements BrigadierCommand {
    @Override
    public String getLabel() {
        return "console";
    }

    @Override
    public String getDescription() {
        return "Broadcasts a message as the console";
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder
                .requires(src -> src.getSender().hasPermission("extras.console"))
                .then(argument("message", greedyString()).executes(ctx -> {
                    Bukkit.dispatchCommand(
                            Bukkit.getConsoleSender(),
                            "minecraft:say " + Utility.translateLegacyColors(
                                    StringArgumentType.getString(ctx, "message")
                            )
                    );
                    return Command.SINGLE_SUCCESS;
                }));
    }
}
