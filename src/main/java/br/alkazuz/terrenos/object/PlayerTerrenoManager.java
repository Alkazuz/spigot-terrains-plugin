package br.alkazuz.terrenos.object;

import br.alkazuz.terrenos.Main;
import br.alkazuz.terrenos.storage.DBCore;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerTerrenoManager {
    private static final PlayerTerreno DEFAULT_PLAYER = new PlayerTerreno(null);
    private static final HashMap<String, HashMap<Integer, PlayerTerreno>> playerTerrenos = new HashMap<>();

    public static PlayerTerreno getPlayerTerrenoOrDefault(Player player, Terreno terreno) {
        if (!playerTerrenos.containsKey(player.getName().toLowerCase())) {
            playerTerrenos.put(player.getName(), new HashMap<>());
        }

        HashMap<Integer, PlayerTerreno> map = playerTerrenos.get(player.getName().toLowerCase());

        if (map.containsKey(terreno.getId())) {
            return map.get(terreno.getId());
        }

        return DEFAULT_PLAYER;
    }

    public static PlayerTerreno getPlayerTerreno(String player, Terreno terreno) {
        player = player.toLowerCase();
        if (!playerTerrenos.containsKey(player)) {
            playerTerrenos.put(player, new HashMap<>());
        }

        HashMap<Integer, PlayerTerreno> map = playerTerrenos.get(player);

        if (map.containsKey(terreno.getId())) {
            return map.get(terreno.getId());
        }

        DBCore dbCore = Main.getInstance().getDBCore();

        String query = "SELECT * FROM `core_terrenos_perms` WHERE `player` = ? AND `terreno_id` = ?";
        PreparedStatement stm = dbCore.prepareStatement(query);

        try {
            stm.setString(1, player.toLowerCase());
            stm.setInt(2, terreno.getId());

            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                PlayerTerreno playerTerreno = new PlayerTerreno(player);
                playerTerreno.setTerreno(terreno);
                playerTerreno.setPlaceBlocks(rs.getBoolean("placeblock"));
                playerTerreno.setBreakBlock(rs.getBoolean("breakblock"));
                playerTerreno.setOpenChest(rs.getBoolean("usechest"));
                playerTerreno.setUseCommands(rs.getBoolean("usecommand"));

                map.put(terreno.getId(), playerTerreno);

                return playerTerreno;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        PlayerTerreno playerTerreno = new PlayerTerreno(player);
        playerTerreno.setTerreno(terreno);
        playerTerreno.save();
        map.put(terreno.getId(), playerTerreno);

        return playerTerreno;
    }

    public static List<String> getAllPlayerWithPermissions(Terreno terreno) {
        DBCore dbCore = Main.getInstance().getDBCore();
        List<String> players = new ArrayList<>();
        String query = "SELECT `player` FROM `core_terrenos_perms` WHERE `terreno_id` = ? AND (`placeblock` = ? OR `breakblock` = ?)";
        try {
            PreparedStatement stm = dbCore.prepareStatement(query);
            stm.setInt(1, terreno.getId());
            stm.setBoolean(2, true);
            stm.setBoolean(3, true);

            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                players.add(rs.getString("player"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return players;
    }

    public static void clearPlayersInTerrain(Terreno terreno) {
        for (HashMap<Integer, PlayerTerreno> map : playerTerrenos.values()) {
            map.remove(terreno.getId());
        }
    }

    public static PlayerTerreno getPlayerTerreno(Player player, Terreno terreno) {
        return getPlayerTerreno(player, terreno);
    }

    public static void savePlayerTerreno(PlayerTerreno playerTerreno) {
        String player = playerTerreno.getPlayer().toLowerCase();
        if (!playerTerrenos.containsKey(player)) {
            playerTerrenos.put(player, new HashMap<>());
        }

        HashMap<Integer, PlayerTerreno> map = playerTerrenos.get(player);

        map.put(playerTerreno.getTerreno().getId(), playerTerreno);
    }
}