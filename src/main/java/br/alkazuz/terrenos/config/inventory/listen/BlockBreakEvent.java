package br.alkazuz.terrenos.config.inventory.listen;

import br.alkazuz.terrenos.utils.TerrenoManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BlockBreakEvent implements Listener {

    @EventHandler(ignoreCancelled = false, priority = org.bukkit.event.EventPriority.HIGHEST)
    public void onBreak(org.bukkit.event.block.BlockBreakEvent e) {
        if (e.getPlayer().hasPermission("terrenos.admin")) return;
        if (!e.getBlock().getWorld().getName().equalsIgnoreCase("region")) return;
        if (e.isCancelled()) return;
        if (TerrenoManager.getProtectedRegion(e.getBlock().getLocation()) != null) return;
        e.setCancelled(true);
        e.getPlayer().sendMessage("§cVocê não pode quebrar blocos aqui. Use §e/terreno info §cpara mais informações.");
    }

    @EventHandler(ignoreCancelled = false, priority = org.bukkit.event.EventPriority.HIGHEST)
    public void onPlace(org.bukkit.event.block.BlockPlaceEvent e) {
        if (e.getPlayer().hasPermission("terrenos.admin")) return;
        if (!e.getBlock().getWorld().getName().equalsIgnoreCase("region")) return;
        if (e.isCancelled()) return;
        if (TerrenoManager.getProtectedRegion(e.getBlock().getLocation()) != null) return;
        e.setCancelled(true);
        e.getPlayer().sendMessage("§cVocê não pode quebrar blocos aqui. Use §e/terreno info §cpara mais informações.");
    }
}
