package pw.kaboom.extras;

import java.io.File;
import java.util.Collections;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import pw.kaboom.extras.commands.CommandBroadcastMM;
import pw.kaboom.extras.commands.CommandBroadcastVanilla;
import pw.kaboom.extras.commands.CommandClearChat;
import pw.kaboom.extras.commands.CommandConsole;
import pw.kaboom.extras.commands.CommandDestroyEntities;
import pw.kaboom.extras.commands.CommandEnchantAll;
import pw.kaboom.extras.commands.CommandGetJSON;
import pw.kaboom.extras.commands.CommandJumpscare;
import pw.kaboom.extras.commands.CommandKaboom;
import pw.kaboom.extras.commands.CommandPing;
import pw.kaboom.extras.commands.CommandPrefix;
import pw.kaboom.extras.commands.CommandPumpkin;
import pw.kaboom.extras.commands.CommandServerInfo;
import pw.kaboom.extras.commands.CommandSkin;
import pw.kaboom.extras.commands.CommandSpawn;
import pw.kaboom.extras.commands.CommandSpidey;
import pw.kaboom.extras.commands.CommandTellraw;
import pw.kaboom.extras.commands.CommandUsername;
import pw.kaboom.extras.modules.block.BlockCheck;
import pw.kaboom.extras.modules.block.BlockPhysics;
import pw.kaboom.extras.modules.entity.EntityExplosion;
import pw.kaboom.extras.modules.entity.EntityKnockback;
import pw.kaboom.extras.modules.entity.EntitySpawn;
import pw.kaboom.extras.modules.entity.EntityTeleport;
import pw.kaboom.extras.modules.player.PlayerChat;
import pw.kaboom.extras.modules.player.PlayerCommand;
import pw.kaboom.extras.modules.player.PlayerConnection;
import pw.kaboom.extras.modules.player.PlayerDamage;
import pw.kaboom.extras.modules.player.PlayerInteract;
import pw.kaboom.extras.modules.player.PlayerPrefix;
import pw.kaboom.extras.modules.player.PlayerRecipe;
import pw.kaboom.extras.modules.player.PlayerTeleport;
import pw.kaboom.extras.modules.server.ServerCommand;
import pw.kaboom.extras.modules.server.ServerGameRule;
import pw.kaboom.extras.modules.server.ServerTabComplete;
import pw.kaboom.extras.modules.server.ServerTick;

public final class Main extends JavaPlugin {
    private File prefixConfigFile;
    private FileConfiguration prefixConfig;

    @Override
    public void onLoad() {
        /* Fill lists */
        Collections.addAll(
                BlockPhysics.getBlockFaces(),
                BlockFace.NORTH,
                BlockFace.SOUTH,
                BlockFace.WEST,
                BlockFace.EAST,
                BlockFace.UP
        );

        /* Load missing config.yml defaults */
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    @Override
    public void onEnable() {
        /* Prefixes */
        prefixConfigFile = new File(this.getDataFolder(), "prefixes.yml");
        prefixConfig = YamlConfiguration.loadConfiguration(prefixConfigFile);

        /* Commands */
        this.getCommand("broadcastminimessage").setExecutor(new CommandBroadcastMM());
        this.getCommand("broadcastvanilla").setExecutor(new CommandBroadcastVanilla());
        this.getCommand("clearchat").setExecutor(new CommandClearChat());
        this.getCommand("console").setExecutor(new CommandConsole());
        this.getCommand("destroyentities").setExecutor(new CommandDestroyEntities());
        this.getCommand("enchantall").setExecutor(new CommandEnchantAll());
        this.getCommand("getjson").setExecutor(new CommandGetJSON());
        this.getCommand("jumpscare").setExecutor(new CommandJumpscare());
        this.getCommand("kaboom").setExecutor(new CommandKaboom());
        this.getCommand("ping").setExecutor(new CommandPing());
        this.getCommand("prefix").setExecutor(new CommandPrefix());
        this.getCommand("pumpkin").setExecutor(new CommandPumpkin());
        this.getCommand("serverinfo").setExecutor(new CommandServerInfo());
        this.getCommand("skin").setExecutor(new CommandSkin());
        this.getCommand("spawn").setExecutor(new CommandSpawn());
        this.getCommand("spidey").setExecutor(new CommandSpidey());
        this.getCommand("tellraw").setExecutor(new CommandTellraw());
        this.getCommand("username").setExecutor(new CommandUsername());

        /* Block-related modules */
        BlockPhysics.init(this);

        this.getServer().getPluginManager().registerEvents(new BlockCheck(), this);
        this.getServer().getPluginManager().registerEvents(new BlockPhysics(), this);

        /* Entity-related modules */
        this.getServer().getPluginManager().registerEvents(new EntityExplosion(), this);
        this.getServer().getPluginManager().registerEvents(new EntityKnockback(), this);
        this.getServer().getPluginManager().registerEvents(new EntitySpawn(), this);
        this.getServer().getPluginManager().registerEvents(new EntityTeleport(), this);

        /* Player-related modules */
        this.getServer().getPluginManager().registerEvents(new PlayerChat(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerCommand(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerConnection(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerDamage(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerInteract(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerRecipe(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerTeleport(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerPrefix(), this);

        /* Server-related modules */
        this.getServer().getPluginManager().registerEvents(new ServerCommand(), this);
        this.getServer().getPluginManager().registerEvents(new ServerGameRule(), this);
        this.getServer().getPluginManager().registerEvents(new ServerTabComplete(), this);
        this.getServer().getPluginManager().registerEvents(new ServerTick(), this);

        /* Custom worlds */
        this.getServer().createWorld(
            new WorldCreator("world_flatlands").generateStructures(false).type(WorldType.FLAT)
        );
    }

	public File getPrefixConfigFile() {
		return this.prefixConfigFile;
	}

	public FileConfiguration getPrefixConfig() {
		return this.prefixConfig;
	}
}
