package pw.kaboom.extras.commands;

import com.destroystokyo.paper.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pw.kaboom.extras.util.Utility;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public final class CommandUsername implements CommandExecutor {
    private final Map<Player, Long> lastUsedMillis = new HashMap<>();

    @Override
    public boolean onCommand(final @Nonnull CommandSender sender,
                             final @Nonnull Command command,
                             final @Nonnull String label,
                             final String[] args) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Component
                    .text("Command has to be run by a player"));
            return true;
        }

        final String nameColor = Utility.translateLegacyColors(String.join(" ", args));
        final String name = nameColor.substring(0, Math.min(16, nameColor.length()));
        final long millis = lastUsedMillis.getOrDefault(player, 0L);
        final long millisDifference = System.currentTimeMillis() - millis;

        if (args.length == 0) {
            player.sendMessage(Component
                    .text("Usage: /" + label + " <username>",
                            NamedTextColor.RED));
            return true;
        }

        if (name.equals(player.getName())) {
            player.sendMessage(Component
                    .text("You already have the username \"" + name + "\""));
            return true;
        }

        if (millisDifference <= 2000) {
            player.sendMessage(Component
                    .text("Please wait a few seconds before changing your username."));
            return true;
        }

        for (Player other : Bukkit.getOnlinePlayers()) {
            if (!other.getName().equalsIgnoreCase(name)) continue;

            player.sendMessage(Component
                    .text("A player with that username is already logged in."));
            return true;
        }

        // Preserve UUIDs, as changing them breaks clients
        final PlayerProfile newProfile = Bukkit.createProfileExact(player.getUniqueId(), name);
        newProfile.setProperties(player.getPlayerProfile().getProperties());

        player.setPlayerProfile(newProfile);
        lastUsedMillis.put(player, System.currentTimeMillis());

        player.sendMessage(
            Component.text("Successfully set your username to \"")
                .append(Component.text(name))
                .append(Component.text("\""))
        );
        return true;
    }
}
