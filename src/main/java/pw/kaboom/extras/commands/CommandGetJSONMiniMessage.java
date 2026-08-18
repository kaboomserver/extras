package pw.kaboom.extras.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import java.util.List;

import static io.papermc.paper.command.brigadier.Commands.argument;

public final class CommandGetJSONMiniMessage implements BrigadierCommand {
    @Override
    public String getLabel() {
        return "getjsonminimessage";
    }

    @Override
    public String getDescription() {
        return "Gets the JSON of a deserialized MiniMessage component";
    }

    @Override
    public List<String> getAliases() {
        return List.of("getjsonmm","jmm");
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder
                .requires(src ->
                        src.getSender().hasPermission("extras.getjsonmm")
                )
                .then(argument("message", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            final Component createdComponent = MiniMessage
                                    .miniMessage().deserialize(
                                            StringArgumentType.getString(ctx, "message")
                                    );

                            final String asJson = GsonComponentSerializer.gson()
                                    .serialize(createdComponent);

                            final Component feedback = Component.empty()
                                    .append(Component.text("Your component as JSON (click" +
                                            " to copy): "))
                                    .append(Component.text(asJson, NamedTextColor.GREEN))
                                    .clickEvent(ClickEvent.copyToClipboard(asJson));

                            ctx.getSource().getSender().sendMessage(feedback);
                            return Command.SINGLE_SUCCESS;
                        }));
    }
}
