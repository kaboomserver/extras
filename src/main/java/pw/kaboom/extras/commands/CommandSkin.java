package pw.kaboom.extras.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pw.kaboom.extras.modules.player.skin.SkinManager;

import javax.annotation.Nonnull;

public final class CommandSkin implements CommandExecutor {
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

        if (args.length == 0) {
            player.sendMessage(Component
                    .text("Usage: /" + label + " <username>\n/" + label + " off",
                            NamedTextColor.RED));
            return true;
        }

        final String name = args[0];

        if (name.equalsIgnoreCase("off") || name.equalsIgnoreCase("remove")
         || name.equalsIgnoreCase("disable")) {
            SkinManager.removeSkin(player, true);
            return true;
        }

        if (name.equalsIgnoreCase("auto") || name.equalsIgnoreCase("default")
        || name.equalsIgnoreCase("reset")) {
            SkinManager.requestSkin(player, player.getName(), true);
            return true;
        }

        SkinManager.requestSkin(player, name, true);
        return true;
    }
}
