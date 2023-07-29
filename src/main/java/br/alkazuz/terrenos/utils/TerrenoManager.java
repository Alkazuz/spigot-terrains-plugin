package br.alkazuz.terrenos.utils;

import br.alkazuz.terrenos.Main;
import br.alkazuz.terrenos.config.Settings;
import com.google.common.collect.Lists;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.List;

public class TerrenoManager {
    private static final Main main = Main.getInstance();
    public static String getRegionMessageInfo(Location location) {
        StringBuilder stringBuilder = new StringBuilder();
        RegionInfo regionInfo = getRegionInfo(location);
        if (regionInfo == null) {
            stringBuilder.append("§cVocê não está em um terreno.");
        } else {
            stringBuilder.append("§eInformações do terreno:\n");
            stringBuilder.append("§eDono: §f").append(regionInfo.getOwner()).append("\n");
            stringBuilder.append(String.format("§eAmigos (%d): §f", regionInfo.getFriends().size()));
            if (regionInfo.getFriends().size() == 0){
                stringBuilder.append("§cNenhum amigo adicionado.\n");
            } else {
                for (String friend : regionInfo.getFriends()) {
                    stringBuilder.append(friend).append(", ");
                }
                stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
                stringBuilder.append("\n");
            }
            stringBuilder.append("§ePvP: §f").append(regionInfo.isPvp() ? "§aLigado" : "§cDesligado").append("\n");
        }
        return stringBuilder.toString();
    }

    public static ProtectedRegion getProtectedRegion(Location location) {
        RegionManager regionManager = main.getWorldGuard().getRegionManager(location.getWorld());
        ApplicableRegionSet regions = regionManager.getApplicableRegions(location);
        if (regions.size() > 0) {
            String id = ((ProtectedRegion)regions.iterator().next()).getId();
            return regionManager.getRegion(id);
        }
        return null;
    }

    public static RegionInfo getRegionInfo(Location location) {
        ProtectedRegion region = getProtectedRegion(location);
        if (region == null) return null;
        if (region.getOwners() == null
                || region.getOwners().getPlayers() == null
                || region.getOwners().getPlayers().size() == 0) return null;
        RegionInfo regionInfo = new RegionInfo();
        if (region.getMembers().size() > 0) {
            regionInfo.setOwner(region.getOwners().getPlayers().iterator().next());
            regionInfo.setFriends(Lists.newArrayList(region.getMembers().getPlayers()));
        } else {
            regionInfo.setOwner(region.getOwners().getPlayers().iterator().next());
        }

        regionInfo.setPvp(region.getFlag(DefaultFlag.PVP) == StateFlag.State.ALLOW);

        return regionInfo;
    }

    public static Location findRandomEmptyRegionLocation(World word, Settings.Size size) {
        RegionManager regionManager = main.getWorldGuard().getRegionManager(word);
        int x = 0;
        int z = 0;
        int y = 0;
        int radiusFar = size.getSize();
        int min = -10_000;
        int max = 10_000;
        int radius = radiusFar;
        boolean find = false;
        while (true) {
            x = (int) (Math.random() * (max - min)) + min;
            z = (int) (Math.random() * (max - min)) + min;
            y = word.getHighestBlockYAt(x, z);
            if (y < 0) continue;
            if (regionManager.getApplicableRegions(word.getBlockAt(x, y, z).getLocation()).size() == 0) {
                find = true;
                break;
            }
            radius--;
            if (radius == 0) {
                radius = radiusFar;
                radiusFar += 100;
            }
        }
        if (!find) return null;
        return new Location(word, x, y, z);
    }

    public static void saveRegion(Location location) throws ProtectionDatabaseException {
        RegionManager regionManager = main.getWorldGuard().getRegionManager(location.getWorld());
        regionManager.save();
    }

