package me.snover.pointer;

import me.snover.TransferClient;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class LocationServerRegistry {

    private static final HashMap<String, LocationContainer> LOCATION_SERVER_REG = new HashMap<>();


    /**
     * Adds a location container for a server. This method can also be used to update/overwrite an existing container
     * @param server The server name EXACTLY as listed in the proxy config
     * @param container the {@link LocationContainer} containing the locations associated with the server name
     */
    public static void update(String server, LocationContainer container) {
        //First, we register the container
        LOCATION_SERVER_REG.put(server, container);
        TransferClient.getPlugin().getLogger().info("New coordinate/server association registered");
    }

    /**
     * Removes a location container for a server
     */
    public static void remove(String server) {
        if(LOCATION_SERVER_REG.containsKey(server)) {
            LOCATION_SERVER_REG.remove(server);
            TransferClient.getCompositeConfig().removeSubsection(server);
            TransferClient.getPlugin().getLogger().info("Removed " + server);
        }
    }

    /**
     *
     * @param server The server with the associated location container
     * @return Returns the {@link LocationContainer} associated with the specified server
     */
    public static LocationContainer getContainer(String server) {
        return LOCATION_SERVER_REG.get(server);
    }

    /**
     * Get the entire registry for all location container and server associations
     * @return Returns the registry as a {@link HashMap}
     */
    public static HashMap<String, LocationContainer> getLocationServerRegistry() {
        return LOCATION_SERVER_REG;
    }

    /**
     * Get a list of all registered servers
     * @return Returns a {@link List} of registered servers
     */
    public static Set<String> getRegisteredServers() {
        int size = LOCATION_SERVER_REG.size();
        if(size < 1) return null;
        return LOCATION_SERVER_REG.keySet();
    }

    /**
     * Checks if a container exists for the server
     * @param server The server with the associated location container
     * @return Returns {@code true} if the container exists
     */
    public static boolean exists(String server) {
        return LOCATION_SERVER_REG.containsKey(server);
    }
}
