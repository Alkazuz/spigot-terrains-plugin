package br.alkazuz.terrenos.inventory;

import br.alkazuz.terrenos.Main;
import br.alkazuz.terrenos.object.Terreno;
import br.alkazuz.terrenos.utils.GuiHolder;
import br.alkazuz.terrenos.utils.ItemBuilder;
import br.alkazuz.terrenos.utils.NumberUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public class GuiStorageInventory implements Listener {
    private static final int INVENTORY_ID = 9124;

    public static void open(Player player, Terreno terreno) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("terreno", terreno);
        GuiHolder holder = new GuiHolder(INVENTORY_ID, properties);
        Inventory inv = Main.getInstance().getServer().createInventory(holder, 27, "Armazén do terreno");

        updateInventory(inv, terreno);

        player.openInventory(inv);
    }

    private static void updateInventory(Inventory inv, Terreno terreno) {
        for (Map.Entry<Material, Integer> entry : terreno.getStorage().entrySet()) {
            inv.addItem(new ItemBuilder(entry.getKey())
                    .addLore("§7",
                            "§7Quantidade: §f" + NumberUtils.formatWithSuffix(entry.getValue()),
                            "§7",
                            "§eClique com o botão direito para",
                            "§evender tudo.")
                    .toItemStack());
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


    }

}
