package br.alkazuz.terrenos.inventory;

import br.alkazuz.terrenos.Main;
import br.alkazuz.terrenos.TerrenoFlags;
import br.alkazuz.terrenos.economy.DepositBank;
import br.alkazuz.terrenos.object.Terreno;
import br.alkazuz.terrenos.utils.GuiHolder;
import br.alkazuz.terrenos.utils.ItemBuilder;
import br.alkazuz.terrenos.utils.NumberFormatUtil;
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

public class GuiFlagsInventory implements Listener {
    private static final int[] SLOTS = new int[]{10, 11, 12, 13, 14, 15, 16, 28, 29, 30, 31, 32, 33, 34};

    public static void openInventory(Player player, Terreno terreno) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("terreno", terreno);
        GuiHolder holder = new GuiHolder(9110, properties);

        Inventory inv = Main.getInstance().getServer().createInventory(holder, 54, "Terreno - Flags");

        updateInventorySlots(inv, terreno);

        player.openInventory(inv);
    }

    private static void updateInventorySlots(Inventory inventory, Terreno terreno) {

        int index = 0;
        for (TerrenoFlags flag : TerrenoFlags.values()) {
            ItemBuilder item = new ItemBuilder(flag.getIcon());
            item.setName("§a" + flag.getName());
            item.addLore("", "§7" + flag.getDescription());

            boolean isEnabled = terreno.getFlagBooleanOrDefault(flag.getFlag(), flag.getDefaultValue());

            ItemBuilder item2 = new ItemBuilder(Material.WOOL, 1, (short) (isEnabled ? 5 : 14));
            item2.setName(isEnabled ? "§aAtivado" : "§cDesativado");
            item2.addLore("", "§eClique para ativar/desativar.", "§ePreço: " + NumberFormatUtil.format(flag.getPriceToggler()));

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
        Player pl = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();
        if (inventory.getHolder() instanceof GuiHolder) {
            GuiHolder holder = (GuiHolder) inventory.getHolder();
            if (holder.getId() == 9110) {
                event.setCancelled(true);
                ItemStack item = event.getCurrentItem();
                if (item == null || item.getType() == Material.AIR) {
                    return;
                }
                Terreno terreno = (Terreno) holder.getProperty("terreno");

                if (event.getRawSlot() >= 0 && event.getRawSlot() < inventory.getSize()) {
                    int slot = event.getRawSlot();
                    if (slot >= 0 && slot < inventory.getSize() && item.getType() == Material.WOOL) {
                        ItemStack itemStack = inventory.getItem(slot - 9);
                        TerrenoFlags terrenoFlags = TerrenoFlags.getByName(ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()));

                        if (terrenoFlags == null) {
                            return;
                        }

                        double price = terrenoFlags.getPriceToggler();
                        double playerMoney = Main.getInstance().getEconomy().getBalance(pl.getName());

                        if (playerMoney < price) {
                            pl.sendMessage("§cVocê não tem dinheiro suficiente para ativar/desativar essa flag.");
                            return;
                        }

                        if (Main.getInstance().getEconomy().withdrawPlayer(pl.getName(), price).transactionSuccess()) {
                            boolean value = terreno.getFlagBooleanOrDefault(terrenoFlags.getFlag(), terrenoFlags.getDefaultValue());
                            terreno.setFlag(terrenoFlags.getFlag(), !value);
                            updateInventorySlots(inventory, terreno);
                            DepositBank.depositBank(price);
                        } else {
                            pl.sendMessage("§cOcorreu um erro ao tentar ativar/desativar essa flag.");
                        }
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
        Player pl = (Player) event.getPlayer();
        Inventory inventory = event.getInventory();
        if (inventory.getHolder() instanceof GuiHolder) {
            GuiHolder holder = (GuiHolder) inventory.getHolder();
            if (holder.getId() == 9110) {
                Terreno terreno = (Terreno) holder.getProperty("terreno");
                terreno.saveFlags();
                pl.sendMessage("§aFlags do terreno foram atualizadas com sucesso.");
            }
        }
    }
}
