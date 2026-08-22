package pw.kaboom.extras.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;

import java.util.List;

public final class CommandClearChat implements BrigadierCommand {
    private static final Component CLEAR_CHAT_COMPONENT;

    static {
        final int maxMessages = 100;
        Component clearChatComponent = Component.empty();
        for (int i = 0; i < maxMessages; i++) {
            clearChatComponent = clearChatComponent
                    .append(Component.newline());
        }
        CLEAR_CHAT_COMPONENT = clearChatComponent
                .append(Component.text("The chat has been cleared")
                        .color(NamedTextColor.DARK_GREEN));
    }


    @Override
    public String getLabel() {
        return "clearchat";
    }

    @Override
    public String getDescription() {
        return "Clears messages from the chat";
    }

    @Override
    public List<String> getAliases() {
        return List.of("cc");
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder
                .requires(src -> src.getSender().hasPermission("extras.clearchat"))
                .executes(_ -> {
                    Bukkit.broadcast(CLEAR_CHAT_COMPONENT);
                    return Command.SINGLE_SUCCESS;
                });
    }
}
