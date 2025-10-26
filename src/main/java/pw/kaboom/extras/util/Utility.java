package pw.kaboom.extras.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import io.papermc.paper.connection.PlayerConfigurationConnection;
import io.papermc.paper.connection.PlayerConnection;
import io.papermc.paper.connection.PlayerLoginConnection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.function.Function;

public final class Utility {
    @SuppressWarnings("UnstableApiUsage")
    public static @Nullable UUID getConnectionUuid(final PlayerConnection connection) {
        // https://discord.com/channels/289587909051416579/555462289851940864/1391545447495237637
        // Thanks, Paper!
        final PlayerProfile profile;
        if ((connection instanceof final PlayerLoginConnection loginConnection)) {
            profile = loginConnection.getAuthenticatedProfile() != null
                    ? loginConnection.getAuthenticatedProfile()
                    : loginConnection.getUnsafeProfile();
        } else if ((connection instanceof final PlayerConfigurationConnection configConnection)) {
            profile = configConnection.getProfile();
        } else {
            profile = null;
        }

        return profile != null ? profile.getId() : null;
    }

    public static void teleportToSpawn(final Player player,
                                       final PlayerTeleportEvent.TeleportCause cause) {
        final World world = player.getServer().getRespawnWorld();
        final Location spawnLocation = world.getSpawnLocation();

        final int y = world.getHighestBlockYAt(spawnLocation);
        final Location location = new Location(world,
            spawnLocation.x(), y + 1, spawnLocation.z(),
            spawnLocation.getYaw(), spawnLocation.getPitch());
        player.teleportAsync(location, cause);
    }

    public static @Nullable Player getPlayerExactIgnoreCase(final String username) {
        return Bukkit.getOnlinePlayers()
                .stream()
                .filter(p -> p.getName().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
    }

    public static <T extends Entity & Attributable> void resetAttribute (final T entity,
                                                                         final Attribute attrib) {
        final AttributeInstance instance = entity.getAttribute(attrib);
        if (instance == null) return;

        for (final AttributeModifier modifier : instance.getModifiers()) {
            instance.removeModifier(modifier);
        }

        final AttributeInstance defaultInstance = entity.getType().getDefaultAttributes()
                .getAttribute(instance.getAttribute());
        if (defaultInstance != null) {
            instance.setBaseValue(defaultInstance.getBaseValue());
        } else {
            instance.setBaseValue(instance.getDefaultValue());
        }
    }

    // TODO: Support hex color codes, too (they aren't supported in Spigot either)
    public static String translateLegacyColors(@Nonnull String text) {
        char[] b = text.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx".indexOf(b[i + 1]) > -1) {
                b[i] = '\u00a7';
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }

    public static <T, R> @Nullable R composeCallable(
            Callable<T> callable,
            Function<T, R> composer
    ) {
        try {
            return composer.apply(callable.call());
        } catch (Throwable ex) {
            return null;
        }
    }
}
