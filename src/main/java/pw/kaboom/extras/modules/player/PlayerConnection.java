package pw.kaboom.extras.modules.player;

import com.destroystokyo.paper.event.profile.PreLookupProfileEvent;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.common.base.Charsets;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.plugin.java.JavaPlugin;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;
import pw.kaboom.extras.Main;
import pw.kaboom.extras.modules.server.ServerTabComplete;
import pw.kaboom.extras.modules.player.skin.SkinManager;
import pw.kaboom.extras.util.Utility;

import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public final class PlayerConnection implements Listener {
    private static final FileConfiguration CONFIG = JavaPlugin.getPlugin(Main.class).getConfig();
    private static final String TITLE = CONFIG.getString("playerJoinTitle");
    private static final String SUBTITLE = CONFIG.getString("playerJoinSubtitle");
    private static final int FADE_IN = 10;
    private static final int STAY = 160;
    private static final int FADE_OUT = 5;

    private static final boolean ENABLE_KICK = CONFIG.getBoolean("enableKick");
    private static final boolean ENABLE_JOIN_RESTRICTIONS = CONFIG.getBoolean(
                                                            "enableJoinRestrictions");
    private static final boolean ALLOW_JOIN_ON_FULL_SERVER = CONFIG.getBoolean(
                                                             "allowJoinOnFullServer");
    private static final boolean OP_ON_JOIN = CONFIG.getBoolean("opOnJoin");
    private static final boolean RANDOMIZE_SPAWN = CONFIG.getBoolean("randomizeSpawn");

    @EventHandler
    void onAsyncPlayerPreLogin(final AsyncPlayerPreLoginEvent event) {
        final Player player = Utility.getPlayerExactIgnoreCase(event.getName());

        if (player != null) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                           "A player with that username is already logged in");
        }

        /*try {
            final PlayerProfile profile = event.getPlayerProfile();

            UUID offlineUUID = UUID.nameUUIDFromBytes(
                ("OfflinePlayer:" + event.getName()).getBytes(Charsets.UTF_8));

            profile.setId(offlineUUID);

            SkinDownloader skinDownloader = new SkinDownloader();
            skinDownloader.fillJoinProfile(profile, event.getName(), event.getUniqueId());
        } catch (Exception ignored) {
        }*/
    }

    @EventHandler
    void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        if (TITLE != null
                || SUBTITLE != null) {
                player.sendTitle(
                TITLE,
                SUBTITLE,
                FADE_IN,
                STAY,
                FADE_OUT
            );
        }

        ServerTabComplete.getLoginNameList().put(player.getUniqueId(), player.getName());
    }

    @EventHandler
    void onPlayerKick(final PlayerKickEvent event) {
        if (!ENABLE_KICK) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    void onPlayerLogin(final PlayerLoginEvent event) {
        // #312 - If allow join on full server is off,
        // but join restrictions are disabled,
        // player can still join on full server

        // Full server kicks should be handled differently from other join restrictions
        // since we have a separate configuration value for it

        if (!ENABLE_JOIN_RESTRICTIONS && !Result.KICK_FULL.equals(event.getResult())) {
            event.allow();
        }

        if (Result.KICK_FULL.equals(event.getResult()) && ALLOW_JOIN_ON_FULL_SERVER) {
            event.allow();
        }

        final Player player = event.getPlayer();

        if (OP_ON_JOIN && !player.isOp()) {
            player.setOp(true);
        }

        final Server server = Bukkit.getServer();


        if (!server.getOnlineMode()) {
            SkinManager.applySkin(player, player.getName(), false);
        }
    }

    @EventHandler
    void onPlayerSpawn(final PlayerSpawnLocationEvent event) {
        if (RANDOMIZE_SPAWN
                && event.getPlayer().getBedSpawnLocation() != event.getSpawnLocation()) {
            final World world = event.getPlayer().getWorld();
            final ThreadLocalRandom random = ThreadLocalRandom.current();

            final double teleportAmount = 500000D;
            final Location location = new Location(
                world,
                random.nextDouble(-teleportAmount, teleportAmount), 
                100, 
                random.nextDouble(-teleportAmount, teleportAmount)
            );
            event.setSpawnLocation(location);
        }
    }

    @EventHandler
    void onPlayerQuit(final PlayerQuitEvent event) {
        PlayerCommand.getCommandMillisList().remove(event.getPlayer().getUniqueId());
        //PlayerInteract.interactMillisList.remove(event.getPlayer().getUniqueId());
        ServerTabComplete.getLoginNameList().remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    void onPreLookupProfile(final PreLookupProfileEvent event) {
        // Disable Mojang API calls, we don't need them
        UUID offlineUUID = UUID.nameUUIDFromBytes(
            ("OfflinePlayer:" + event.getName()).getBytes(Charsets.UTF_8));
        event.setUUID(offlineUUID);

        event.setProfileProperties(new HashSet<ProfileProperty>());
    }
}
