package br.alkazuz.terrenos.listeners;

import br.alkazuz.terrenos.config.Settings;
import br.alkazuz.terrenos.object.PlayerTerreno;
import br.alkazuz.terrenos.object.PlayerTerrenoManager;
import br.alkazuz.terrenos.object.Terreno;
import br.alkazuz.terrenos.utils.TerrenoManager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.*;
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

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onProjectLaunch(ProjectileLaunchEvent e) {
        if (!e.getEntity().getWorld().getName().equalsIgnoreCase(Settings.TERRAIN_WORLD)) return;
        if (e.getEntityType() == EntityType.SMALL_FIREBALL && e
                .getEntity().getShooter() != null)
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onUsePotion(PotionSplashEvent event) {
        if (event.getEntity().getWorld().getName().equals(Settings.TERRAIN_WORLD))
            event.setCancelled(true);
    }

    @EventHandler
    public void entityDamageByEntityHookByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof org.bukkit.craftbukkit.v1_5_R3.entity.CraftFish) {
            event.setCancelled(true);
        } else if (event.getDamager().getWorld().getName().equals(Settings.TERRAIN_WORLD) &&
                event.getDamager() instanceof org.bukkit.entity.Arrow) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getPlayer().hasPermission("terrenos.admin")) return;
        if (!event.getPlayer().getWorld().getName().equalsIgnoreCase(Settings.TERRAIN_WORLD)) return;
        if (event.isCancelled()) return;
        Terreno terreno = TerrenoManager.getTerrenoInLocation(event.getPlayer().getLocation());
        if (terreno == null || terreno.deleting) {
            return;
        }
        if (terreno.getOwner().equalsIgnoreCase(event.getPlayer().getName())) return;

        PlayerTerreno playerTerreno = PlayerTerrenoManager.getPlayerTerrenoOrDefault(event.getPlayer(), terreno);
        if (!playerTerreno.canBreakBlock()) {
            event.setCancelled(true);
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

    @EventHandler
    public void onBlockFlow(BlockFromToEvent event) {
        if (!event.getBlock().getWorld().getName().equalsIgnoreCase(Settings.TERRAIN_WORLD)) return;
        Terreno terreno = TerrenoManager.getTerrenoInLocation(event.getBlock().getLocation());
        if (terreno == null) {
            event.setCancelled(true);
            return;
        }
        boolean outSide = terreno.isOutSide(event.getToBlock().getLocation());
        if (outSide) {
            event.setCancelled(true);
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
        if (event.getClickedBlock().getType().toString().contains("SIGN")) return;
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
