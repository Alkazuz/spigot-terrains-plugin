package br.alkazuz.terrenos.inventory.listen;

import br.alkazuz.terrenos.Main;
import br.alkazuz.terrenos.config.Settings;
import br.alkazuz.terrenos.inventory.GuiInventory;
import br.alkazuz.terrenos.inventory.GuiTerrenosInventory;
import br.alkazuz.terrenos.object.Terreno;
import br.alkazuz.terrenos.utils.EntityName;
import br.alkazuz.terrenos.utils.GuiHolder;
import br.alkazuz.terrenos.utils.RandomLocationFinder;
import br.alkazuz.terrenos.utils.TerrenoManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryClickListenner implements Listener {
    @EventHandler
    public void onClickShop(final InventoryClickEvent e) {
        if (e.getInventory().getHolder() instanceof GuiHolder) {
            Player p = (Player) e.getWhoClicked();
            GuiHolder holder = (GuiHolder) e.getInventory().getHolder();
            int guiID = holder.getId();
            if (guiID == 9249) {
                e.setCancelled(true);
                ItemStack item = e.getCurrentItem();
                if (item == null) return;
                if (item.getItemMeta() == null) return;
                if (item.getItemMeta().getDisplayName() == null) return;
                int itemSize = Integer.valueOf(ChatColor.stripColor(item.getItemMeta().getDisplayName()).split("x")[1]);
                Settings.Size size = Settings.SIZES.stream().filter(s -> itemSize == s.getSize()).findFirst().orElse(null);
                if (size == null) return;
                GuiInventory.openConfirmShop(p, size);
            }
        }
    }

    private Long lastFindEmptyTime = 0L;

    @EventHandler
    public void onClickShopConfirm(final InventoryClickEvent e) {
        if (e.getInventory().getHolder() instanceof GuiHolder) {
            Player p = (Player) e.getWhoClicked();
            GuiHolder holder = (GuiHolder) e.getInventory().getHolder();
            int guiID = holder.getId();
            if (guiID == 9247) {
                Settings.Size size = Settings.SIZES.stream().filter(s -> (int) holder.getProperty("size") == s.getSize()).findFirst().orElse(null);
                e.setCancelled(true);
                if (size == null) return;
                if (e.getSlot() == 6) {
                    if (System.currentTimeMillis() - lastFindEmptyTime < 5000) {
                        p.sendMessage("§cUm terreno já foi criado recentemente por outro jogador, aguarde um pouco.");
                        return;
                    }

                    lastFindEmptyTime = System.currentTimeMillis();

                    p.closeInventory();

                    p.sendMessage("§eProcurando um terreno livre...");

                    Location randomLoc = RandomLocationFinder.getRandomLocation(size.getSize(), p.getLocation());

                    Bukkit.getScheduler().scheduleSyncDelayedTask(
                            Main.getInstance(),
                            () -> {
                                if (randomLoc == null) {
                                    p.sendMessage("§cNão foi possível encontrar um terreno livre.");
                                    return;
                                }
                                try {
                                    if (TerrenoManager.createTerreno(p, randomLoc, size, false)) {
                                        p.sendMessage("§aTerreno criado com sucesso.");
                                        p.teleport(randomLoc);
                                    } else {
                                        p.sendMessage("§cNão foi possível criar o terreno.");
                                    }
                                } catch (Exception ex) {
                                    p.sendMessage("§c" + ex.getMessage());
                                }

                                long time = System.currentTimeMillis() - lastFindEmptyTime;
                                Main.debug("InventoryClickListenner.onClickShopConfirm randomLoc took " + time + "ms");
                            }
                    );
                } else if (e.getSlot() == 2) {
                    Location location = p.getLocation();
                    p.closeInventory();
                    try {
                        if (TerrenoManager.createTerreno(p, location, size, true)) {
                            p.sendMessage("§aTerreno criado com sucesso.");
                        } else {
                            p.sendMessage("§cNão foi possível criar o terreno.");
                        }
                    } catch (Exception ex) {
                        p.sendMessage("§c" + ex.getMessage());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onClickInventoryMain(final InventoryClickEvent e) {
        if (e.getInventory().getHolder() instanceof GuiHolder) {
            Player p = (Player) e.getWhoClicked();
            GuiHolder holder = (GuiHolder) e.getInventory().getHolder();
            int guiID = holder.getId();
            if (guiID == 9248) {
                e.setCancelled(true);
                if (e.getSlot() == 3) {
                    p.chat("/terreno info");
                    p.closeInventory();
                } else if (e.getSlot() == 4) {
                    GuiInventory.openShop(p);
                } else if (e.getSlot() == 5) {
                    GuiTerrenosInventory.open(p);
                }
            }
        }
    }

    @EventHandler
    public void onClickInventorySetSpawn(final InventoryClickEvent e) {
        if (e.getInventory().getHolder() instanceof GuiHolder) {
            Player p = (Player) e.getWhoClicked();
            GuiHolder holder = (GuiHolder) e.getInventory().getHolder();
            int guiID = holder.getId();
            if (guiID == 9250) {
                e.setCancelled(true);

                Terreno terreno = (Terreno) holder.getProperty("terreno");

                if (e.getCurrentItem() == null) return;
                if (e.getCurrentItem().getItemMeta() == null) return;

                if (e.getCurrentItem().getType() != Material.MONSTER_EGG) return;

                EntityType entityType = EntityType.fromId(e.getCurrentItem().getDurability());
                if (entityType == null) return;

                if (terreno.getSpawns().containsKey(entityType)) {
                    terreno.getSpawns().remove(entityType);
                    p.sendMessage("§aSpawn removido com sucesso.");
                } else {
                    terreno.getSpawns().put(entityType, p.getLocation());
                    terreno.save();

                    p.sendMessage("§aSpawn de " + EntityName.valueOf(entityType).getName() + " definido com sucesso.");
                }

                GuiInventory.updateSpawnSet(e.getInventory(), terreno);
            }
        }
    }

}
