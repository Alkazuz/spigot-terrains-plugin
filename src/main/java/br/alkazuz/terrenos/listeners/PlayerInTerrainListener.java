package br.alkazuz.terrenos.listeners;

import br.alkazuz.terrenos.config.Settings;
import br.alkazuz.terrenos.object.PlayerTerreno;
import br.alkazuz.terrenos.object.PlayerTerrenoManager;
import br.alkazuz.terrenos.object.Terreno;
import br.alkazuz.terrenos.utils.TerrenoManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInTerrainListener implements Listener {

    @EventHandler(priority = org.bukkit.event.EventPriority.HIGHEST)
    public void onBreak(org.bukkit.event.block.BlockBreakEvent e) {
        if (e.getPlayer().hasPermission("terrenos.admin")) return;
        if (!e.getBlock().getWorld().getName().equalsIgnoreCase(Settings.TERRAIN_WORLD)) return;
        if (e.isCancelled()) return;
        Terreno terreno = TerrenoManager.getTerrenoInLocation(e.getBlock().getLocation());
        if (terreno == null) {
            e.setCancelled(true);
            return;
        }
        if (terreno.getOwner().equalsIgnoreCase(e.getPlayer().getName())) return;

        PlayerTerreno playerTerreno = PlayerTerrenoManager.getPlayerTerrenoOrDefault(e.getPlayer(), terreno);
        if (!playerTerreno.canBreakBlock()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPreCommand(PlayerCommandPreprocessEvent event) {
        if (event.getPlayer().hasPermission("terrenos.admin")) return;
        if (!event.getPlayer().getWorld().getName().equalsIgnoreCase(Settings.TERRAIN_WORLD)) return;
        if (event.isCancelled()) return;
        Terreno terreno = TerrenoManager.getTerrenoInLocation(event.getPlayer().getLocation());
        if (terreno == null || terreno.deleting) {
            return;
        }
        String msg = event.getMessage().toLowerCase();

        if (terreno.getOwner().equalsIgnoreCase(event.getPlayer().getName())) return;

        PlayerTerreno playerTerreno = PlayerTerrenoManager.getPlayerTerrenoOrDefault(event.getPlayer(), terreno);
        if (playerTerreno.canUseCommands()) return;

        if (!terreno.canSetHome() && msg.startsWith("/sethome")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cVocê não pode usar esse comando nesse terreno.");

        } else if (!terreno.canTpAccept() && msg.startsWith("/tpaccept")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cVocê não pode usar esse comando nesse terreno.");
        }
    }

    @EventHandler(priority = org.bukkit.event.EventPriority.HIGHEST)
    public void onPlace(org.bukkit.event.block.BlockPlaceEvent e) {
        if (e.getPlayer().hasPermission("terrenos.admin")) return;
        if (!e.getBlock().getWorld().getName().equalsIgnoreCase(Settings.TERRAIN_WORLD)) return;
        if (e.isCancelled()) return;
        Terreno terreno = TerrenoManager.getTerrenoInLocation(e.getBlock().getLocation());
        if (terreno == null || terreno.deleting) {
            e.setCancelled(true);
            return;
        }

        if (terreno.getOwner().equalsIgnoreCase(e.getPlayer().getName())) return;

        PlayerTerreno playerTerreno = PlayerTerrenoManager.getPlayerTerrenoOrDefault(e.getPlayer(), terreno);

        if (!playerTerreno.canPlaceBlocks()) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPvp(EntityDamageByEntityEvent event) {
        if (!event.getEntity().getWorld().getName().equalsIgnoreCase(Settings.TERRAIN_WORLD)) return;
        if (event.isCancelled()) return;
        if (event.getDamager() == null) return;
        if (event.getEntity() == null) return;
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof Player)) return;
        Terreno terreno = TerrenoManager.getTerrenoInLocation(event.getEntity().getLocation());
        if (terreno == null) return;
        if (terreno.isPvp24()) {
            event.setCancelled(false);
            return;
        }
        if (terreno.isPvp()) return;

        event.setCancelled(true);
    }

    @EventHandler(priority = org.bukkit.event.EventPriority.HIGHEST)
    public void onInteractChest(PlayerInteractEvent event) {
        if (event.getPlayer().hasPermission("terrenos.admin")) return;
        if (event.getClickedBlock() == null) return;
        if (!event.getClickedBlock().getWorld().getName().equalsIgnoreCase(Settings.TERRAIN_WORLD)) return;
        if (event.isCancelled()) return;
        Terreno terreno = TerrenoManager.getTerrenoInLocation(event.getClickedBlock().getLocation());
        if (terreno == null) {
            event.setCancelled(true);
            return;
        }
        if (terreno.getOwner().equalsIgnoreCase(event.getPlayer().getName())) return;

        PlayerTerreno playerTerreno = PlayerTerrenoManager.getPlayerTerrenoOrDefault(event.getPlayer(), terreno);
        if (!playerTerreno.canOpenChest()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = org.bukkit.event.EventPriority.HIGHEST)
    public void onExplode(EntityExplodeEvent event) {
        if (!event.getLocation().getWorld().getName().equalsIgnoreCase(Settings.TERRAIN_WORLD)) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = org.bukkit.event.EventPriority.HIGHEST)
    public void onMobGrief(EntityChangeBlockEvent e) {
        if (!e.getBlock().getWorld().getName().equalsIgnoreCase(Settings.TERRAIN_WORLD)) return;
        e.setCancelled(true);
    }
}
