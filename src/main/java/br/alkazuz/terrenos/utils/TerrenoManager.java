package br.alkazuz.terrenos.utils;

import br.alkazuz.terrenos.Main;
import br.alkazuz.terrenos.config.Settings;
import br.alkazuz.terrenos.object.Terreno;
import br.alkazuz.terrenos.storage.DBCore;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class TerrenoManager {
    private static final Main main = Main.getInstance();
    private static final HashSet<Integer> alreadyLoadedChunks = new HashSet<>();
    private static final HashMap<Integer, Terreno> terrenos = new HashMap<>();

    public static void loadTerrainsChunks(Chunk chunk) {
        int computeHash = Serializer.computeHash(chunk);
        if (alreadyLoadedChunks.contains(computeHash)) {
            return;
        }

        DBCore database = main.getDBCore();

        String query = "SELECT * FROM `core_terrenos` WHERE `x1` <= ? AND `x2` >= ? AND `z1` <= ? AND `z2` >= ? AND `world` = ?";

        PreparedStatement ps = database.prepareStatement(query);
        try {
            ps.setInt(1, chunk.getX() * 16);
            ps.setInt(2, chunk.getX() * 16 + 15);
            ps.setInt(3, chunk.getZ() * 16);
            ps.setInt(4, chunk.getZ() * 16 + 15);
            ps.setString(5, chunk.getWorld().getName());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                int x1 = rs.getInt("x1");
                int x2 = rs.getInt("x2");
                int z1 = rs.getInt("z1");
                int z2 = rs.getInt("z2");
                String owner = rs.getString("owner");
                String world = rs.getString("world");

                Terreno terreno = new Terreno(id, owner, x1, x2, z1, z2, world);

                int computeTerrainHash = Serializer.computeHash(terreno);

                terrenos.put(computeTerrainHash, terreno);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static void loadTerrainsForChunk(Chunk chunk) {
        DBCore database = main.getDBCore();
        String query = "SELECT * FROM `core_terrenos` WHERE `x1` <= ? AND `x2` >= ? AND `z1` <= ? AND `z2` >= ? AND `world` = ?";

        try (PreparedStatement ps = database.prepareStatement(query)) {
            ps.setInt(1, chunk.getX() * 16);
            ps.setInt(2, chunk.getX() * 16 + 15);
            ps.setInt(3, chunk.getZ() * 16);
            ps.setInt(4, chunk.getZ() * 16 + 15);
            ps.setString(5, chunk.getWorld().getName());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                int x1 = rs.getInt("x1");
                int x2 = rs.getInt("x2");
                int z1 = rs.getInt("z1");
                int z2 = rs.getInt("z2");
                String owner = rs.getString("owner");
                String world = rs.getString("world");

                Terreno terreno = new Terreno(id, owner, x1, x2, z1, z2, world);
                int computeTerrainHash = Serializer.computeHash(terreno);
                terrenos.put(computeTerrainHash, terreno);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadTerrainsInRadius(Chunk centerChunk, int radius) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                Chunk chunk = centerChunk.getWorld().getChunkAt(centerChunk.getX() + dx, centerChunk.getZ() + dz);
                int computeHash = Serializer.computeHash(chunk);
                if (!alreadyLoadedChunks.contains(computeHash)) {
                    loadTerrainsChunks(chunk);
                    alreadyLoadedChunks.add(computeHash);
                }
            }
        }
    }

    public static Terreno getTerrenoInLocation(Location location) {
        for (Terreno terreno : terrenos.values()) {
            if (terreno.isOnTerrain(location)) {
                return terreno;
            }
        }
        return null;
    }

    public static String getRegionMessageInfo(Location location) {
        StringBuilder stringBuilder = new StringBuilder();
        Terreno regionInfo = getTerrenoInLocation(location);
        if (regionInfo == null) {
            stringBuilder.append("§cVocê não está em um terreno.");
        } else {
            stringBuilder.append("§eInformações do terreno:\n");
            stringBuilder.append("§eDono: §f").append(PlayerUtils.getPlayerPrefix(regionInfo.getOwner())).append("\n");
            /*stringBuilder.append(String.format("§eAmigos (%d): §f", regionInfo.getFriends().size()));
            if (regionInfo.getFriends().size() == 0){
                stringBuilder.append("§cNenhum amigo adicionado.\n");
            } else {
                for (String friend : regionInfo.getFriends()) {
                    stringBuilder.append(friend).append(", ");
                }
                stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
                stringBuilder.append("\n");
            }*/
            stringBuilder.append("§ePvP: §f").append(regionInfo.isPvp() ? "§aLigado" : "§cDesligado").append("\n");
        }
        return stringBuilder.toString();
    }

    public static void sellRegion(Player player, Terreno region, int price) {
        Location location = player.getLocation();
        if (location.getBlock().getType() != Material.AIR) {
            throw new IllegalArgumentException("Você precisa estar em um espaço vazio para vender o terreno.");
        }
        Block block = location.getWorld().getBlockAt(location);
        block.setType(Material.SIGN_POST);
        Sign sig = (Sign)block.getState();
        sig.setLine(0, "§e[COMPRAR]");
        sig.setLine(1, region.getOwner());
        sig.setLine(2, NumberFormat.getInstance().format(price));
        sig.setLine(3, "§aClique aqui!");
        sig.update();
    }

    public static Location findRandomEmptyRegionLocation(World world, Settings.Size terrainsSize) {
        int size = terrainsSize.getSize();
        DBCore database = main.getDBCore();

        String extremasQuery = "SELECT MAX(x2) AS maxX, MIN(x1) AS minX, MAX(z2) AS maxZ, MIN(z1) AS minZ FROM `core_terrenos` WHERE `world` = ?";
        try (PreparedStatement psExtremas = database.prepareStatement(extremasQuery)) {
            psExtremas.setString(1, world.getName());
            ResultSet rsExtremas = psExtremas.executeQuery();

            if (rsExtremas.next()) {
                int maxX = rsExtremas.getInt("maxX");
                int minX = rsExtremas.getInt("minX");
                int maxZ = rsExtremas.getInt("maxZ");
                int minZ = rsExtremas.getInt("minZ");

                String checkQuery = "SELECT COUNT(*) FROM `core_terrenos` WHERE `world` = ? AND NOT (`x2` <= ? OR `x1` >= ? OR `z2` <= ? OR `z1` >= ?)";
                Random random = new Random();

                for (int attempts = 0; attempts < 1000; attempts++) {
                    int x = random.nextInt(maxX - minX + size) + minX - size;
                    int z = random.nextInt(maxZ - minZ + size) + minZ - size;

                    try (PreparedStatement psCheck = database.prepareStatement(checkQuery)) {
                        psCheck.setString(1, world.getName());
                        psCheck.setInt(2, x);
                        psCheck.setInt(3, x + size);
                        psCheck.setInt(4, z);
                        psCheck.setInt(5, z + size);
                        ResultSet rsCheck = psCheck.executeQuery();

                        if (rsCheck.next() && rsCheck.getInt(1) == 0) {
                            return new Location(world, x, world.getHighestBlockYAt(x, z), z);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean hasNearReagion(Location location, int radius) {
        int x = location.getBlockX();
        int z = location.getBlockZ();
        int y = location.getBlockY();
        for (int i = x - radius; i < x + radius + 5; i++) {
            for (int j = z - radius; j < z + radius + 5; j++) {
                for (int k = y - radius; k < y + radius + 5; k++) {
                    if (getTerrenoInLocation(new Location(location.getWorld(), i, k, j)) != null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean createTerreno(Player p, Location randomLoc, Settings.Size size) throws Exception {
        Chunk chunk = randomLoc.getChunk();
        loadTerrainsInRadius(chunk, 12);
        if (hasNearReagion(randomLoc, size.getSize())) {
            throw new IllegalArgumentException("§cVocê não pode criar um terreno perto de outro.");
        }

        Economy economy = main.getEconomy();
        int price = p.hasPermission("terrenos.vip") ? size.getVipPrice() : size.getPrice();

        if (!economy.has(p.getName(), price)) {
            throw new IllegalArgumentException("§cVocê não tem dinheiro suficiente para comprar o terreno.");
        }

        if(economy.withdrawPlayer(p.getName(), price).type != EconomyResponse.ResponseType.SUCCESS) {
            throw new Exception("§cOcorreu um erro ao retirar o dinheiro da sua conta.");
        }

        int x1 = randomLoc.getBlockX() - size.getSize() / 2;
        int x2 = randomLoc.getBlockX() + size.getSize() / 2;
        int z1 = randomLoc.getBlockZ() - size.getSize() / 2;
        int z2 = randomLoc.getBlockZ() + size.getSize() / 2;

        Terreno terreno = new Terreno(null, p.getName(), x1, x2, z1, z2, randomLoc.getWorld().getName());
        terreno.save();

        makeBorderFance(randomLoc, size);
        if (size.isMobSpawn()) {
            Location center = new Location(randomLoc.getWorld(), randomLoc.getX(), randomLoc.getBlockY() + 5, randomLoc.getY());
            center.getWorld().getBlockAt(center).setType(Material.MOB_SPAWNER);
        }

        terrenos.put(Serializer.computeHash(terreno), terreno);

        return true;
    }

    private static void makeBorderFance(Location location, Settings.Size size) {
        int x = location.getBlockX();
        int z = location.getBlockZ();
        int y = location.getBlockY();
        World world = location.getWorld();
        for (int i = x - (size.getSize() / 2); i < x + (size.getSize() / 2); i++) {
            for (int j = z - (size.getSize() / 2); j < z + (size.getSize() / 2); j++) {
                if (i != x - (size.getSize() / 2) && i != x + (size.getSize() / 2) - 1 && j != z - (size.getSize() / 2) && j != z + (size.getSize() / 2) - 1) continue;
                world.getBlockAt(i, y, j).setType(Material.FENCE);
                world.getBlockAt(i, y + 1, j).setType(Material.AIR);
            }
        }
    }
}
