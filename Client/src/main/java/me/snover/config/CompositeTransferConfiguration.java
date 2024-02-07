package me.snover.config;

import com.google.common.base.Preconditions;
import me.snover.pointer.CoordinateContainer;
import me.snover.pointer.CoordinateServerRegistry;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * This class handles the loading and saving of the configuration and data files.
 */
public class CompositeTransferConfiguration {
    private FileConfiguration config = null;
    private FileConfiguration data = null;
    private File configFile = null;
    private File dataFile = null;
    private final JavaPlugin plugin;

    public CompositeTransferConfiguration(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadResources(boolean loadConfig, boolean loadCoords) {
        //load config file
        if (loadConfig) {
            //Check if exists
            if (config == null) {
                if (configFile == null) {
                    configFile = new File(plugin.getDataFolder(), "config.yml");
                    //If no config exists, create one
                    if (!configFile.exists()) {
                        plugin.saveResource("config.yml", false);
                    }
                }

                //Begin actual loading process
                config = YamlConfiguration.loadConfiguration(configFile);
                ResourceOptions.forcedSpawn = config.getBoolean("forcedspawn");
                //TODO Later on, change this to a serialized org.bukkit.Location
                String[] spawnString = config.getString("spawn").split("-");
                for (String s : spawnString) {
                    plugin.getLogger().info(s);
                }
                int x = Integer.parseInt(spawnString[0]);
                int y = Integer.parseInt(spawnString[1]);
                int z = Integer.parseInt(spawnString[2]);

                ResourceOptions.spawnLocation = new Location(plugin.getServer().getWorld("world"), x, y, z);
                ResourceOptions.secretKey = config.getString("secret");
                ResourceOptions.servers = config.getStringList("servers");
            }
        }

        //load server coordinate registries
        if (loadCoords) {
            if (data == null) {
                if (dataFile == null) {
                    dataFile = new File(plugin.getDataFolder(), "data.yml");
                    if (!dataFile.exists())  {
                        plugin.saveResource("data.yml", false);
                    }
                }
            }

            //Begin actual loading process
            data = YamlConfiguration.loadConfiguration(dataFile);
            ConfigurationSection coordSection = data.getConfigurationSection("coordinatecontainers");
            if(coordSection != null) {
                Set<String> containers = coordSection.getKeys(false);
                for(String serverKey : containers) {
                    plugin.getLogger().info(serverKey);
                    CoordinateContainer container = (CoordinateContainer) coordSection.get(serverKey);
                    CoordinateServerRegistry.add(serverKey, container);
                }
            }
        }
    }


    @SuppressWarnings("CallToPrintStackTrace")
    public void saveResources(boolean saveConfig, boolean saveCoords) {
        if(saveConfig) {
            if(config == null || configFile == null) {
                plugin.getLogger().warning("Cannot save a null configuration!");
                return;
            }
            Location loc = ResourceOptions.spawnLocation;
            String pos = loc.getBlockX() + "-" + loc.getBlockY() + "-" + loc.getBlockZ();
            config.set("forcedspawn", ResourceOptions.forcedSpawn);
            config.set("spawn", pos);
            config.set("servers", ResourceOptions.servers);
            try {
                config.save(configFile);
            } catch (IOException e) {
                plugin.getLogger().severe("Error attempting to save config!");
                e.printStackTrace();
            }
        }

        if(saveCoords) {
            if(data == null || dataFile == null) {
                plugin.getLogger().warning("Cannot save a null data configuration!");
                return;
            }

            if(!data.isConfigurationSection("coordinatecontainers")) {
                data.createSection("coordinatecontainers");
            }

            ConfigurationSection containerSection = data.getConfigurationSection("coordinatecontainers");
            for(String server : ResourceOptions.servers) {
                containerSection.set(server, CoordinateServerRegistry.getContainer(server));
            }
            try {
                Preconditions.checkNotNull(containerSection);
                data.save(dataFile);
            } catch (IOException e) {
                plugin.getLogger().severe("Error attempting to save data config!");
                e.printStackTrace();
            }
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public FileConfiguration getData() {
        return data;
    }
}
