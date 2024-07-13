package br.alkazuz.terrenos.utils;

import br.alkazuz.terrenos.Main;
import br.alkazuz.terrenos.config.Settings;
import br.alkazuz.terrenos.economy.DepositBank;
import br.alkazuz.terrenos.object.PlayerTerrenoManager;
import br.alkazuz.terrenos.object.Terreno;
import br.alkazuz.terrenos.storage.DBCore;
import br.alkazuz.terrenos.workload.FenceTerrenoWorkload;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

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

        String query = "SELECT * FROM `core_terrenos` WHERE `x2` >= ? AND `x1` <= ? AND `z2` >= ? AND `z1` <= ? AND `world` = ?";

        PreparedStatement ps = database.prepareStatement(query);
        try {
            ps.setInt(1, chunk.getX() * 16);
            ps.setInt(2, chunk.getX() * 16 + 15);
            ps.setInt(3, chunk.getZ() * 16);
            ps.setInt(4, chunk.getZ() * 16 + 15);
            ps.setString(5, chunk.getWorld().getName());

            ResultSet rs = ps.executeQuery();
            int count = 0;

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
                count++;
            }
            Main.debug("Terrenos carregados: " + count);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void loadTerrainsInRadius(Chunk centerChunk, int radius) {
        World world = centerChunk.getWorld();
        int loadedCount = 0;

        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxZ = Integer.MIN_VALUE;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                Chunk chunk = centerChunk.getWorld().getChunkAt(centerChunk.getX() + dx, centerChunk.getZ() + dz);
                int computeHash = Serializer.computeHash(chunk);

                if (!alreadyLoadedChunks.contains(computeHash)) {
                    minX = Math.min(minX, chunk.getX() * 16);
                    maxX = Math.max(maxX, chunk.getX() * 16 + 15);
                    minZ = Math.min(minZ, chunk.getZ() * 16);
                    maxZ = Math.max(maxZ, chunk.getZ() * 16 + 15);
                    alreadyLoadedChunks.add(computeHash);
                } else {
                    loadedCount++;
                }
            }
        }

        if (loadedCount == (2 * radius + 1) * (2 * radius + 1)) {
            return;
        }

        String query = "SELECT * FROM `core_terrenos` WHERE `x1` <= ? AND `x2` >= ? AND `z1` <= ? AND `z2` >= ? AND `world` = ?";
        DBCore database = main.getDBCore();
        try (PreparedStatement ps = database.prepareStatement(query)) {
            ps.setInt(1, maxX);
            ps.setInt(2, minX);
            ps.setInt(3, maxZ);
            ps.setInt(4, minZ);
            ps.setString(5, world.getName());

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                int x1 = rs.getInt("x1");
                int x2 = rs.getInt("x2");
                int z1 = rs.getInt("z1");
                int z2 = rs.getInt("z2");
                String owner = rs.getString("owner");
                String worldName = rs.getString("world");

                Terreno terreno = new Terreno(id, owner, x1, x2, z1, z2, worldName);
                terreno.loadFlags();
                int computeTerrainHash = Serializer.computeHash(terreno);

                if (!terrenos.containsKey(computeTerrainHash)) {
                    terrenos.put(computeTerrainHash, terreno);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
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

    public static void loadPlayerTerrenos(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            DBCore database = main.getDBCore();
            String query = "SELECT * FROM `core_terrenos` WHERE `owner` = ?";
            int count = 0;
            try (PreparedStatement ps = database.prepareStatement(query)) {
                ps.setString(1, player.getName());
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
                    terreno.loadFlags();
                    int computeTerrainHash = Serializer.computeHash(terreno);
                    if (!terrenos.containsKey(computeTerrainHash)) {
                        terrenos.put(computeTerrainHash, terreno);
                        count++;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Main.debug("Foram carregados " + count + " terrenos para o jogador " + player.getName());
        });
    }

    public static List<Terreno> getTerrenosOfPlayer(Player player) {
        return terrenos.values().stream().filter(terreno -> terreno.getOwner().equalsIgnoreCase(player.getName())).collect(Collectors.toList());
    }

    public static String getRegionMessageInfo(Location location) {
        StringBuilder stringBuilder = new StringBuilder();
        Terreno regionInfo = getTerrenoInLocation(location);
        if (regionInfo == null) {
            stringBuilder.append("§cVocê não está em um terreno.");
        } else {
            stringBuilder.append("§e\n§e§lInformações do terreno\n");
            stringBuilder.append("§eID: §f").append(regionInfo.getId()).append("\n");
            stringBuilder.append("§eDono: §f").append(PlayerUtils.getPlayerPrefix(regionInfo.getOwner()) + regionInfo.getOwner()).append("\n");
            stringBuilder.append("§ePvP: §f").append(regionInfo.isPvp() ? "§aLigado" : "§cDesligado").append("\n");
            stringBuilder.append("§ePvP 24 horas: §f").append(regionInfo.isPvp24() ? "§aLigado" : "§cDesligado").append("\n");
            stringBuilder.append("§ePermitido usar /sethome: §f").append(regionInfo.canSetHome() ? "§aSim" : "§cNão").append("\n");
            stringBuilder.append("§ePermitido usar /tpaccept: §f").append(regionInfo.canTpAccept() ? "§aSim" : "§cNão").append("\n");
            List<String> players = PlayerTerrenoManager.getAllPlayerWithPermissions(regionInfo);
            stringBuilder.append("§eJogadores com permissão (").append(players.size()).append("): \n§f");
            for (String player : players) {
                stringBuilder.append(player).append(", ");
            }
            if (!players.isEmpty()) {
                stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
            }
            stringBuilder.append("\n§e");

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
        Sign sig = (Sign) block.getState();
        sig.setLine(0, "§e[COMPRAR]");
        sig.setLine(1, region.getOwner());
        sig.setLine(2, NumberFormat.getInstance().format(price));
        sig.setLine(3, "§aClique aqui!");
        sig.update();
    }

    public static Location findRandomEmptyRegionLocation(World world, Settings.Size terrainsSize) {
        /*int size = terrainsSize.getSize();
        DBCore database = main.getDBCore();
        Random random = new Random();

        BoundsCache.Pair<Integer, Integer> xBounds = BoundsCache.getCachedXBounds(world);
        BoundsCache.Pair<Integer, Integer> zBounds = BoundsCache.getCachedZBounds(world);

        for (int attempts = 0; attempts < 10; attempts++) {
            int x = random.nextInt(xBounds.getRight() - xBounds.getLeft() + size) + xBounds.getLeft() - size;
            int z = random.nextInt(zBounds.getRight() - zBounds.getLeft() + size) + zBounds.getLeft() - size;



            try (PreparedStatement psCheck = database.prepareStatement(checkQuery)) {
                psCheck.setString(1, world.getName());
                psCheck.setInt(2, x);
                psCheck.setInt(3, x + size);
                psCheck.setInt(4, z);
                psCheck.setInt(5, z + size);
                ResultSet rsCheck = psCheck.executeQuery();

                if (rsCheck.next() && rsCheck.getInt(1) == 0
                        && !TerrenoManager.hasNearWorldGuardRegion(new Location(world, x, world.getHighestBlockYAt(x, z), z), size)) {
                    return new Location(world, x, 5, z);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
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

    public static boolean hasNearWorldGuardRegion(Location location, int radius) {
        Location spawn = new Location(location.getWorld(), 0, 0, 0);

        if (location.distance(spawn) < 300) {
            return true;
        }

        return false;
    }

    public static boolean createTerreno(Player p, Location randomLoc, Settings.Size size, boolean loadChunks) throws Exception {
        Chunk chunk = randomLoc.getChunk();

        if (loadChunks) {
            loadTerrainsInRadius(chunk, 5);

        }
        if (hasNearWorldGuardRegion(randomLoc, size.getSize())) {

            throw new IllegalArgumentException("§cVocê não pode criar um terreno neste local, afaste-se de regiões protegidas.");
        }

        if (hasNearReagion(randomLoc, size.getSize())) {

            throw new IllegalArgumentException("§cVocê não pode criar um terreno perto de outro.");
        }

        Economy economy = main.getEconomy();
        int price = p.hasPermission("terrenos.vip") ? size.getVipPrice() : size.getPrice();

        if (economy.getBalance(p.getName()) < price) {
            throw new IllegalArgumentException("§cVocê não tem dinheiro suficiente para comprar o terreno.");
        }

        if (economy.withdrawPlayer(p.getName(), price).type != EconomyResponse.ResponseType.SUCCESS) {
            throw new Exception("§cOcorreu um erro ao retirar o dinheiro da sua conta.");
        }

        DepositBank.depositBank(price);

        int x1 = randomLoc.getBlockX() - size.getSize() / 2;
        int x2 = randomLoc.getBlockX() + size.getSize() / 2;
        int z1 = randomLoc.getBlockZ() - size.getSize() / 2;
        int z2 = randomLoc.getBlockZ() + size.getSize() / 2;

        Terreno terreno = new Terreno(null, p.getName(), x1, x2, z1, z2, randomLoc.getWorld().getName());
        terreno.save();

        if (size.isMobSpawn()) {
            Location center = new Location(randomLoc.getWorld(), randomLoc.getX(), 8, randomLoc.getZ());
            center.getWorld().getBlockAt(center).setType(Material.MOB_SPAWNER);
        }

        terrenos.put(Serializer.computeHash(terreno), terreno);

        Bukkit.getScheduler().runTaskLater(main, () -> {
            makeBorderFence(randomLoc, size);
        }, 20 * 2);

        return true;
    }

    private static void makeBorderFence(Location location, Settings.Size size) {
        FenceTerrenoWorkload workload = new FenceTerrenoWorkload(location, size, null);
        BukkitTask task = Bukkit.getServer().getScheduler().runTaskTimer(Main.getInstance(), workload, 1L, 1L);
        workload.setTask(task);
    }

    public static void buyRegion(Player player, Terreno region, double price) {
        Economy economy = main.getEconomy();
        String owner = region.getOwner();

        if (economy.getBalance(player.getName()) < price) {
            throw new IllegalArgumentException("Você não tem dinheiro suficiente para comprar este terreno.");
        }

        if (!economy.withdrawPlayer(player.getName(), price).transactionSuccess()) {
            throw new IllegalArgumentException("Ocorreu um erro ao retirar o dinheiro da sua conta.");
        }

        if (!economy.depositPlayer(owner, price).transactionSuccess()) {
            economy.depositPlayer(player.getName(), price);
            throw new IllegalArgumentException("Ocorreu um erro ao depositar o dinheiro na conta do dono.");
        }

        region.setOwner(player.getName());
        PlayerTerrenoManager.clearPlayersInTerrain(region);
        region.save();
    }

    public static Terreno getTerrenoById(int i) {
        return terrenos.values().stream().filter(terreno -> terreno.getId() == i).findFirst().orElse(null);
    }

    public static void removeTerreno(Terreno terreno) {
        terrenos.remove(Serializer.computeHash(terreno));
    }
}
