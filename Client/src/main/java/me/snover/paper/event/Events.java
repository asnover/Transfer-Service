package me.snover.paper.event;

import me.snover.paper.TransferClient;
import me.snover.paper.config.ResourceOptions;
import me.snover.paper.messaging.PluginMessageSender;
import me.snover.paper.pointer.CoordinateContainer;
import me.snover.paper.pointer.CoordinateServerRegistry;
import me.snover.paper.pointer.CoordinateSet;
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
    Timer lockTimeoutTimer = new Timer();

    /**
     * This event decides whether to send the player to a server or not based on any matches made in the {@link CoordinateServerRegistry}
     * @param event
     */
    @SuppressWarnings("JavadocDeclaration")
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        //noinspection DataFlowIssue
        if(CoordinateServerRegistry.getRegisteredServers().isEmpty()) return;
        Player player = event.getPlayer();
        if(playerLock.contains(player)) return;
        if(editingPlayer.contains(player)) return;
        int playerX = player.getLocation().getBlockX();
        int playerY = player.getLocation().getBlockY();
        int playerZ = player.getLocation().getBlockZ();
        HashMap<String, CoordinateContainer> REG = CoordinateServerRegistry.getCoordinateServerRegistry();

        for(String server : CoordinateServerRegistry.getRegisteredServers()) {
            CoordinateContainer container = REG.get(server);
            if(container == null) return;
            CoordinateSet[] set = container.getCoordinateSets();
            for (CoordinateSet coordinateSet : set) {
                if (playerX == coordinateSet.getX() && playerY == coordinateSet.getY() && playerZ == coordinateSet.getZ()) {
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
