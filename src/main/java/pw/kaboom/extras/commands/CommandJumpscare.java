package pw.kaboom.extras.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

public final class CommandJumpscare implements CommandExecutor {
    private void createJumpscare(final Player player) {
        final int count = 4;
        player.spawnParticle(Particle.MOB_APPEARANCE, player.getLocation(), count);

        final int maxIterator = 10;
        for (int i = 0; i <= maxIterator; i++) {
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 1, 0);
        }
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
        } else {
            if (args[0].equals("*") || args[0].equals("**")) {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    createJumpscare(onlinePlayer);
                }
                sender.sendMessage("Successfully created jumpscare for every player");
            } else {
                final Player target = Bukkit.getPlayer(args[0]);
                if (target != null) {
                    createJumpscare(target);
                    sender.sendMessage("Successfully created jumpscare for player \"" + target.getName() + "\"");
                } else {
                    sender.sendMessage("Player \"" + args[0] + "\" not found");
                }
            }
        }
        return true;
    }
}
