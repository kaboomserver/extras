package pw.kaboom.extras.helpers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.earth2me.essentials.IEssentials;

public class PlayerMuting {
    private static IEssentials essentials = (IEssentials) Bukkit.getServer()
        .getPluginManager()
        .getPlugin("Essentials");

    public static boolean isMuted(Player player) {
        return essentials != null && essentials.getUser(player).isMuted();
    } 
}
