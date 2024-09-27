package me.snover.paper.pointer;

import com.google.common.base.Preconditions;
import org.bukkit.Utility;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents a container associated with a server. This container is serializable and can be used to save
 * data in a YAML file
 */
public class CoordinateContainer implements ConfigurationSerializable {

    private final String SERVER_NAME;
    private final List<CoordinateSet> COORDINATE_SET_LIST = new ArrayList<>();

    public CoordinateContainer(@NotNull final String SERVER_NAME) {
        Preconditions.checkNotNull(SERVER_NAME);
        this.SERVER_NAME = SERVER_NAME;
    }

    /**
     * Adds a set of coordinates to the container as whole numbers
     * @param x
     * @param y
     * @param z
     */
    @SuppressWarnings("JavadocDeclaration")
    public void addCoordinateSet(int x, int y, int z) {

        if(coordinateSetExists(x, y ,z)) return;
        CoordinateSet set = new CoordinateSet(x, y, z);
        COORDINATE_SET_LIST.add(set);
    }

    /**
     * Removes a coordinate set from the container
     * @param x
     * @param y
     * @param z
     */
    @SuppressWarnings("JavadocDeclaration")
    public void removeCoordinateSet(int x, int y, int z) {

        if(!coordinateSetExists(x, y, z)) return;
        for(int i = 0; i < COORDINATE_SET_LIST.size(); i++) {
            CoordinateSet coordinateSet = COORDINATE_SET_LIST.get(i);
            int xFromSet = coordinateSet.getX();
            int yFromSet = coordinateSet.getY();
            int zFromSet = coordinateSet.getZ();

            if(xFromSet == x && yFromSet == y && zFromSet == z) {
                COORDINATE_SET_LIST.remove(i);
                return;
            }
        }
    }

    /**
     * Get all coordinate sets within this container
     * @return Returns a list of coordinate sets
     */
    public CoordinateSet[] getCoordinateSets() {
        CoordinateSet[] coordinateSets = new CoordinateSet[COORDINATE_SET_LIST.size()];
        for(int i = 0; i < COORDINATE_SET_LIST.size(); i++) coordinateSets[i] = COORDINATE_SET_LIST.get(i);
        return coordinateSets;
    }

    /**
     * Check to see if a set of coordinates exist within this container
     * @param x
     * @param y
     * @param z
     * @return Returns {@code true} if the coordinate set exists.
     */
    @SuppressWarnings("JavadocDeclaration")
    public boolean coordinateSetExists(int x, int y, int z) {
        for (CoordinateSet coordinateSet : COORDINATE_SET_LIST) {
            int xFromSet = coordinateSet.getX();
            int yFromSet = coordinateSet.getY();
            int zFromSet = coordinateSet.getZ();

            if (xFromSet == x && yFromSet == y && zFromSet == z) return true;
        }
        return false;
    }

    /**
     * Get the server name associated with this container.
     * @return Returns the name of the server as a {@link String}
     */
    public String getServerName() {
        return SERVER_NAME;
    }

    @Override
    @Utility
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("server", SERVER_NAME);
        data.put("size", COORDINATE_SET_LIST.size());
        for(int i = 0; i < COORDINATE_SET_LIST.size(); i++) {
            CoordinateSet set = COORDINATE_SET_LIST.get(i);
            data.put("set" + i, set.getX() + "_" + set.getY() + "_" + set.getZ());
        }
        return data;
    }

    @NotNull
    public static CoordinateContainer deserialize(Map<String, Object> data) {
        CoordinateContainer container = new CoordinateContainer((String) data.get("server"));
        int size = (int) data.get("size");
        for(int i = 0; i < size; i++) {
            String coords = (String) data.get("set" + i);
            String[] split = coords.split("_");
            container.addCoordinateSet(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
        }
        return container;
    }
}
