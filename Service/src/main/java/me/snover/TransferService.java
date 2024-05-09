package me.snover;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import me.snover.config.CompositeConfiguration;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Main class for the Transfer Service plugin
 * @since 1.1.0
 * @author Adam Snover
 */
@Plugin(id = "transferservice", name = "Transfer Service", version = "1.1.0", description = "A service for client servers interacting with the proxy", authors = {"Adam Snover"})
public class TransferService {
    private final ProxyServer SERVER;
    private final Logger LOGGER;
    private final Path DATA_DIRECTORY;
    private CompositeConfiguration config;

    /**
     * Constructs the plugin
     * @param server The proxy server
     * @param logger The proxy logger
     * @param dataDirectory The data directory for the plugin to use
     */

    @Inject
    public TransferService(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        SERVER = server;
        LOGGER = logger;
        DATA_DIRECTORY = dataDirectory;
    }

    /**
     * Initializes the plugin
     * @param event The event that is fired to initialize the plugin
     */
    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        SERVER.getEventManager().register(this, new MessageReceivedHandler(this));
        SERVER.getChannelRegistrar().register(MinecraftChannelIdentifier.create("transfers", "main"));
        try {
            config = new CompositeConfiguration(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        config.load();
    }

    /**
     *
     * @return Returns the proxy server
     */
    public ProxyServer getServer() {
        return SERVER;
    }

    /**
     *
     * @return Returns the proxy logger
     */
    public Logger getLogger() {
        return LOGGER;
    }

    /**
     *
     * @return Returns the data directory for the plugin to use
     */
    public Path getDataDirectory() {
        return DATA_DIRECTORY;
    }

}
