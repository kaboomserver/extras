package pw.kaboom.extras.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import pw.kaboom.extras.util.Utility;

public final class CommandSpawn implements BrigadierCommand {
    @Override
    public String getLabel() {
        return "spawn";
    }

    @Override
    public String getDescription() {
        return "Teleports you to spawn";
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder
                .requires(src ->
                        src.getSender().hasPermission("extras.spawn")
                                && src.getSender() instanceof Player
                )
                .executes(ctx -> {
                    if (!(ctx.getSource().getSender() instanceof final Player player)) {
                        throw new IllegalStateException("This command must be called by a player");
                    }
                    Utility.teleportToSpawn(player, PlayerTeleportEvent.TeleportCause.COMMAND);
                    player.sendMessage(Component.text("Successfully moved to spawn"));
                    return Command.SINGLE_SUCCESS;
                });
    }
}