    public static boolean createTerreno(Player p, Location location, Settings.Size size) throws Exception {
        if (hasNearReagion(location, size.getSize())) {
            throw new IllegalArgumentException("§cVocê não pode criar um terreno perto de outro.");
        }
        Economy economy = main.getEconomy();
        int price = p.hasPermission("terrenos.vip") ? size.getVipPrice() : size.getPrice();
        if (economy.getBalance(p) < price) {
            throw new IllegalArgumentException("§cVocê não tem dinheiro suficiente para comprar um terreno.");
        }
        if(economy.withdrawPlayer(p, price) == null) {
            throw new Exception("§cOcorreu um erro ao retirar o dinheiro da sua conta.");
        }
        RegionManager regionManager = main.getWorldGuard().getRegionManager(location.getWorld());
        int locx = location.getBlockX();
        int locz = location.getBlockZ();
        BlockVector bv1 = new BlockVector(locx - (size.getSize() / 2), 256, locz - (size.getSize() / 2));
        BlockVector bv2 = new BlockVector(locx + (size.getSize() / 2), 0, locz + (size.getSize() / 2));
        ProtectedCuboidRegion region = new ProtectedCuboidRegion(p.getName() + "_" + System.currentTimeMillis(), bv1, bv2);
        DefaultDomain dd = new DefaultDomain();
        regionManager.addRegion(region);
        region.setPriority(100);
        dd.addPlayer(p.getName());
        region.setOwners(dd);

        region.setFlag(DefaultFlag.PVP, DefaultFlag.PVP.parseInput(main.getWorldGuard(), p, "allow"));
        region.setFlag(DefaultFlag.USE, DefaultFlag.USE.parseInput(main.getWorldGuard(), p, "deny"));
        region.setFlag(DefaultFlag.ENDER_BUILD, DefaultFlag.ENDER_BUILD.parseInput(main.getWorldGuard(), p, "deny"));
        region.setFlag(DefaultFlag.CREEPER_EXPLOSION, DefaultFlag.CREEPER_EXPLOSION.parseInput(main.getWorldGuard(), p, "deny"));

        try {
            regionManager.save();
            makeBorderFance(location, size);
            if (size.isMobSpawn()) {
                Location center = new Location(location.getWorld(), locx, location.getBlockY() + 5, locz);
                center.getWorld().getBlockAt(center).setType(Material.MOB_SPAWNER);
            }
        } catch (final ProtectionDatabaseException e) {
            throw new Exception("§cOcorreu um erro ao salvar o terreno.");

        }
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

    public static boolean hasNearReagion(Location location, int radius) {
        RegionManager regionManager = main.getWorldGuard().getRegionManager(location.getWorld());
        int x = location.getBlockX();
        int z = location.getBlockZ();
        int y = location.getBlockY();
        for (int i = x - radius; i < x + radius + 5; i++) {
            for (int j = z - radius; j < z + radius + 5; j++) {
                for (int k = y - radius; k < y + radius + 5; k++) {
                    if (regionManager.getApplicableRegions(location.getWorld().getBlockAt(i, k, j).getLocation()).size() > 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void sellRegion(Player player, ProtectedRegion region, int price) {
        Location location = player.getLocation();
        if (location.getBlock().getType() != Material.AIR) {
            throw new IllegalArgumentException("Você precisa estar em um espaço vazio para vender o terreno.");
        }
        Block block = location.getWorld().getBlockAt(location);
        block.setType(Material.SIGN_POST);
        Sign sig = (Sign)block.getState();
        sig.setLine(0, "§e[COMPRAR]");
        sig.setLine(1, region.getOwners().getPlayers().toArray()[0].toString());
        sig.setLine(2, NumberFormat.getInstance().format(price));
        sig.setLine(3, "§aClique aqui!");
        sig.update();
    }

    public static void buyRegion(Player player, ProtectedRegion region, int price) {
        Economy economy = main.getEconomy();
        String owner = region.getOwners().getPlayers().toArray()[0].toString();
        if (economy.getBalance(player) < price) {
            throw new IllegalArgumentException("Você não tem dinheiro suficiente para comprar este terreno.");
        }
        if(economy.withdrawPlayer(player, price) == null) {
            throw new IllegalArgumentException("Ocorreu um erro ao retirar o dinheiro da sua conta.");
        }
        economy.depositPlayer(owner, price);
        DefaultDomain dd = new DefaultDomain();
        dd.addPlayer(player.getName());
        region.setOwners(dd);
        region.setMembers(new DefaultDomain());
        try {
            RegionManager regionManager = main.getWorldGuard().getRegionManager(player.getWorld());
            regionManager.save();
        } catch (ProtectionDatabaseException e) {
            throw new IllegalArgumentException("Ocorreu um erro aofazer a transação de terrenos.");
        }
    }

    public static class RegionInfo {
        private String owner;
        private List<String> friends = Lists.newArrayList();
        private boolean pvp;

        public RegionInfo() {

        }

        public RegionInfo(String owner, boolean pvp) {
            this.owner = owner;
            this.pvp = pvp;
        }

        public String getOwner() {
            return owner;
        }

        public List<String> getFriends() {
            return friends;
        }

        public boolean isPvp() {
            return pvp;
        }


        public void setOwner(String owner) {
            this.owner = owner;
        }

        public void setPvp(boolean pvp) {
            this.pvp = pvp;
        }


        public void addFriend(String friend) {
            this.friends.add(friend);
        }

        public void removeFriend(String friend) {
            this.friends.remove(friend);
        }

        public boolean isFriend(String friend) {
            return this.friends.contains(friend);
        }

        public void setFriends(List<String> friends) {
            this.friends = friends;
        }

        public void clearFriends() {
            this.friends.clear();
        }
    }
}
