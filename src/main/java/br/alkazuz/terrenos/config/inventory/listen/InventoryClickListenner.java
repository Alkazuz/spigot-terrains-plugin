package br.alkazuz.terrenos.config.inventory.listen;

import br.alkazuz.terrenos.config.Settings;
import br.alkazuz.terrenos.config.inventory.GuiInventory;
import br.alkazuz.terrenos.utils.GuiHolder;
import br.alkazuz.terrenos.utils.TerrenoManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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
                Settings.Size size = Settings.SIZES.stream().filter(s -> (int)holder.getProperty("size") == s.getSize()).findFirst().orElse(null);
                e.setCancelled(true);
                if (size == null) return;
                if (e.getSlot() == 6) {
                    if (System.currentTimeMillis() - lastFindEmptyTime < 5000) {
                        p.sendMessage("§cUm terreno já foi criado recentemente por outro jogador, aguarde um pouco.");
                        return;
                    }

                    lastFindEmptyTime = System.currentTimeMillis();

                    Location randomLoc = TerrenoManager.findRandomEmptyRegionLocation(p.getWorld(), size);

                    p.closeInventory();
                    if (randomLoc == null) {
                        p.sendMessage("§cNão foi possível encontrar um terreno livre.");
                        return;
                    }
                    try {
                        if (TerrenoManager.createTerreno(p, randomLoc, size)) {
                            p.sendMessage("§aTerreno criado com sucesso.");
                        } else {
                            p.sendMessage("§cNão foi possível criar o terreno.");
                        }
                    } catch (Exception ex) {
                        p.sendMessage("§c" + ex.getMessage());
                    }
                } else if (e.getSlot() == 2) {
                    Location location = p.getLocation();
                    p.closeInventory();
                    try {
                        if (TerrenoManager.createTerreno(p, location, size)) {
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

                } else if (e.getSlot() == 4) {
                    GuiInventory.openShop(p);
                } else if (e.getSlot() == 5) {
                    // GuiInventory.openTeleport(p);
                }
            }
        }
    }

}
