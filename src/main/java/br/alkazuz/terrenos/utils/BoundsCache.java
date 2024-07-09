package br.alkazuz.terrenos.utils;

import br.alkazuz.terrenos.Main;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BoundsCache {
    private static final Map<String, Pair<Integer, Integer>> xBoundsCache = new HashMap<>();
    private static final Map<String, Pair<Integer, Integer>> zBoundsCache = new HashMap<>();

    public static Pair<Integer, Integer> getCachedXBounds(World world) {
        String worldName = world.getName();
        if (!xBoundsCache.containsKey(worldName)) {
            updateBoundsForWorld(worldName);
        }
        return xBoundsCache.get(worldName);
    }

    public static Pair<Integer, Integer> getCachedZBounds(World world) {
        String worldName = world.getName();
        if (!zBoundsCache.containsKey(worldName)) {
            updateBoundsForWorld(worldName);
        }
        return zBoundsCache.get(worldName);
    }

    private static void updateBoundsForWorld(String worldName) {
        String query = "SELECT MAX(x2) AS maxX, MIN(x1) AS minX, MAX(z2) AS maxZ, MIN(z1) AS minZ FROM `core_terrenos` WHERE `world` = ?";
        try (PreparedStatement ps = Main.getInstance().getDBCore().prepareStatement(query)) {
            ps.setString(1, worldName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int maxX = rs.getInt("maxX");
                int minX = rs.getInt("minX");
                int maxZ = rs.getInt("maxZ");
                int minZ = rs.getInt("minZ");
                xBoundsCache.put(worldName, new Pair<>(minX, maxX));
                zBoundsCache.put(worldName, new Pair<>(minZ, maxZ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Pair<L, R> {
        private final L left;
        private final R right;

        public Pair(L left, R right) {
            this.left = left;
            this.right = right;
        }

        public L getLeft() {
            return left;
        }

        public R getRight() {
            return right;
        }
    }
}


