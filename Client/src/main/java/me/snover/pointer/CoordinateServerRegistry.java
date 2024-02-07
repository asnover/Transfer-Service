package me.snover.pointer;

import me.snover.TransferClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CoordinateServerRegistry {

    private static final HashMap<String, CoordinateContainer> COORDINATE_SERVER_REG = new HashMap<>();
    private static final List<String> REG_SERVERS = new ArrayList<>();

    /**
     * Adds a coordinate container for a server. This method can also be used to update/overwrite an existing container
     * @param server The server name EXACTLY as listed in the proxy config
     * @param container the {@link CoordinateContainer} containing the set of coordinates associated with the server name
     */
    public static void add(String server, CoordinateContainer container) {
        //First, we register the container
        COORDINATE_SERVER_REG.put(server, container);
        TransferClient.getPlugin().getLogger().info("New coordinate/server association registered");

        //Check if any servers have been registered yet. If not, then register
        if(REG_SERVERS.isEmpty()) {
            REG_SERVERS.add(server);
            return;
        }

        //Check to see if the server has been registered yet.
        boolean exists = false;
        for(String currentServer : REG_SERVERS) {
            if(currentServer.equals(server))  {
                exists = true;
                break;
            }
        }
        if(!exists) REG_SERVERS.add(server);
    }

    @Deprecated(forRemoval = true)
    public static void add(CoordinateContainer container) {
        add(container.getServerName(), container);
    }
    /**
    * Removes a coordinate container for a server
     */
    public static void remove(String server) {
        if(COORDINATE_SERVER_REG.containsKey(server)) {
            COORDINATE_SERVER_REG.remove(server);
            for(String currentServer : REG_SERVERS) {
                if(currentServer.equals(server)) {
                    REG_SERVERS.remove(server);
                    break;
                }
            }
        }
    }

    /**
     *
     * @param server The server with the associated coordinate container
     * @return Returns the {@link CoordinateContainer} associated with the specified server
     */
    public static CoordinateContainer getContainer(String server) {
        return COORDINATE_SERVER_REG.get(server);
    }

    /**
     * Get the entire registry for all coordinate container and server associations
     * @return Returns the registry as a {@link HashMap}
     */
    public static HashMap<String, CoordinateContainer> getCoordinateServerRegistry() {
        return COORDINATE_SERVER_REG;
    }

    /**
     * Get a list of all registered servers
     * @return Returns a {@link List} of registered servers
     */
    public static List<String> getRegisteredServers() {
        return REG_SERVERS;
    }

    /**
     * Checks if a container exists for the server
     * @param server The server with the associated coordinate container
     * @return Returns {@code true} if the container exists
     */
    public static boolean exists(String server) {
        return COORDINATE_SERVER_REG.containsKey(server);
    }

}
