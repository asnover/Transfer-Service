package me.snover.pointer;

import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationContainer implements ConfigurationSerializable {
    private final String SERVER_NAME;
    private final List<Location> LOCATION_LIST = new ArrayList<>();

    public LocationContainer(@NotNull final String SERVER_NAME) {
        Preconditions.checkNotNull(SERVER_NAME);
        this.SERVER_NAME = SERVER_NAME;
    }

    /**
     * Adds a set of coordinates to the container as whole numbers
     * @param world
     * @param x
     * @param y
     * @param z
     */
    @SuppressWarnings("JavadocDeclaration")
    public void addLocation(World world, int x, int y, int z) {
        addLocation(new Location(world, x, y, z));
    }

    public void addLocation(Location location) {
        if(locationExists(location)) return;
        LOCATION_LIST.add(location);
    }

    /**
     * Removes a coordinate set from the container
     * @param location
     */
    @SuppressWarnings("JavadocDeclaration")
    public void removeLocation(Location location) {
        if(!locationExists(location)) return;
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        String world = location.getWorld().getName();

        for(int i = 0; i < LOCATION_LIST.size(); i++) {
            Location regLocation = LOCATION_LIST.get(i);
            double regX = regLocation.getX();
            double regY = regLocation.getY();
            double regZ = regLocation.getZ();
            String regWorld = regLocation.getWorld().getName();
            if(regWorld.equals(world) && regX == x && regY == y && regZ == z) {
                LOCATION_LIST.remove(i);
                return;
            }
        }
    }

    /**
     * Get all coordinate sets within this container
     * @return Returns a list of coordinate sets
     */
    public Location[] getLocations() {
        Location[] locations = new Location[LOCATION_LIST.size()];
        for(int i = 0; i < LOCATION_LIST.size(); i++) locations[i] = LOCATION_LIST.get(i);
        return locations;
    }



    /**
     * Check to see if a set of coordinates exist within this container
     * @param location A Bukkit location
     * @return Returns {@code true} if the coordinate set exists.
     */
    public boolean locationExists(Location location) {
        String world = location.getWorld().getName();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        for (Location regLocation : LOCATION_LIST) {
            String worldFromReg = regLocation.getWorld().getName();
            double xFromReg = regLocation.getX();
            double yFromReg = regLocation.getY();
            double zFromReg = regLocation.getZ();

            if (worldFromReg.equals(world) && xFromReg == x && yFromReg == y && zFromReg == z) return true;
        }
        return false;
    }

    public String getServerName() {
        return SERVER_NAME;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("server", SERVER_NAME);
        data.put("size", LOCATION_LIST.size());
        for(int i = 0; i < LOCATION_LIST.size(); i++) {
            Location location = LOCATION_LIST.get(i);
            data.put("location" + i, location);
        }
        return data;
    }

    @SuppressWarnings("unused")
    @NotNull
    public static LocationContainer deserialize(Map<String, Object> data) {
        LocationContainer container = new LocationContainer((String) data.get("server"));
        int size = (int) data.get("size");
        for(int i = 0; i < size; i++) {
            Location location = (Location) data.get("location" + i);
            container.addLocation(location);
        }
        return container;
    }
}
