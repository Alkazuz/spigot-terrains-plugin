package br.alkazuz.terrenos.config.inventory.listen;

import br.alkazuz.terrenos.config.Settings;
import br.alkazuz.terrenos.utils.TerrenoManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BlockBreakEvent implements Listener {

    @EventHandler(priority = org.bukkit.event.EventPriority.HIGHEST)
    public void onBreak(org.bukkit.event.block.BlockBreakEvent e) {
        if (e.getPlayer().hasPermission("terrenos.admin")) return;
        if (!e.getBlock().getWorld().getName().equalsIgnoreCase(Settings.TERRAIN_WORLD)) return;
        if (e.isCancelled()) return;
        if (TerrenoManager.getTerrenoInLocation(e.getBlock().getLocation()) != null) return;
        e.setCancelled(true);
    }

    @EventHandler(priority = org.bukkit.event.EventPriority.HIGHEST)
    public void onPlace(org.bukkit.event.block.BlockPlaceEvent e) {
        if (e.getPlayer().hasPermission("terrenos.admin")) return;
        if (!e.getBlock().getWorld().getName().equalsIgnoreCase(Settings.TERRAIN_WORLD)) return;
        if (e.isCancelled()) return;
        if (TerrenoManager.getTerrenoInLocation(e.getBlock().getLocation()) != null) return;
        e.setCancelled(true);
    }
}
