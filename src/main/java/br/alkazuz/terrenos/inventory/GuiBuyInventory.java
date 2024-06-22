package br.alkazuz.terrenos.inventory;

import br.alkazuz.terrenos.Main;
import br.alkazuz.terrenos.config.Settings;
import br.alkazuz.terrenos.object.Terreno;
import br.alkazuz.terrenos.utils.*;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.text.NumberFormat;
import java.util.HashMap;

public class GuiBuyInventory implements Listener {
    private static final int INVENTORY_ID = 9100;
    public static void openConfirmShop(Player player, Terreno terreno, double price, Sign sign) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("terreno", terreno);
        properties.put("owner", terreno.getOwner());
        properties.put("sign", sign);
        properties.put("price", price);
        GuiHolder holder = new GuiHolder(INVENTORY_ID, properties);
        final Inventory inv = Main.getInstance().getServer().createInventory(holder, 45, "Confirmar compra");
        ItemBuilder item = new ItemBuilder(Material.GRASS)
                .setName("§aTerreno")
                .addLore(
                        "", "§7Localização: §fX: " + terreno.getCenter().getBlockX() + ", Y: " + terreno.getCenter().getBlockZ(),
                            "§7Dono: §f" + terreno.getOwner(),
                            "§7Preço: §f" + NumberFormatUtil.format(price));

        inv.setItem(13, item.toItemStack());
        inv.setItem(20, new ItemBuilder(Material.WOOL, 1, (short) 5).setName("§aConfirmar compra").toItemStack());
        inv.setItem(24, new ItemBuilder(Material.WOOL, 1, (short) 14).setName("§cCancelar compra").toItemStack());

        player.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof GuiHolder)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        if (!(event.getInventory().getHolder() instanceof GuiHolder)) {
            return;
        }

        GuiHolder holder = (GuiHolder) event.getInventory().getHolder();
        if (holder.getId() != INVENTORY_ID) {
            return;
        }

        event.setCancelled(true);

        Sign sign = (Sign) holder.getProperty("sign");
        Terreno terreno = (Terreno) holder.getProperty("terreno");
        double price = (double) holder.getProperty("price");

        if (event.getSlot() == 20) {
            try {
                Block block = sign.getBlock();
                if (block.getType() != Material.WALL_SIGN && block.getType() != Material.SIGN_POST) {
                    player.sendMessage("§cA placa de compra foi removida.");
                    return;
                }
                String owner = terreno.getOwner();
                TerrenoManager.buyRegion(player, terreno, price);
                player.sendMessage("§aVocê comprou o terreno de §f" + PlayerUtils.getPlayerPrefix(owner) + owner + "§a por §f" + NumberFormatUtil.format(price) + "§a.");
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
                sign.getBlock().setType(Material.AIR);
            } catch (Exception e) {
                player.sendMessage("§c" + e.getMessage());
                return;
            }
            player.closeInventory();
        } else if (event.getSlot() == 24) {
            player.sendMessage("§cVocê cancelou a compra do terreno.");
            player.closeInventory();
        }
    }

}
