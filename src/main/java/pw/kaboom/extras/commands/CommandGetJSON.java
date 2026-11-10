package pw.kaboom.extras.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.List;

import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static io.papermc.paper.command.brigadier.Commands.argument;

public final class CommandGetJSON implements BrigadierCommand {
    private static final JSONComponentSerializer SERIALIZER = JSONComponentSerializer.json();

    @Override
    public String getLabel() {
        return "getjson";
    }

    @Override
    public String getDescription() {
        return "Gets the JSON of a deserialized legacy component";
    }

    @Override
    public List<String> getAliases() {
        return List.of("gj");
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder
                .requires(src -> src.getSender().hasPermission("extras.getjson"))
                .then(argument("message", greedyString())
                        .executes(ctx -> {
                            final Component createdComponent = LegacyComponentSerializer
                                    .legacyAmpersand()
                                    .deserialize(StringArgumentType.getString(ctx, "message"));

                            final String asJson = SERIALIZER.serialize(createdComponent);

                            final Component feedback = Component.empty()
                                    .append(Component.text("Your component as JSON (click to " +
                                            "copy): "))
                                    .append(Component.text(asJson, NamedTextColor.GREEN))
                                    .clickEvent(ClickEvent.copyToClipboard(asJson));

                            ctx.getSource().getSender().sendMessage(feedback);
                            return Command.SINGLE_SUCCESS;
                        })
                );
    }
}
