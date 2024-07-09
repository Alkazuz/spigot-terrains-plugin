package br.alkazuz.terrenos.object;

import br.alkazuz.terrenos.Main;
import br.alkazuz.terrenos.TerrenoFlags;
import br.alkazuz.terrenos.storage.DBCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

public class Terreno {
    private Integer id;
    private String owner;
    private int x1, x2, z1, z2;
    private final String world;
    private final HashMap<String, Object> flags = new HashMap<>();
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

    public HashMap<String, Object> getFlags() {
        return this.flags;
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
