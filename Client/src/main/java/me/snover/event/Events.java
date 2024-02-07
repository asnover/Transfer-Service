package me.snover.event;

import me.snover.TransferClient;
import me.snover.config.ResourceOptions;
import me.snover.messaging.PluginMessageSender;
import me.snover.pointer.CoordinateContainer;
import me.snover.pointer.CoordinateServerRegistry;
import me.snover.pointer.CoordinateSet;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class contains all events for the plugin.
 */
public class Events implements Listener {

    private static final List<Player> playerLock = new ArrayList<>();

    /**
     * This event decides whether to send the player to a server or not based on any matches made in the {@link CoordinateServerRegistry}
     * @param event
     */
    @SuppressWarnings("JavadocDeclaration")
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        if(ResourceOptions.servers.isEmpty()) return;
        Player player = event.getPlayer();
        if(playerLock.contains(player)) return;
        int playerX = player.getLocation().getBlockX();
        int playerY = player.getLocation().getBlockY();
        int playerZ = player.getLocation().getBlockZ();
        HashMap<String, CoordinateContainer> REG = CoordinateServerRegistry.getCoordinateServerRegistry();

        for(String server : ResourceOptions.servers) {
            CoordinateContainer container = REG.get(server);
            if(container == null) return;
            CoordinateSet[] set = container.getCoordinateSets();
            for (CoordinateSet coordinateSet : set) {
                if (playerX == coordinateSet.getX() && playerY == coordinateSet.getY() && playerZ == coordinateSet.getZ()) {
                    playerLock.add(player);
                    PluginMessageSender.sendTransferMessage(TransferClient.getPlugin(), player, container.getServerName());
                    return;
                }
            }
        }
    }

    /**
     * Force the player to spawn at a location specified in the config
     * @param event
     */
    @SuppressWarnings("JavadocDeclaration")
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(ResourceOptions.forcedSpawn) {
            player.teleport(ResourceOptions.spawnLocation);
        }
    }

    /**
     * Helps mitigate player move event spam
     * @param event
     */
    @SuppressWarnings("JavadocDeclaration")
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if(playerLock.contains(player)) unlock(player);
    }

    public static void unlock(Player player) {
        playerLock.remove(player);
    }
}
