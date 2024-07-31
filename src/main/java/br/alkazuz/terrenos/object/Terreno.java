package br.alkazuz.terrenos.object;

import br.alkazuz.terrenos.Main;
import br.alkazuz.terrenos.TerrenoFlags;
import br.alkazuz.terrenos.storage.DBCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Terreno {
    private Integer id;
    private String owner;
    private int x1, x2, z1, z2;
    private final String world;
    private final HashMap<String, Object> flags = new HashMap<>();
    private final HashMap<EntityType, Location> spawns = new HashMap<>();
    public boolean deleting = false;

    public Terreno(Integer id, String owner, int x1, int x2, int z1, int z2, String world) {
        this.id = id;
        this.owner = owner;
        this.x1 = x1;
        this.x2 = x2;
        this.z1 = z1;
        this.z2 = z2;
        this.world = world;
    }

    public Integer getId() {
        return this.id;
    }

    public String getOwner() {
        return this.owner;
    }

    public int getX1() {
        return this.x1;
    }

    public int getX2() {
        return this.x2;
    }

    public int getZ1() {
        return this.z1;
    }

    public int getZ2() {
        return this.z2;
    }


    public String getWorld() {
        return this.world;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setX1(int x1) {
        this.x1 = x1;
    }

    public void setX2(int x2) {
        this.x2 = x2;
    }

    public void setZ1(int z1) {
        this.z1 = z1;
    }

    public void setZ2(int z2) {
        this.z2 = z2;
    }

    public boolean isOnTerrain(int x, int z) {
        return (x >= this.x1 && x <= this.x2 && z >= this.z1 && z <= this.z2);
    }

    public boolean isOnTerrain(Location loc) {
        return isOnTerrain(loc.getBlockX(), loc.getBlockZ(), loc.getWorld().getName());
    }

    public boolean isOnTerrain(int x, int z, String world) {
        return (isOnTerrain(x, z) && this.world.equals(world));
    }

    public void setFlag(String key, Object value) {
        this.flags.put(key, value);
    }

    public Object getFlag(String key) {
        return this.flags.get(key);
    }

    public Object getFlagOrDefault(String key, Object def) {
        return this.flags.getOrDefault(key, def);
    }

    public boolean getFlagBooleanOrDefault(String key, boolean def) {
        return Boolean.parseBoolean(String.valueOf(getFlagOrDefault(key, def)));
    }

    public boolean isPvp() {
        return Boolean.parseBoolean(String.valueOf(getFlagOrDefault(TerrenoFlags.PVP.getFlag(), TerrenoFlags.PVP.getDefaultValue())));
    }

    public boolean isPvp24() {
        return Boolean.parseBoolean
                (String.valueOf(getFlagOrDefault(TerrenoFlags.PVP_24H.getFlag(), TerrenoFlags.PVP_24H.getDefaultValue())));
    }

    public boolean canSetHome() {
        return Boolean.parseBoolean(
                String.valueOf(getFlagOrDefault(TerrenoFlags.SET_HOME.getFlag(), TerrenoFlags.SET_HOME.getDefaultValue())));
    }

    public boolean canTpAccept() {
        return Boolean.parseBoolean(
                String.valueOf(getFlagOrDefault(TerrenoFlags.TP_ACCEPT.getFlag(), TerrenoFlags.TP_ACCEPT.getDefaultValue())));
    }

    public HashMap<EntityType, Location> getSpawns() {
        return spawns;
    }

    public HashMap<String, Object> getFlags() {
        return this.flags;
    }

    private void deleteSpawnsIfNotEntry() {
        List<EntityType> allSpawns = new ArrayList<>(spawns.keySet());
        String placeholders = allSpawns.stream().map(spawn -> "?").collect(Collectors.joining(", "));
        String sql = "DELETE FROM `core_terrenos_spawns` WHERE `terreno_id` = ? AND `entity` NOT IN (" + placeholders + ");";

        try (PreparedStatement ps = Main.getInstance().getDBCore().prepareStatement(sql)) {
            ps.setInt(1, id);
            for (int i = 0; i < allSpawns.size(); i++) {
                ps.setString(i + 2, allSpawns.get(i).toString());
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void save() {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            long start = System.currentTimeMillis();
            DBCore db = Main.getInstance().getDBCore();
            String sql;
            if (id == null) {
                sql = "INSERT INTO `core_terrenos` (`owner`, `x1`, `x2`, `z1`, `z2`, `world`) VALUES (?, ?, ?, ?, ?, ?);";
            } else {
                sql = "UPDATE `core_terrenos` SET `owner` = ?, `x1` = ?, `x2` = ?, `z1` = ?, `z2` = ?, `world` = ? WHERE `id` = ?;";
            }

            deleteSpawnsIfNotEntry();

            for (Map.Entry<EntityType, Location> entry : spawns.entrySet()) {
                EntityType entity = entry.getKey();
                Location location = entry.getValue();
                try (PreparedStatement ps = db.prepareStatement(
                        "INSERT INTO `core_terrenos_spawns` (`terreno_id`, `x`, `y`, `z`, `world`, `entity`) VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE `x` = VALUES(`x`), `y` = VALUES(`y`), `z` = VALUES(`z`), `world` = VALUES(`world`);")) {
                    ps.setInt(1, id);
                    ps.setDouble(2, location.getX());
                    ps.setDouble(3, location.getY());
                    ps.setDouble(4, location.getZ());
                    ps.setString(5, location.getWorld().getName());
                    ps.setString(6, entity.toString());
                    ps.executeUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try (PreparedStatement ps = db.prepareStatement(sql, id == null ? PreparedStatement.RETURN_GENERATED_KEYS : PreparedStatement.NO_GENERATED_KEYS)) {
                ps.setString(1, owner);
                ps.setInt(2, x1);
                ps.setInt(3, x2);
                ps.setInt(4, z1);
                ps.setInt(5, z2);
                ps.setString(6, world);
                if (id != null) {
                    ps.setInt(7, id);
                }

                int affectedRows = ps.executeUpdate();
                if (id == null && affectedRows > 0) {
                    try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            id = generatedKeys.getInt(1);
                        }
                    }
                }

                long end = System.currentTimeMillis() - start;
                Main.debug("Terreno.save() took " + end + "ms");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void loadSpawns() {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            DBCore db = Main.getInstance().getDBCore();
            String sql = "SELECT `entity`, `x`, `y`, `z`, `world` FROM `core_terrenos_spawns` WHERE `terreno_id` = ?;";

            try (PreparedStatement ps = db.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        EntityType entity = EntityType.valueOf(rs.getString("entity"));
                        int x = rs.getInt("x");
                        int y = rs.getInt("y");
                        int z = rs.getInt("z");
                        String world = rs.getString("world");
                        spawns.put(entity, new Location(Bukkit.getWorld(world), x, y, z));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void loadFlags() {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            DBCore db = Main.getInstance().getDBCore();
            String sql = "SELECT `key`, `value` FROM `core_terrenos_flags` WHERE `terreno_id` = ?;";

            try (PreparedStatement ps = db.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String key = rs.getString("key");
                        String value = rs.getString("value");
                        flags.put(key, value);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void saveFlags() {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            DBCore db = Main.getInstance().getDBCore();
            String sql = "INSERT INTO `core_terrenos_flags` (`terreno_id`, `key`, `value`) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE `value` = VALUES(`value`);";

            try (PreparedStatement ps = db.prepareStatement(sql)) {
                for (String key : flags.keySet()) {
                    Object value = flags.get(key);
                    ps.setInt(1, id);
                    ps.setString(2, key);
                    ps.setString(3, value.toString());
                    ps.addBatch();
                }

                ps.executeBatch();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Location getCenter() {
        return new Location(Bukkit.getWorld(world), (x1 + x2) / 2, 10, (z1 + z2) / 2);
    }

    public boolean isOutSide(Location location) {
        return location.getBlockX() < x1 || location.getBlockX() > x2 || location.getBlockZ() < z1 || location.getBlockZ() > z2;
    }

    public void delete() {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            DBCore db = Main.getInstance().getDBCore();
            try {
                try (PreparedStatement ps = db.prepareStatement("DELETE FROM `core_terrenos` WHERE `id` = ?;")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = db.prepareStatement("DELETE FROM `core_terrenos_flags` WHERE `terreno_id` = ?;")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = db.prepareStatement("DELETE FROM `core_terrenos_perms` WHERE `terreno_id` = ?;")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = db.prepareStatement("DELETE FROM `core_terrenos_spawns` WHERE `terreno_id` = ?;")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public String toString() {
        return "Terreno{" +
                "id=" + id +
                ", owner='" + owner + '\'' +
                ", x1=" + x1 +
                ", x2=" + x2 +
                ", z1=" + z1 +
                ", z2=" + z2 +
                ", world='" + world + '\'' +
                ", flags=" + flags +
                ", deleting=" + deleting +
                '}';
    }


}
