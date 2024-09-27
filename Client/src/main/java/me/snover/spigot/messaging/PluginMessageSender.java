package me.snover.spigot.messaging;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.snover.spigot.config.ResourceOptions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * This class will handle any plugin messages that need to be sent
 */
public class PluginMessageSender {

    /**
     * Send a message to the proxy to transfer a player to another server
     * @param plugin The plugin initiating the transfer
     * @param player The player to be transferred
     * @param serverName The name of the server that the player is to be transferred to
     */
    public static void sendTransferMessage(Plugin plugin, Player player, String serverName) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(ResourceOptions.secretKey);
        out.writeUTF("transfer");
        out.writeUTF(serverName);
        out.writeUTF(player.getName());
        player.sendPluginMessage(plugin, "transfers:main", out.toByteArray());
    }
}
