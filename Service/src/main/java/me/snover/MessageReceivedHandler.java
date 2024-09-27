package me.snover;

import com.google.common.io.ByteArrayDataInput;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.ConnectionRequestBuilder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import me.snover.config.CompositeConfiguration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Provides a listener for the transfer channel(s)
 * @since 1.0.0
 * @author Adam Snover
 */
public class MessageReceivedHandler {
    private final TransferService PLUGIN;

    public MessageReceivedHandler(TransferService plugin) {
        PLUGIN = plugin;

    }

    /**
     * When a plugin message is received, process and transfer the player.
     * @param event Handles the {@link PluginMessageEvent}
     */
    @SuppressWarnings("unused")
    @Subscribe()
    public void onMessageReceived(PluginMessageEvent event) {
        String id = event.getIdentifier().getId();
        ByteArrayDataInput in = event.dataAsDataStream();

        if(id.equalsIgnoreCase("transfers:main")) {
            //Check if key is valid
            String key = in.readUTF();
            if(!key.equals(CompositeConfiguration.getSecret())) {
                PLUGIN.getLogger().warn("Received mismatched key! Rejecting the plugin message.");
                return;
            }
            //Start transfer process
            String request = in.readUTF();
            if(request.equalsIgnoreCase("transfer")) {
                String serverName = in.readUTF();
                String playerName = in.readUTF();
                Player player = PLUGIN.getProxyServer().getPlayer(playerName).get();
                RegisteredServer server;
                if(PLUGIN.getProxyServer().getServer(serverName).isPresent()) {
                    server = PLUGIN.getProxyServer().getServer(serverName).get();
                } else {
                    player.sendMessage(Component.text("Unable to send you to the server.\n" + serverName + " is not a server name that is registered with the proxy.\nPlease contact your server adminstrator.", NamedTextColor.RED));
                    PLUGIN.getLogger().warn(serverName, " is not a server name that is registered with the proxy.");
                    PluginMessageEvent.ForwardResult.handled();
                    return;
                }

                //Forward player connection to specified server
                ConnectionRequestBuilder conReq = player.createConnectionRequest(server);
                CompletableFuture<ConnectionRequestBuilder.Result> result = conReq.connect();
                try {
                    if(!result.get().isSuccessful()) {
                        PLUGIN.getLogger().error("Attempted to connect {} to {} but the attempt was unsuccessful", playerName, serverName);
                        Component reason;
                        if(result.get().getReasonComponent().isPresent()) {
                            reason = result.get().getReasonComponent().get();
                            player.sendMessage(reason);
                            PLUGIN.getLogger().error(reason.toString());
                        } else {
                            PLUGIN.getLogger().error("No further information");
                        }
                    }
                } catch (InterruptedException | ExecutionException e) {
                    PLUGIN.getLogger().error("Error upon getting result for player connection!\n{}\n(Player was {})\n(Server was {})\n(Current server is {})", e.getMessage(), playerName, serverName, player.getCurrentServer().get().getServerInfo().getName());
                    player.sendMessage(Component.text("An error occurred attempting to connect you to the server.\n", NamedTextColor.RED));
                } finally {
                    PluginMessageEvent.ForwardResult.handled();
                }
            }
        }
    }
}
