package pw.kaboom.extras.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public final class CommandJumpscare implements CommandExecutor {
    private void createJumpscare(final Player player) {
        final int count = 4;
        player.spawnParticle(Particle.MOB_APPEARANCE, player.getLocation(), count);

        final int maxIterator = 10;
        for (int i = 0; i <= maxIterator; i++) {
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 1, 0);
        }
    }

    public boolean onCommand(final @Nonnull CommandSender sender,
                             final @Nonnull Command command,
                             final @Nonnull String label,
                             final String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Component
                    .text("Usage: /" + label + " <player>",
                            NamedTextColor.RED));
        } else {
            if (args[0].equals("*") || args[0].equals("**")) {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    createJumpscare(onlinePlayer);
                }
                sender.sendMessage(Component
                        .text("Successfully created jumpscare for every player"));
            } else {
                final Player target = Bukkit.getPlayer(args[0]);
                if (target != null) {
                    createJumpscare(target);
                    sender.sendMessage(Component
                            .text("Successfully created jumpscare for player \""
                                    + target.getName() + "\""));
                } else {
                    sender.sendMessage(Component
                            .text("Player \"" + args[0] + "\" not found"));
                }
            }
        }
        return true;
    }
}
