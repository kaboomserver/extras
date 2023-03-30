package pw.kaboom.extras.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pw.kaboom.extras.Main;
import pw.kaboom.extras.skin.SkinManager;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public final class CommandSkin implements CommandExecutor {
    private final Map<Player, Long> lastUsedMillis = new HashMap<>();

    @Override
    public boolean onCommand(final @Nonnull CommandSender sender,
                             final @Nonnull Command command,
                             final @Nonnull String label,
                             final String[] args) {
        final Main plugin = JavaPlugin.getPlugin(Main.class);

        if (plugin.isFolia()) {
            sender.sendMessage(Component
                    .text("Command cannot be ran on Folia servers!"));
            return true;
        }

        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(Component
                    .text("Command has to be run by a player"));
            return true;
        }

        final Player player = (Player) sender;
        final long millis = lastUsedMillis.getOrDefault(player, 0L);
        final long millisDifference = System.currentTimeMillis() - millis;

        if (args.length == 0) {
            player.sendMessage(Component
                    .text("Usage: /" + label + " <username>\n/" + label + " off",
                            NamedTextColor.RED));
            return true;
        }

        if (millisDifference <= 2000) {
            player.sendMessage(Component
                    .text("Please wait a few seconds before changing your skin"));
            return true;
        }

        lastUsedMillis.put(player, System.currentTimeMillis());

        final String name = args[0];

        if (name.equalsIgnoreCase("off") || name.equalsIgnoreCase("remove")
         || name.equalsIgnoreCase("disable")) {
            SkinManager.resetSkin(player, true);
            return true;
        }

        if (name.equalsIgnoreCase("auto") || name.equalsIgnoreCase("default")
        || name.equalsIgnoreCase("reset")) {
            SkinManager.applySkin(player, player.getName(), true);
            return true;
        }

        final boolean shouldSendMessage = true;

        SkinManager.applySkin(player, name, shouldSendMessage);
        return true;
    }
}
