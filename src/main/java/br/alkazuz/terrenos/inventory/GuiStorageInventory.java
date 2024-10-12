package br.alkazuz.terrenos.inventory;

import br.alkazuz.terrenos.Main;
import br.alkazuz.terrenos.config.Settings;
import br.alkazuz.terrenos.object.Terreno;
import br.alkazuz.terrenos.utils.GuiHolder;
import br.alkazuz.terrenos.utils.ItemBuilder;
import br.alkazuz.terrenos.utils.ItemName;
import br.alkazuz.terrenos.utils.NumberUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class GuiStorageInventory implements Listener {
    private static final int INVENTORY_ID = 9124;

    public static void open(Player player, Terreno terreno) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("terreno", terreno);
        GuiHolder holder = new GuiHolder(INVENTORY_ID, properties);
        Inventory inv = Main.getInstance().getServer().createInventory(holder, 27, "Armazém do terreno");

        updateInventory(inv, terreno);

        player.openInventory(inv);
    }

    private static void updateInventory(Inventory inv, Terreno terreno) {
        inv.clear();
        for (Map.Entry<Material, Integer> entry : terreno.getStorage().entrySet()) {
            inv.addItem(new ItemBuilder(entry.getKey())
                    .addLore("§7",
                            "§7Quantidade: §f" + NumberUtils.formatWithSuffix(entry.getValue()),
                            "§7",
                            "§eBotão esquerdo para vender TUDO.",
                            "§eBotão direito para pegar x64.")
                    .toItemStack());
        }
    }

    private void openConfirmSell(Player player, Terreno terreno, Material material) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("terreno", terreno);
        properties.put("material", material);
        GuiHolder holder = new GuiHolder(9125, properties);
        Inventory inv = Main.getInstance().getServer().createInventory(holder, 9, "Confirmar venda");

        inv.setItem(2, new ItemBuilder(material)
                .addLore("§7",
                        "§7Quantidade: §f" + NumberUtils.formatWithSuffix(terreno.getStorage().get(material)))
                .toItemStack());

        inv.setItem(4, new ItemBuilder(Material.EMERALD_BLOCK)
                .setName("§aConfirmar venda")
                .addLore("§7",
                        "§7Preço unitário: §f" + Settings.MATERIAL_PRICES.get(material),
                        "§7Preço total: §f" + NumberUtils.formatWithSuffix(Settings.MATERIAL_PRICES.get(material)
                                * terreno.getStorage().get(material)))
                .toItemStack());

        inv.setItem(6, new ItemBuilder(Material.REDSTONE_BLOCK)
                .setName("§cCancelar venda")
                .addLore("§7Click para cancelar a venda.")
                .toItemStack());

        player.openInventory(inv);
    }

    @EventHandler
    public void onClickConfirm(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        if (!(event.getInventory().getHolder() instanceof GuiHolder)) {
            return;
        }

        GuiHolder holder = (GuiHolder) event.getInventory().getHolder();

        if (holder.getId() != 9125) {
            return;
        }

        event.setCancelled(true);

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
            return;
        }

        Terreno terreno = (Terreno) holder.getProperty("terreno");
        Material material = (Material) holder.getProperty("material");

        if (event.getSlot() == 4) {
            int amount = terreno.getStorage().get(material);
            double price = Settings.MATERIAL_PRICES.get(material);
            double total = amount * price;

            Economy economy = Main.getInstance().getEconomy();

            economy.depositPlayer(player.getName(), total);

            terreno.removeStorage(material, amount);
            terreno.queueSaveStorage();
            player.sendMessage("§aVocê vendeu " + NumberUtils.formatWithSuffix(amount) + " " + ItemName.valueOf(material).getName() + " por " + NumberUtils.formatWithSuffix(total));
            player.closeInventory();
        } else if (event.getSlot() == 6) {
            open(player, terreno);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
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

        Material material = event.getCurrentItem().getType();
        Terreno terreno = (Terreno) holder.getProperty("terreno");

        if (event.isLeftClick()) {
            openConfirmSell(player, terreno, material);
        } else if (event.isRightClick()) {
            if (terreno.getStorage().getOrDefault(material, 0) <= 0) {
                return;
            }
            int amount = 64;
            if (terreno.getStorage().get(material) < 64) {
                amount = terreno.getStorage().get(material);
            }
            int firstEmpty = player.getInventory().firstEmpty();
            if (firstEmpty == -1) {
                player.sendMessage("§cSeu inventário está cheio.");
                return;
            }
            player.getInventory().addItem(new ItemStack(material, amount));
            terreno.removeStorage(material, amount);
            terreno.queueSaveStorage();
            updateInventory(event.getInventory(), terreno);
        }

    }

}
