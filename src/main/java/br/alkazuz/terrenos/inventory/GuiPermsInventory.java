package br.alkazuz.terrenos.inventory;

import br.alkazuz.terrenos.Main;
import br.alkazuz.terrenos.object.PlayerTerreno;
import br.alkazuz.terrenos.object.PlayerTerrenoManager;
import br.alkazuz.terrenos.object.Terreno;
import br.alkazuz.terrenos.perms.EPermissions;
import br.alkazuz.terrenos.utils.GuiHolder;
import br.alkazuz.terrenos.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class GuiPermsInventory implements Listener {
    private static final int[] SLOTS = new int[] { 10, 11, 12, 13, 14, 15, 16, 28, 29, 30, 31, 32, 33, 34};
    public static void openInventory(Player player, Terreno terreno, PlayerTerreno playerTerreno) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("terreno", terreno);
        properties.put("playerTerreno", playerTerreno);
        GuiHolder holder = new GuiHolder(9111, properties);

        Inventory inv = Main.getInstance().getServer().createInventory(holder, 54, "Terreno - Permissões");

        updateInventorySlots(inv, terreno, playerTerreno);

        player.openInventory(inv);
    }

    private static void updateInventorySlots(Inventory inventory, Terreno terreno, PlayerTerreno playerTerreno) {

        int index = 0;
        for (EPermissions perm : EPermissions.values()) {
            ItemBuilder item = new ItemBuilder(perm.getItem());
            item.setName("§a" + perm.getName());
            item.addLore("", "§7" + perm.getDescription());

            boolean isEnabled = perm.isEnabled(playerTerreno);

            ItemBuilder item2 = new ItemBuilder(Material.WOOL, 1, (short) (isEnabled ? 5 : 14));
            item2.setName(isEnabled ? "§aAtivado" : "§cDesativado");

            inventory.setItem(SLOTS[index], item.toItemStack());
            inventory.setItem(SLOTS[index] + 9, item2.toItemStack());
            index++;
        }
    }

    @EventHandler
    public void onClickInventory(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Inventory inventory = event.getInventory();
        if (inventory.getHolder() instanceof GuiHolder) {
            GuiHolder holder = (GuiHolder) inventory.getHolder();
            if (holder.getId() == 9111) {
                event.setCancelled(true);
                ItemStack item = event.getCurrentItem();
                if (item == null || item.getType() == Material.AIR) {
                    return;
                }
                Terreno terreno = (Terreno) holder.getProperty("terreno");
                PlayerTerreno playerTerreno = (PlayerTerreno) holder.getProperty("playerTerreno");

                if (event.getRawSlot() >= 0 && event.getRawSlot() < inventory.getSize()) {
                    int slot = event.getRawSlot();
                    if (slot >= 0 && slot < inventory.getSize() && item.getType() == Material.WOOL) {
                        ItemStack itemStack = inventory.getItem(slot - 9);
                        EPermissions perm = EPermissions.getByName(ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()));
                        playerTerreno.togglePermission(perm);
                        updateInventorySlots(inventory, terreno, playerTerreno);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onCloseInventory(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        Inventory inventory = event.getInventory();
        if (inventory.getHolder() instanceof GuiHolder) {
            GuiHolder holder = (GuiHolder) inventory.getHolder();
            if (holder.getId() == 9111) {
                Player player = (Player) event.getPlayer();
                PlayerTerreno playerTerreno = (PlayerTerreno) holder.getProperty("playerTerreno");
                playerTerreno.save();
                PlayerTerrenoManager.savePlayerTerreno(playerTerreno);
                player.sendMessage("§aPermissões salvas com sucesso.");
            }
        }
    }
}
