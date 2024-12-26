package br.alkazuz.terrenos.listeners;

import br.alkazuz.terrenos.config.Settings;
import br.alkazuz.terrenos.object.Terreno;
import br.alkazuz.terrenos.utils.TerrenoManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.List;

public class ChunkUnloadListener implements Listener {

    @EventHandler
    public void chunkUnload(ChunkUnloadEvent event) {
        if (!event.getWorld().getName().equals(Settings.TERRAIN_WORLD)) {
            return;
        }
        List<Terreno> terrenos = TerrenoManager.getTerrenosInChunk(event.getChunk());
        boolean isDeleting = terrenos.stream().anyMatch(t -> t.deleting);
        if (isDeleting) {
            event.setCancelled(true);
        }
    }

}
