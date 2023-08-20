package pw.kaboom.extras.util;

import org.bukkit.Bukkit;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class Utility {
    public static @Nullable Player getPlayerExactIgnoreCase(final String username) {
        return Bukkit.getOnlinePlayers()
                .stream()
                .filter(p -> p.getName().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
    }

    public static void resetAttribute(final AttributeInstance attribute) {
        for (final AttributeModifier modifier: attribute.getModifiers()) {
            attribute.removeModifier(modifier);
        }

        attribute.setBaseValue(attribute.getDefaultValue());
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
}
