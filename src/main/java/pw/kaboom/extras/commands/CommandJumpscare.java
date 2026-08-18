package pw.kaboom.extras.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.Component;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.argument.ArgumentTypes.players;

public final class CommandJumpscare implements BrigadierCommand {
    @Override
    public String getLabel() {
        return "jumpscare";
    }

    @Override
    public String getDescription() {
        return "Scares a player";
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder
                .requires(src ->
                        src.getSender().hasPermission("extras.jumpscare")
                )
                .then(argument("players", players())
                        .executes(ctx -> {
                            final PlayerSelectorArgumentResolver selector = ctx.getArgument(
                                    "players",
                                    PlayerSelectorArgumentResolver.class
                            );
                            final List<Player> targets = selector.resolve(ctx.getSource());
                            for (final Player target : targets) {
                                createJumpscare(target);
                            }
                            if (targets.size() == 1) {
                                ctx.getSource().getSender().sendMessage(
                                        Component.text("Successfully created jumpscare for " +
                                                        "player \"")
                                                .append(Component.text(
                                                        targets.getFirst().getName())
                                                )
                                                .append(Component.text("\""))
                                );
                            } else {
                                ctx.getSource().getSender().sendMessage(
                                        Component.text("Successfully created jumpscare for "
                                                + targets.size()
                                                + " players")
                                );
                            }
                            return targets.size();
                        })
                );
    }

    private void createJumpscare(final Player player) {
        final int count = 4;
        player.spawnParticle(Particle.ELDER_GUARDIAN, player.getLocation(), count);

        final int maxIterator = 10;
        for (int i = 0; i <= maxIterator; i++) {
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 1, 0);
        }
    }
}
