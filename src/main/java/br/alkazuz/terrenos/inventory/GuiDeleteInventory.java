package br.alkazuz.terrenos.inventory;

import br.alkazuz.terrenos.Main;
import br.alkazuz.terrenos.inventory.listen.InventoryClickListenner;
import br.alkazuz.terrenos.object.Terreno;
import br.alkazuz.terrenos.utils.GuiHolder;
import br.alkazuz.terrenos.utils.ItemBuilder;
import br.alkazuz.terrenos.workload.ClearTerrenoWorkload;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public class GuiDeleteInventory implements Listener {
    private static final int INVENTORY_ID = 9122;
    public static void open(Player player, Terreno terreno) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("terreno", terreno);
        GuiHolder holder = new GuiHolder(INVENTORY_ID, properties);
        Inventory inv = Main.getInstance().getServer().createInventory(holder, 27, "Deletar terreno");

        inv.setItem(11, new ItemBuilder(Material.WOOL, 1, (short) 5).setName("§aConfirmar")
                .addLore("", "§7Clique para confirmar a exclusão.",
                         "§c§lSEU TERRENO SERÁ LIMPO E",
                          "§c§lNÃO PODERÁ SER RECUPERADO!").toItemStack());
        inv.setItem(15, new ItemBuilder(Material.WOOL, 1, (short) 14).setName("§cCancelar")
                .addLore("", "§7Clique para cancelar a exclusão.").toItemStack());

        player.openInventory(inv);
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

        if (event.getSlot() == 11) {
            Terreno terreno = (Terreno) holder.getProperty("terreno");
            ClearTerrenoWorkload workload = new ClearTerrenoWorkload(terreno, null);
            BukkitTask task = Bukkit.getServer().getScheduler().runTaskTimer(Main.getInstance(), workload, 1L, 1L);
            workload.setTask(task);
            player.sendMessage("§eSeu terreno está sendo limpo e será excluído em breve.");
            player.closeInventory();
        } else if (event.getSlot() == 15) {
            player.closeInventory();
            player.sendMessage("§cExclusão de terreno cancelada.");
        }
    }

}
