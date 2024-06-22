package br.alkazuz.terrenos;

import br.alkazuz.terrenos.command.CommandTerreno;
import br.alkazuz.terrenos.command.SubCommands;
import br.alkazuz.terrenos.config.Settings;
import br.alkazuz.terrenos.inventory.GuiBuyInventory;
import br.alkazuz.terrenos.inventory.GuiFlagsInventory;
import br.alkazuz.terrenos.inventory.GuiPermsInventory;
import br.alkazuz.terrenos.inventory.GuiTerrenosInventory;
import br.alkazuz.terrenos.listeners.PlayerInTerrainListener;
import br.alkazuz.terrenos.inventory.listen.InventoryClickListenner;
import br.alkazuz.terrenos.config.manager.ConfigManager;
import br.alkazuz.terrenos.listeners.LazyLoadingTerrainListener;
import br.alkazuz.terrenos.listeners.PlayerJoinListener;
import br.alkazuz.terrenos.listeners.SellRegionListener;
import br.alkazuz.terrenos.storage.DBCore;
import br.alkazuz.terrenos.storage.MySQLCore;
import br.alkazuz.terrenos.storage.SQLiteCore;
import br.alkazuz.terrenos.utils.EventWaiter;
import br.alkazuz.terrenos.utils.GuiHolder;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private static Main instance;
    private EventWaiter eventWaiter;
    private Economy econ;
    private DBCore database;
    private Vault vault;

    public static Main getInstance() {
        return Main.instance;
    }

    @Override
    public void onEnable() {
        Main.instance = this;
        genSetings();
        loadSettings();
        load();
        startDatabase();
    }

    @Override
    public void onDisable() {
        if (database != null)
            database.close();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getOpenInventory() != null) {
                if (player.getOpenInventory().getTopInventory().getHolder() instanceof GuiHolder) {
                    player.closeInventory();
                }
            }
        }
    }

    public void load() {
        SubCommands.load();
        this.eventWaiter = new EventWaiter(this);
        eventWaiter.addEvents(InventoryClickEvent.class,
                InventoryCloseEvent.class,
                PlayerChatEvent.class);
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new InventoryClickListenner(), this);
        pm.registerEvents(new PlayerInTerrainListener(), this);
        pm.registerEvents(new SellRegionListener(),  this);
        pm.registerEvents(new LazyLoadingTerrainListener(), this);
        pm.registerEvents(new GuiPermsInventory(), this);
        pm.registerEvents(new GuiFlagsInventory(), this);
        pm.registerEvents(new GuiBuyInventory(), this);
        pm.registerEvents(new GuiTerrenosInventory(), this);
        pm.registerEvents(new PlayerJoinListener(), this);

        getCommand("terreno").setExecutor(new CommandTerreno());

        setupEconomy();
    }

    private void startDatabase() {
        if (Settings.MYSQL_ENABLED) {
            database = new MySQLCore(Settings.MYSQL_HOST, Settings.MYSQL_DATABASE, Settings.MYSQL_USER, Settings.MYSQL_PASSWORD);
        } else {
            database = new SQLiteCore(getDataFolder().getPath());
        }

        if (database.checkConnection()) {
            database.execute("CREATE TABLE IF NOT EXISTS `core_terrenos` (" +
                    "`id` INT NOT NULL AUTO_INCREMENT," +
                    "`owner` VARCHAR(20) NOT NULL," +
                    "`x1` INT NOT NULL," +
                    "`x2` INT NOT NULL," +
                    "`z1` INT NOT NULL," +
                    "`z2` INT NOT NULL," +
                    "`world` VARCHAR(255) NOT NULL," +
                    "`created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "`deleted_at` TIMESTAMP NULL DEFAULT NULL," +
                    "PRIMARY KEY (`id`));");
            database.execute("CREATE TABLE IF NOT EXISTS `core_terrenos_perms` (" +
                    "`id` INT NOT NULL AUTO_INCREMENT," +
                    "`terreno_id` INT NOT NULL," +
                    "`player` VARCHAR(20) NOT NULL," +
                    "`placeblock` BOOLEAN DEFAULT 0," +
                    "`breakblock` BOOLEAN DEFAULT 0," +
                    "`usechest` BOOLEAN DEFAULT 0," +
                    "`usecommand` BOOLEAN DEFAULT 0," +
                    "PRIMARY KEY (`id`));");
            database.execute("CREATE TABLE IF NOT EXISTS `core_terrenos_flags` (" +
                    "`terreno_id` INT NOT NULL," +
                    "`key` VARCHAR(20) NOT NULL," +
                    "`value` VARCHAR(255) NOT NULL);");
        }
    }

    public void genSetings() {
        ConfigManager.createConfig("settings");
    }

    public void loadSettings() {
        Settings.load();
    }

    public DBCore getDBCore() {
        return this.database;
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null)
            econ = (Economy)economyProvider.getProvider();
        return (econ != null);
    }

    public Vault getVault() {
        if (vault == null)
            vault = (Vault)getServer().getPluginManager().getPlugin("Vault");
        return vault;
    }

    public Economy getEconomy() {
        return econ;
    }

    public EventWaiter getEventWaiter() {
        return eventWaiter;
    }

    public static void debug(String message) {
        Bukkit.getConsoleSender().sendMessage("§e[Terrenos] " + message);
    }

}
