package br.alkazuz.terrenos.listeners;

import br.alkazuz.terrenos.config.Settings;
import br.alkazuz.terrenos.utils.TerrenoManager;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class LazyLoadingTerrainListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onMove(PlayerMoveEvent event) {
        if (event.getFrom().getChunk().equals(event.getTo().getChunk())) return;
        Chunk from = event.getFrom().getChunk();
        if (!from.getWorld().getName().equals(Settings.TERRAIN_WORLD)) return;
        TerrenoManager.loadTerrainsInRadius(from, 2);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onTeleport(PlayerTeleportEvent event) {
        Chunk to = event.getTo().getChunk();
        if (!to.getWorld().getName().equals(Settings.TERRAIN_WORLD)) return;
        TerrenoManager.loadTerrainsInRadius(to, 2);
    }
}
