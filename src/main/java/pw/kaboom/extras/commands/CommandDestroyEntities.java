package pw.kaboom.extras.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.List;

public final class CommandDestroyEntities implements BrigadierCommand {

    @Override
    public String getLabel() {
        return "destroyentities";
    }

    @Override
    public String getDescription() {
        return "Destroys all entities in every world";
    }

    @Override
    public List<String> getAliases() {
        return List.of("de");
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder
                .requires(src -> src.getSender().hasPermission("extras.destroyentities"))
                .executes(ctx -> {
                    int entityCount = 0;
                    int worldCount = 0;

                    for (final World world : Bukkit.getWorlds()) {
                        for (final Entity entity : world.getEntities()) {
                            if (!EntityType.PLAYER.equals(entity.getType())) {
                                try {
                                    entity.remove();
                                    entityCount++;
                                } catch (Exception _) {
                                    // Broken entity
                                }
                            }
                        }
                        worldCount++;
                    }

                    ctx.getSource().getSender().sendMessage(
                            Component.text("Successfully destroyed ")
                                    .append(Component.text(entityCount))
                                    .append(Component.text(" entities in "))
                                    .append(Component.text(worldCount))
                                    .append(Component.text(" worlds"))
                    );
                    return entityCount;
                });
    }
}
