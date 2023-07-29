package br.alkazuz.terrenos.config;

import br.alkazuz.terrenos.config.manager.ConfigManager;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class Settings {
    public static List<Size> SIZES = new ArrayList<>();
    public static int MAX_REGIONS;
    public static int MAX_VIP_REGIONS;
    public static int PVP_PRICE;
    public static int FALL_DAMAGE_PRICE;
    public static void load() {
        try {
            FileConfiguration config = ConfigManager.getConfig("settings");
            for (String key : config.getConfigurationSection("Sizes").getKeys(false)) {
                int size = Integer.parseInt(key);
                int price = config.getInt("Sizes." + key + ".Price");
                int vipPrice = config.getInt("Sizes." + key + ".VipPrice");
                boolean mobSpawn = config.getBoolean("Sizes." + key + ".MobSpawn");
                SIZES.add(new Size(size, price, vipPrice, mobSpawn));
            }
            MAX_REGIONS = config.getInt("MaxRegions");
            MAX_VIP_REGIONS = config.getInt("MaxVipRegions");
            PVP_PRICE = config.getInt("Prices.PVP");
            FALL_DAMAGE_PRICE = config.getInt("Prices.FallDamage");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static class Size {
        private int size;
        private int price;
        private int vipPrice;
        private boolean mobSpawn;

        public Size(int size, int price, int vipPrice, boolean mobSpawn) {
            this.size = size;
            this.price = price;
            this.vipPrice = vipPrice;
            this.mobSpawn = mobSpawn;
        }

        public int getSize() {
            return size;
        }

        public int getPrice() {
            return price;
        }

        public int getVipPrice() {
            return vipPrice;
        }

        public boolean isMobSpawn() {
            return mobSpawn;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public void setVipPrice(int vipPrice) {
            this.vipPrice = vipPrice;
        }

        public void setMobSpawn(boolean mobSpawn) {
            this.mobSpawn = mobSpawn;
        }
    }

}
