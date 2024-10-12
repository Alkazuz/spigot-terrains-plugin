package br.alkazuz.terrenos.listeners;

import br.alkazuz.terrenos.Main;
import br.alkazuz.terrenos.object.Terreno;
import br.alkazuz.terrenos.utils.TerrenoManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Arrays;

public class ItemSpawnTerrainListener implements Listener {

    private final Material[] allowedMaterials = new Material[]{
            Material.CACTUS
    };

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent e) {
        if (!e.getLocation().getWorld().getName().equalsIgnoreCase("region")) return;
        Terreno terreno = TerrenoManager.getTerrenoInLocation(e.getLocation());
        if (terreno == null) {
            return;
        }

        if (!Arrays.asList(allowedMaterials).contains(e.getEntity().getItemStack().getType())) {
            return;
        }

        if (e.getEntity().hasMetadata("ignore")) {
            return;
        }

        terreno.addStorage(e.getEntity().getItemStack().getType(), e.getEntity().getItemStack().getAmount());
        e.getEntity().remove();
        e.setCancelled(true);
        terreno.queueSaveStorage();
    }

    @EventHandler
    public void playerDropItem(PlayerDropItemEvent e) {
        if (!e.getPlayer().getWorld().getName().equalsIgnoreCase("region")) return;
        Terreno terreno = TerrenoManager.getTerrenoInLocation(e.getPlayer().getLocation());
        if (terreno == null) return;

        if (!Arrays.asList(allowedMaterials).contains(e.getItemDrop().getItemStack().getType())) return;

        e.getItemDrop().setMetadata("ignore", new FixedMetadataValue(Main.getInstance(), true));
    }
}
