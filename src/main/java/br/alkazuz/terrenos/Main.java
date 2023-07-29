package br.alkazuz.terrenos;

import br.alkazuz.terrenos.command.CommandTerreno;
import br.alkazuz.terrenos.command.SubCommands;
import br.alkazuz.terrenos.config.Settings;
import br.alkazuz.terrenos.config.inventory.listen.InventoryClickListenner;
import br.alkazuz.terrenos.config.manager.ConfigManager;
import br.alkazuz.terrenos.listeners.SellRegionListener;
import br.alkazuz.terrenos.utils.EventWaiter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private static Main instance;
    private WorldGuardPlugin worldGuard;
    private WorldEditPlugin worldEdit;
    private EventWaiter eventWaiter;
    private Economy econ;

    public static Main getInstance() {
        return Main.instance;
    }

    @Override
    public void onEnable() {
        Main.instance = this;
        genSetings();
        loadSettings();
        load();
    }

    public void load() {
        SubCommands.load();
        this.eventWaiter = new EventWaiter(this);
        eventWaiter.addEvents(InventoryClickEvent.class,
                InventoryCloseEvent.class,
                PlayerChatEvent.class);
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new InventoryClickListenner(), this);
        pm.registerEvents(new br.alkazuz.terrenos.config.inventory.listen.BlockBreakEvent(), this);
        pm.registerEvents(new SellRegionListener(),  this);

        getCommand("terreno").setExecutor(new CommandTerreno());

        setupEconomy();
    }

    public void genSetings() {
        ConfigManager.createConfig("settings");
    }

    public void loadSettings() {
        Settings.load();
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null)
            econ = (Economy)economyProvider.getProvider();
        return (econ != null);
    }
    public WorldEditPlugin getWorldEdit() {
        if (worldEdit != null)
            return worldEdit;
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldEdit");
        if (plugin == null || !(plugin instanceof WorldEditPlugin))
            return null;
        worldEdit = (WorldEditPlugin) plugin;
        return worldEdit;
    }
    public WorldGuardPlugin getWorldGuard() {
        if (worldGuard != null)
            return worldGuard;
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
        if (plugin == null || !(plugin instanceof WorldGuardPlugin))
            return null;
        worldGuard = (WorldGuardPlugin) plugin;
        return worldGuard;
    }

    public Economy getEconomy() {
        return econ;
    }

    public EventWaiter getEventWaiter() {
        return eventWaiter;
    }

}
