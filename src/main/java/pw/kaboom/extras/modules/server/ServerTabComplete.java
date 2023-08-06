package pw.kaboom.extras.modules.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;

public final class ServerTabComplete implements Listener {
    private static final HashMap<UUID, String> loginNameList = new HashMap<>();

    @EventHandler
    void onAsyncTabComplete(final AsyncTabCompleteEvent event) {
        final String[] arr = event.getBuffer().split(" ", 2);

        // Vanilla clients will not send tab complete requests on the first word,
        // but modified or bot clients may
        if (arr.length < 2) {
            return;
        }

        String command = arr[0];
        String argsFragment = arr[1];
        if (command.startsWith("/")) {
            command = command.substring(1);
        }

        if (command.equalsIgnoreCase("op")) {
            event.setCompletions(getOpCompletions(argsFragment));
        } else if (command.equalsIgnoreCase("deop")) {
            event.setCompletions(getDeopCompletions(argsFragment));
        } else {
            return;
        }

        if (event.getCompletions().isEmpty()) {
            event.setCancelled(true);
        }
    }

    static List<String> getOpCompletions(final String argsFragment) {
        final List<String> deops = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.isOp()) {
                String loginName = loginNameList.get(player.getUniqueId());
                if (loginName != null && loginName.startsWith(argsFragment)) {
                    deops.add(loginName);
                }
            }
        }
        return deops;
    }

    static List<String> getDeopCompletions(final String argsFragment) {
        final List<String> ops = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOp()) {
                String loginName = loginNameList.get(player.getUniqueId());
                if (loginName != null && loginName.startsWith(argsFragment)) {
                    ops.add(loginName);
                }
            }
        }
        return ops;
    }

    public static HashMap<UUID, String> getLoginNameList() {
        return loginNameList;
    }
}
