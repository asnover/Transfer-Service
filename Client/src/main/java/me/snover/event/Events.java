package me.snover.event;

import me.snover.TransferClient;
import me.snover.config.ResourceOptions;
import me.snover.messaging.PluginMessageSender;
import me.snover.pointer.LocationContainer;
import me.snover.pointer.LocationServerRegistry;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

/**
 * This class contains all events for the plugin.
 */
public class Events implements Listener {

    private static final List<Player> playerLock = new ArrayList<>();
    private static final List<Player> editingPlayer = new ArrayList<>();
    final Timer lockTimeoutTimer = new Timer();

    /**
     * This event decides whether to send the player to a server or not based on any matches made in the {@link LocationServerRegistry}
     * @param event
     */
    @SuppressWarnings("JavadocDeclaration")
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        //noinspection DataFlowIssue
        if(LocationServerRegistry.getRegisteredServers().isEmpty()) return;
        Player player = event.getPlayer();
        if(playerLock.contains(player)) return;
        if(editingPlayer.contains(player)) return;
        int playerX = player.getLocation().getBlockX();
        int playerY = player.getLocation().getBlockY();
        int playerZ = player.getLocation().getBlockZ();
        HashMap<String, LocationContainer> REG = LocationServerRegistry.getLocationServerRegistry();

        for(String server : LocationServerRegistry.getRegisteredServers()) {
            LocationContainer container = REG.get(server);
            if(container == null) return;
            Location[] locations = container.getLocations();
            for (Location location : locations) {
                if (playerX == location.getX() && playerY == location.getY() && playerZ == location.getZ()) {
                    playerLock.add(player);
                    lockTimeoutTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if(playerLock.contains(player)) unlock(player);
                        }
                    }, 5000L);
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

    /**
     * Allow the playerMoveEvent to be executed again for the specified player
     * @param player Player to release from the lock
     */
    public static void unlock(Player player) {
        playerLock.remove(player);
    }

    public static void addEditingPlayer(Player player) {
        if(!editingPlayer.contains(player)) {
            editingPlayer.add(player);
        }
    }

    public static void removeEditingPlayer(Player player) {
        editingPlayer.remove(player);
    }

    public static boolean isPlayerEditing(Player player) {
        return editingPlayer.contains(player);
    }
}
