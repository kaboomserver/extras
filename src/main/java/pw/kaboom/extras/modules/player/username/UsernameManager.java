package pw.kaboom.extras.modules.player.username;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class UsernameManager implements Listener {
    private static final long RATELIMIT_MILLIS = 2000;
    private static final Map<Player, Long> lastUsedMillis = new HashMap<>();
    private static final Map<Player, String> originalNames = new HashMap<>();

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        originalNames.remove(player);
        lastUsedMillis.remove(player);
    }

    public static String getOriginalName(final Player player) {
        return originalNames.getOrDefault(player, player.getName());
    }

    public static boolean isRateLimited(final Player player) {
        final long millis = lastUsedMillis.getOrDefault(player, 0L);
        return System.currentTimeMillis() - millis <= RATELIMIT_MILLIS;
    }

    public static boolean isNameTaken(final Player player, final String name) {
        for (final Player other : Bukkit.getOnlinePlayers()) {
            if (other.equals(player) || !other.getName().equalsIgnoreCase(name)) {
                continue;
            }
            return true;
        }
        return false;
    }

    public static void setUsername(final Player player, final String name) {
        final UUID uuid = player.getUniqueId();
        originalNames.putIfAbsent(player, player.getName());

        // Preserve UUIDs, as changing them breaks clients
        final PlayerProfile newProfile = Bukkit.createProfileExact(uuid, name);
        
        player.setPlayerProfile(newProfile);
        lastUsedMillis.put(player, System.currentTimeMillis());
    }

    public static void resetUsername(final Player player) {
        setUsername(player, getOriginalName(player));
        originalNames.remove(player);
    }
}
