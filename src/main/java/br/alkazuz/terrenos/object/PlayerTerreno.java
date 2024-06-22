package br.alkazuz.terrenos.object;

import br.alkazuz.terrenos.Main;
import br.alkazuz.terrenos.perms.EPermissions;
import br.alkazuz.terrenos.storage.DBCore;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PlayerTerreno {
    private Integer id;
    private String player;
    private Terreno terreno;
    private boolean placeBlocks = false;
    private boolean breakBlock = false;
    private boolean openChest = false;
    private boolean useCommands = false;

    public PlayerTerreno(String player) {
        this.player = player;
    }

    public String getPlayer() {
        return player;
    }

    public Terreno getTerreno() {
        return terreno;
    }

    public void setTerreno(Terreno terreno) {
        this.terreno = terreno;
    }

    public boolean canPlaceBlocks() {
        return placeBlocks;
    }

    public void setPlaceBlocks(boolean placeBlocks) {
        this.placeBlocks = placeBlocks;
    }

    public boolean canBreakBlock() {
        return breakBlock;
    }

    public void setBreakBlock(boolean breakBlock) {
        this.breakBlock = breakBlock;
    }

    public boolean canOpenChest() {
        return openChest;
    }

    public void setOpenChest(boolean openChest) {
        this.openChest = openChest;
    }

    public boolean canUseCommands() {
        return useCommands;
    }

    public void setUseCommands(boolean useCommands) {
        this.useCommands = useCommands;
    }

    public boolean isOwner() {
        return terreno.getOwner().equalsIgnoreCase(player);
    }

    public void togglePermission(EPermissions perm) {
        switch (perm) {
            case BUILD:
                placeBlocks = !placeBlocks;
                break;
            case BREAK:
                breakBlock = !breakBlock;
                break;
            case CHEST:
                openChest = !openChest;
                break;
            case COMMANDS:
                useCommands = !useCommands;
                break;
        }
        save();
    }

    public void save() {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            DBCore db = Main.getInstance().getDBCore();
            String sql = "INSERT INTO `core_terrenos_perms` " +
                    "(`terreno_id`, `player`, `placeblock`, `breakblock`, `usechest`, `usecommand`) VALUES (?, ?, ?, ?, ?, ?);";
            if (id != null) {
                sql = "UPDATE `core_terrenos_perms` " +
                        "SET `terreno_id` = ?, `player` = ?, `placeblock` = ?, `breakblock` = ?, `usechest` = ?, `usecommand` = ? WHERE `id` = ?;";
            }

            try (PreparedStatement ps = db.prepareStatement(sql, id == null ? PreparedStatement.RETURN_GENERATED_KEYS : PreparedStatement.NO_GENERATED_KEYS)) {

                ps.setInt(1, terreno.getId());
                ps.setString(2, player.toLowerCase());
                ps.setBoolean(3, placeBlocks);
                ps.setBoolean(4, breakBlock);
                ps.setBoolean(5, openChest);
                ps.setBoolean(6, useCommands);

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
            } catch (Exception e) {
                e.printStackTrace();

            }

        });
    }

}
