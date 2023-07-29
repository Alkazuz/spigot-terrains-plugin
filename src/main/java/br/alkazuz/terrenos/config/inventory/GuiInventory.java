package br.alkazuz.terrenos.config.inventory;

import br.alkazuz.terrenos.Main;
import br.alkazuz.terrenos.config.Settings;
import br.alkazuz.utils.GuiHolder;
import br.alkazuz.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.NumberFormat;
import java.util.HashMap;

public class GuiInventory {

    public static void openConfirmShop(Player player, Settings.Size size) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("size", size.getSize());
        GuiHolder holder = new GuiHolder(9247, properties);
        final Inventory inv = Main.getInstance().getServer().createInventory(holder, 9, "Menu de Terrenos - Comprar");
        ItemBuilder item = new ItemBuilder(Material.GRASS).setName("§aTerreno " + size.getSize() + "x" + size.getSize());
        if (size.isMobSpawn()) {
            item.addLore("", "§aPreço: §f" + NumberFormat.getInstance().format(size.getPrice()), "§6Preço VIP: §f" + NumberFormat.getInstance().format(size.getVipPrice()), "",
                    "§bPossui gaiola de mobs.");
        } else {
            item.addLore("", "§aPreço: §f" + NumberFormat.getInstance().format(size.getPrice()), "§6Preço VIP: §f" + NumberFormat.getInstance().format(size.getVipPrice()));
        }
        inv.setItem(2, new ItemBuilder(Material.FENCE)
                .setName("§aNesta localidade").addLore("", "§7Criar um terreno nesse local.", "§aClique para confirmar.").toItemStack());
        inv.setItem(4, item.toItemStack());
        inv.setItem(6, new ItemBuilder(Material.ENDER_PEARL)
                .setName("§aEm qualquer localidade").addLore("", "§7O terreno será criado em um local aleatório.", "§aClique para confirmar.").toItemStack());
        player.openInventory(inv);
    }

    public static void openShop(Player player) {
        GuiHolder holder = new GuiHolder(9249);
        final Inventory inv = Main.getInstance().getServer().createInventory(holder, 18, "Menu de Terrenos - Comprar");
        for (Settings.Size size : Settings.SIZES) {
            ItemBuilder item = new ItemBuilder(Material.GRASS).setName("§aTerreno " + size.getSize() + "x" + size.getSize());
            if (size.isMobSpawn()) {
                item.addLore("", "§aPreço: §f" + NumberFormat.getInstance().format(size.getPrice()), "§6Preço VIP: §f" + NumberFormat.getInstance().format(size.getVipPrice()), "",
                        "§bPossui gaiola de mobs.");
            } else {
                item.addLore("", "§aPreço: §f" + NumberFormat.getInstance().format(size.getPrice()), "§6Preço VIP: §f" + NumberFormat.getInstance().format(size.getVipPrice()));
            }
            inv.addItem(item.toItemStack());
        }
        player.openInventory(inv);
    }

    public static void openMain(Player player){
        GuiHolder holder = new GuiHolder(9248);
        final Inventory inv = Main.getInstance().getServer().createInventory(holder, 9, "Menu de Terrenos");
        inv.setItem(3, new ItemBuilder(Material.BOOK).setName("§aInformações")
                .addLore("", "§7Clique para ver as informações", "§7do terreno sua posição.").toItemStack());
        inv.setItem(4, new ItemBuilder(Material.GRASS).setName("§aComprar Terreno")
                .addLore("", "§7Clique para comprar um terreno", "§7e escolher o tamanho.").toItemStack());
        inv.setItem(5, new ItemBuilder(Material.ENDER_PEARL).setName("§aTeleportar")
                .addLore("", "§7Clique para teleportar para seu terreno.").toItemStack());
        player.openInventory(inv);
    }

}
