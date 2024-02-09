package me.snover;

import com.google.common.io.ByteArrayDataInput;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.ConnectionRequestBuilder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import me.snover.config.CompositeConfiguration;

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
     * @param event
     */
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
                RegisteredServer server = PLUGIN.getServer().getServer(serverName).get();
                Player player = PLUGIN.getServer().getPlayer(playerName).get();

                //Forward player connection to specified server
                ConnectionRequestBuilder conReq = player.createConnectionRequest(server);
                CompletableFuture<ConnectionRequestBuilder.Result> result = conReq.connect();
                try {
                    if(!result.get().isSuccessful()) {
                        PLUGIN.getLogger().warn("Attempted to connect " + playerName + " to " + serverName + " but the attempt was unsuccessful");
                    }
                } catch (InterruptedException | ExecutionException e) {
                    PLUGIN.getLogger().error("Error upon getting result for player connection! \n(Player was " + playerName + ")\n(Server was " + serverName + ")\n(Current server is " + player.getCurrentServer().get().getServerInfo().getName() + ")");
                    e.printStackTrace();
                } finally {
                    PluginMessageEvent.ForwardResult.handled();
                }
            }
        }
    }
}
