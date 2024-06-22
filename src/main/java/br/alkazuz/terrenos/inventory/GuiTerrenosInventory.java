package br.alkazuz.terrenos.inventory;

import br.alkazuz.terrenos.Main;
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
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class GuiTerrenosInventory implements Listener {
    private static final int INVENTORY_ID = 9101;
    private static final int[] slots = new int[] {
            10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43 };
    public static void open(Player player) {
        HashMap<String, Object> properties = new HashMap<>();
        GuiHolder holder = new GuiHolder(INVENTORY_ID, properties);
        final Inventory inv = Main.getInstance().getServer().createInventory(holder, 54, "Seus terrenos");

        int index = 0;

        for (Terreno terreno : TerrenoManager.getTerrenosOfPlayer(player)) {
            if (index >= slots.length) {
                break;
            }
            ItemBuilder item = new ItemBuilder(Material.GRASS)
                    .setName("§aTerreno #" + terreno.getId())
                    .addLore(
                            "", "§7Localização: §fX: " + terreno.getCenter().getBlockX() + ", Y: " + terreno.getCenter().getBlockZ(),
                            "§aClique para teletransportar-se até o terreno.");
            inv.setItem(slots[index], item.toItemStack());
            index++;
        }

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

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
            return;
        }

        ItemStack item = event.getCurrentItem();
        Terreno terreno = TerrenoManager.getTerrenoById(Integer.parseInt(item.getItemMeta().getDisplayName().replace("§aTerreno #", "")));

        if (terreno == null) {
            player.sendMessage("§cOcorreu um erro ao tentar encontrar o terreno.");
            player.closeInventory();
            return;
        }

        player.closeInventory();
        player.teleport(terreno.getCenter().add(0.5, 1, 0.5), PlayerTeleportEvent.TeleportCause.PLUGIN);
        player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
    }

}
