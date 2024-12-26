package br.alkazuz.terrenos.listeners;

import br.alkazuz.terrenos.config.Settings;
import br.alkazuz.terrenos.object.Terreno;
import br.alkazuz.terrenos.utils.TerrenoManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class TerrenoSpawnerListener implements Listener {
    private HashMap<String, Long> cooldown = new HashMap<>();
    private HashMap<String, Long> cooldownPlaceSpawners = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onUseEgg2(PlayerInteractEvent e) {
        if (e.getPlayer().getWorld().getName().equals(Settings.TERRAIN_WORLD)) return;
        if (e.getItem() == null || e.getItem().getType() != Material.MONSTER_EGG) return;
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlaceMobSpawner(BlockPlaceEvent e) {
        if (!e.getPlayer().getWorld().getName().equals(Settings.TERRAIN_WORLD)) return;
        if (e.getBlock().getType() != Material.MOB_SPAWNER) return;
        if (cooldownPlaceSpawners.containsKey(e.getPlayer().getName()) && cooldownPlaceSpawners.get(e.getPlayer().getName()) > System.currentTimeMillis()) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§cAguarde um pouco para fazer isso novamente!");
            return;
        }
        cooldownPlaceSpawners.put(e.getPlayer().getName(), System.currentTimeMillis() + 1000);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onSpawn(CreatureSpawnEvent e) {
        if (!e.getLocation().getWorld().getName().equals(Settings.TERRAIN_WORLD)) return;
        CreatureSpawnEvent.SpawnReason reason = e.getSpawnReason();
        if (reason != CreatureSpawnEvent.SpawnReason.SPAWNER) return;

        Terreno terreno = TerrenoManager.getTerrenoInLocation(e.getLocation());
        if (terreno == null) {
            return;
        }

        EntityType entityType = e.getEntityType();
        if (!terreno.getSpawns().containsKey(entityType)) return;

        Location location = terreno.getSpawns().get(entityType);

        e.getEntity().teleport(location.clone().add(0.5, 0.5, 0.5));
    }

    @EventHandler
    public void onSpawnNatural(CreatureSpawnEvent event) {
        if (!event.getLocation().getWorld().getName().equals(Settings.TERRAIN_WORLD)) return;
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER ||
                event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) return;
        event.setCancelled(true);
        event.getEntity().remove();
    }

    @EventHandler
    public void onUseEgg(PlayerInteractEvent e) {
        if (e.getPlayer().getGameMode() == org.bukkit.GameMode.CREATIVE) return;
        ItemStack item = e.getPlayer().getItemInHand();
        if (item == null || item.getType() != Material.MONSTER_EGG) return;
        if (e.getClickedBlock() == null) {
            e.setCancelled(true);
            return;
        }
        if (e.getClickedBlock().getType() != Material.MOB_SPAWNER) {
            e.setCancelled(true);
            return;
        }

        if (!e.getPlayer().getWorld().getName().equals(Settings.TERRAIN_WORLD)) return;

        if (cooldown.containsKey(e.getPlayer().getName()) && cooldown.get(e.getPlayer().getName()) > System.currentTimeMillis()) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§cAguarde um pouco para fazer isso novamente!");
            return;
        }

        Terreno terreno = TerrenoManager.getTerrenoInLocation(e.getClickedBlock().getLocation());

        if (terreno == null && !e.getPlayer().hasPermission("terrenos.admin")) {
            e.setCancelled(true);
            return;
        }

        if (!terreno.getOwner().equalsIgnoreCase(e.getPlayer().getName()) && !e.getPlayer().hasPermission("terrenos.admin")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§cSomente o dono do terreno pode fazer isso!");
            return;
        }

        BlockFace blockFace = getBlockFace(e.getPlayer());
        if (blockFace != BlockFace.UP) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§cVocê só pode colocar spawners em cima do bloco!");
            return;
        }

        EntityType entityType = EntityType.fromId(item.getDurability());

        CreatureSpawner mobSpawner = (CreatureSpawner) e.getClickedBlock().getState();

        if (entityType == null) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§cEste ovo não é válido!");
            return;
        }

        if (mobSpawner.getSpawnedType() == entityType) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§cEste spawner já é deste tipo!");
            return;
        }

        mobSpawner.setSpawnedType(entityType);
        mobSpawner.update();
        updateItemOrRemove(e.getPlayer(), item);
        cooldown.put(e.getPlayer().getName(), System.currentTimeMillis() + 1000);

    }

    public void updateItemOrRemove(Player player, ItemStack item) {
        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            player.getInventory().removeItem(item);
        }
    }

    public BlockFace getBlockFace(Player player) {
        List<Block> lastTwoTargetBlocks = player.getLastTwoTargetBlocks(null, 100);
        if (lastTwoTargetBlocks.size() != 2 || !lastTwoTargetBlocks.get(1).getType().isOccluding()) return null;
        Block targetBlock = lastTwoTargetBlocks.get(1);
        Block adjacentBlock = lastTwoTargetBlocks.get(0);
        return targetBlock.getFace(adjacentBlock);
    }
}
