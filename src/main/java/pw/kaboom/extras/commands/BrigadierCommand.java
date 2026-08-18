package pw.kaboom.extras.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;

import java.util.List;

public interface BrigadierCommand {
    String getLabel();

    String getDescription();

    default List<String> getAliases() {
        return List.of();
    }

    void build(final LiteralArgumentBuilder<CommandSourceStack> builder);
}
