package br.alkazuz.terrenos.config;

import br.alkazuz.terrenos.config.manager.ConfigManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Settings {
    public static List<Size> SIZES = new ArrayList<>();
    public static String DEFAULT_WORLD;

    public static boolean MYSQL_ENABLED;
    public static String MYSQL_HOST;
    public static String MYSQL_DATABASE;
    public static String MYSQL_USER;
    public static String MYSQL_PASSWORD;
    public static String TERRAIN_WORLD;
    public static HashMap<Material, Double> MATERIAL_PRICES = new HashMap<>();
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
            TERRAIN_WORLD = config.getString("TerrainWorld");
            MYSQL_ENABLED = config.getBoolean("MySQL.enabled");
            MYSQL_HOST = config.getString("MySQL.host");
            MYSQL_DATABASE = config.getString("MySQL.database");
            MYSQL_USER = config.getString("MySQL.username");
            MYSQL_PASSWORD = config.getString("MySQL.password");
            MAX_REGIONS = config.getInt("MaxRegions");
            MAX_VIP_REGIONS = config.getInt("MaxVipRegions");
            PVP_PRICE = config.getInt("Prices.PVP");
            FALL_DAMAGE_PRICE = config.getInt("Prices.FallDamage");
            DEFAULT_WORLD = config.getString("DefaultWorld");
            if (config.contains("PricesStorage")) {
                for (String key : config.getConfigurationSection("PricesStorage").getKeys(false)) {
                    Material material = Material.getMaterial(key);
                    double price = config.getDouble("PricesStorage." + key);
                    MATERIAL_PRICES.put(material, price);
                }
            }
        } catch (Exception e) {
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
