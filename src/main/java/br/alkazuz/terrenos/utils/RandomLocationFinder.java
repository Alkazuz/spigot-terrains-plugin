package br.alkazuz.terrenos.utils;

import br.alkazuz.terrenos.Main;
import br.alkazuz.terrenos.storage.DBCore;
import org.bukkit.Location;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RandomLocationFinder {

    public static Location getRandomLocation(int size, Location playerLocation) {
        List<br.alkazuz.terrenos.utils.RandomLocationFinder.Region> occupiedRegions = loadOccupiedRegions();

        int maxExtent = 5000;
        int step = 10;

        int x = 0, z = 0;

        Location location = null;

        playerLocation = new Location(playerLocation.getWorld(), 600, 5, 600);

        for (int i = step; i <= maxExtent; i += step) {
            x = i;
            location = new Location(playerLocation.getWorld(),
                    playerLocation.getX() + x * step,
                    playerLocation.getY(), playerLocation.getZ() + z * step);
            if (!isRegionOccupied(location, size, occupiedRegions)) {
                return location;
            }

            x = -i;
            location = new Location(playerLocation.getWorld(),
                    playerLocation.getX() + x * step,
                    playerLocation.getY(), playerLocation.getZ() + z * step);
            if (!isRegionOccupied(location, size, occupiedRegions)) {
                return location;
            }

            z = i;
            location = new Location(playerLocation.getWorld(),
                    playerLocation.getX() + x * step,
                    playerLocation.getY(), playerLocation.getZ() + z * step);
            if (!isRegionOccupied(location, size, occupiedRegions)) {
                return location;
            }

            location = new Location(playerLocation.getWorld(),
                    playerLocation.getX() + x * step,
                    playerLocation.getY(), playerLocation.getZ() + z * step);
            if (!isRegionOccupied(location, size, occupiedRegions)) {
                return location;
            }
        }

        return location;
    }

    private static boolean isRegionOccupied(Location location, int size, List<br.alkazuz.terrenos.utils.RandomLocationFinder.Region> occupiedRegions) {
        int safetyMargin = 6;
        int minX = location.getBlockX() - safetyMargin;
        int maxX = location.getBlockX() + size + safetyMargin;
        int minZ = location.getBlockZ() - safetyMargin;
        int maxZ = location.getBlockZ() + size + safetyMargin;

        for (br.alkazuz.terrenos.utils.RandomLocationFinder.Region region : occupiedRegions) {
            if (maxX >= region.minX && minX <= region.maxX && maxZ >= region.minZ && minZ <= region.maxZ) {
                return true;
            }
        }

        return false;
    }


    private static List<Region> loadOccupiedRegions() {
        List<Region> occupiedRegions = new ArrayList<>();
        DBCore database = Main.getInstance().getDBCore();
        String query = "SELECT x1, x2, z1, z2 FROM `core_terrenos`";
        try (PreparedStatement ps = database.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                occupiedRegions.add(new Region(rs.getInt("x1"), rs.getInt("x2"), rs.getInt("z1"), rs.getInt("z2")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return occupiedRegions;
    }


    public static class Region {
        int minX, maxX, minZ, maxZ;

        public Region(int minX, int maxX, int minZ, int maxZ) {
            this.minX = minX;
            this.maxX = maxX;
            this.minZ = minZ;
            this.maxZ = maxZ;
        }
    }

}

