package pw.kaboom.extras;

import io.papermc.paper.registry.keys.BlockTypeKeys;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
import pw.kaboom.extras.commands.*;
import pw.kaboom.extras.modules.block.BlockCheck;
import pw.kaboom.extras.modules.block.BlockPhysics;
import pw.kaboom.extras.modules.entity.EntityExplosion;
import pw.kaboom.extras.modules.entity.EntityKnockback;
import pw.kaboom.extras.modules.entity.EntitySpawn;
import pw.kaboom.extras.modules.player.*;
import pw.kaboom.extras.modules.player.skin.SkinManager;
import pw.kaboom.extras.modules.server.ServerCommand;
import pw.kaboom.extras.modules.server.ServerGameRule;
import pw.kaboom.extras.modules.server.ServerTabComplete;
import pw.kaboom.extras.util.FlatLayers;

import java.io.File;

public final class Main extends JavaPlugin {
    public static Main PLUGIN;

    private File prefixConfigFile;
    private FileConfiguration prefixConfig;

    public Main() {
        PLUGIN = this;
    }

    @Override
    public void onLoad() {
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
        this.getCommand("broadcastrainbow").setExecutor(new CommandBroadcastRainbow());
        this.getCommand("broadcastminimessage").setExecutor(new CommandBroadcastMM());
        this.getCommand("broadcastvanilla").setExecutor(new CommandBroadcastVanilla());
        this.getCommand("clearchat").setExecutor(new CommandClearChat());
        this.getCommand("console").setExecutor(new CommandConsole());
        this.getCommand("destroyentities").setExecutor(new CommandDestroyEntities());
        this.getCommand("enchantall").setExecutor(new CommandEnchantAll());
        this.getCommand("getjson").setExecutor(new CommandGetJSON());
        this.getCommand("getjsonmm").setExecutor(new CommandGetJSONMM());
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

        /* Player-related modules */
        this.getServer().getPluginManager().registerEvents(new PlayerChat(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerCommand(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerConnection(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerDamage(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerInteract(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerRecipe(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerTeleport(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerPrefix(), this);

        final var skinManager = new SkinManager();
        this.getServer().getPluginManager().registerEvents(skinManager, this);
        skinManager.start();

        /* Server-related modules */
        ServerGameRule.init(this);

        this.getServer().getPluginManager().registerEvents(new ServerCommand(), this);
        this.getServer().getPluginManager().registerEvents(new ServerGameRule(), this);
        this.getServer().getPluginManager().registerEvents(new ServerTabComplete(), this);

        /* Custom worlds */
        this.getServer().createWorld(
            new WorldCreator("world_flatlands")
                    .generateStructures(false)
                    .type(WorldType.FLAT)
                    .generatorSettings(
                            new FlatLayers()
                                    .addLayer(BlockTypeKeys.BEDROCK, 1)
                                    .addLayer(BlockTypeKeys.DIRT, 124)
                                    .addLayer(BlockTypeKeys.STONE, 2)
                                    .addLayer(BlockTypeKeys.GRASS_BLOCK, 1)
                                    .build()
                    )
        );

        final Messenger messenger = this.getServer().getMessenger();
        final PlayerMessaging playerMessaging = new PlayerMessaging(this);

        this.getServer().getPluginManager().registerEvents(playerMessaging, this);

        messenger.registerIncomingPluginChannel(this, PlayerMessaging.REGISTER, playerMessaging);
        messenger.registerIncomingPluginChannel(this, PlayerMessaging.UNREGISTER, playerMessaging);

        messenger.registerIncomingPluginChannel(this, PlayerMessaging.MESSAGE, playerMessaging);
        messenger.registerOutgoingPluginChannel(this, PlayerMessaging.MESSAGE);
    }

	public File getPrefixConfigFile() {
		return this.prefixConfigFile;
	}

	public FileConfiguration getPrefixConfig() {
		return this.prefixConfig;
	}
}
