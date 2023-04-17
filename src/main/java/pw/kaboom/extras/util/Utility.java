package pw.kaboom.extras.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public final class Utility {
    public static @Nullable Player getPlayerExactIgnoreCase(final String username) {
        return Bukkit.getOnlinePlayers()
                .stream()
                .filter(p -> p.getName().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
    }
}
