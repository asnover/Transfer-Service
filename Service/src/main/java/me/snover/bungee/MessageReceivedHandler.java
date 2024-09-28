package me.snover.bungee;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import me.snover.bungee.config.CompositeConfiguration;
import net.md_5.bungee.event.EventHandler;

import java.util.Map;

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
    @EventHandler
    public void onMessageReceived(PluginMessageEvent event) {
        String id = event.getTag();
        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());

        //TODO Figure how bungee uses plugin message channels and replace placeholder.
        if(id.equalsIgnoreCase("PLACEHOLDER")) {
            //Check if key is valid
            String key = in.readUTF();
            if(!key.equals(CompositeConfiguration.getSecret())) {
                PLUGIN.getLogger().warning("Received mismatched key! Rejecting the plugin message.");
                return;
            }
            //Start transfer process
            String request = in.readUTF();
            if(request.equalsIgnoreCase("transfer")) {
                String serverName = in.readUTF();
                String playerName = in.readUTF();
                ProxiedPlayer player = PLUGIN.getProxy().getPlayer(playerName);

                Map<String, ServerInfo> serverMap = PLUGIN.getProxy().getServers();
                ServerInfo server;
                if(serverMap.containsKey(serverName)) {
                    server = serverMap.get(serverName);
                } else {
                    TextComponent failMsg = new TextComponent("Unable to send you to the server.\n" + serverName + " is not a server name that is registered with the proxy.\nPlease contact your server adminstrator.");
                    failMsg.setColor(ChatColor.RED);
                    player.sendMessage(failMsg);
                    PLUGIN.getLogger().warning(serverName + " is not a server name that is registered with the proxy.");
                    return;
                }

                //Forward player connection to specified server
                player.connect(server);

                /*ConnectionRequestBuilder conReq = player.createConnectionRequest(server);
                CompletableFuture<ConnectionRequestBuilder.Result> result = conReq.connect();
                try {
                    if(!result.get().isSuccessful()) {
                        PLUGIN.getLogger().error("Attempted to connect " + playerName + " to " + serverName + " but the attempt was unsuccessful");
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
                    PLUGIN.getLogger().error("Error upon getting result for player connection!\n" + e.getMessage() + "\n(Player was " + playerName + ")\n(Server was " + serverName + ")\n(Current server is " + player.getCurrentServer().get().getServerInfo().getName() + ")");
                    player.sendMessage(Component.text("An error occurred attempting to connect you to the server.\n", NamedTextColor.RED));
                } finally {
                    PluginMessageEvent.ForwardResult.handled();
                }*/
            }
        }
    }
}
