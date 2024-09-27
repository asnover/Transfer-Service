package me.snover.paper.config;

import me.snover.spigot.TransferClient;
import org.bukkit.Location;

/**
 * This class contains all options listed in the config and held in memory.
 */
public class ResourceOptions {
    public static volatile boolean forcedSpawn = false;
    public static volatile Location spawnLocation = new Location(TransferClient.getPlugin().getServer().getWorlds().getFirst(), 0.500d, 4.0d, 0.500d); //new Location(TransferClient.getPlugin().getServer().getWorld("world"), 0.500d, 4.0d, 0.500d);
    public static volatile String secretKey = null;
}
