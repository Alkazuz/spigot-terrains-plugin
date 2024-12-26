package br.alkazuz.terrenos.listeners;

import br.alkazuz.terrenos.Main;
import br.alkazuz.terrenos.config.Settings;
import br.alkazuz.terrenos.inventory.GuiBuyInventory;
import br.alkazuz.terrenos.object.Terreno;
import br.alkazuz.terrenos.utils.TerrenoManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
public class SellRegionListener implements org.bukkit.event.Listener {

    @EventHandler(ignoreCancelled = false, priority = org.bukkit.event.EventPriority.HIGHEST)
    public void onBreakSignSell(BlockBreakEvent e) {
        Player player = e.getPlayer();
        if (!player.getWorld().getName().equals(Settings.TERRAIN_WORLD)) {
            return;
        }
        if (e.getBlock().getType() != org.bukkit.Material.WALL_SIGN && e.getBlock().getType() != org.bukkit.Material.SIGN_POST) {
            return;
        }
        org.bukkit.block.Sign sign = (org.bukkit.block.Sign) e.getBlock().getState();
        if (!isSellSign(sign)) { // if (!isSellSign(sign)) {
            return;
        }
        Terreno region = TerrenoManager.getTerrenoInLocation(e.getBlock().getLocation());
        if (region == null) {
            e.getBlock().breakNaturally();
            return;
        }
        e.setCancelled(true);
        if (region.getOwner().equals(player.getName())) {
            e.getBlock().breakNaturally();
            player.sendMessage("§aVocê removeu a placa de venda do seu terreno.");
        }
    }

    @EventHandler(priority = org.bukkit.event.EventPriority.HIGHEST)
    public void onRightClickBlockInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (!player.getWorld().getName().equals(Settings.TERRAIN_WORLD)) {
            return;
        }
        if (e.getClickedBlock() == null) {
            return;
        }
        if (e.getClickedBlock().getType() != org.bukkit.Material.WALL_SIGN && e.getClickedBlock().getType() != org.bukkit.Material.SIGN_POST) {
            return;
        }
        if (e.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        org.bukkit.block.Sign sign = (org.bukkit.block.Sign) e.getClickedBlock().getState();
        if (!isSellSign(sign)) {
            return;
        }
        e.setCancelled(true);
        Terreno region = TerrenoManager.getTerrenoInLocation(e.getClickedBlock().getLocation());
        if (region == null) {
            player.sendMessage("§cEsta placa não está em um terreno.");
            sign.setType(Material.AIR);
            return;
        }

        if (region.getOwner().equals(player.getName())) {
            player.sendMessage("§cVocê não pode comprar seu próprio terreno.");
            return;
        }

        int price = Integer.parseInt(ChatColor.stripColor(sign.getLine(2).replace(",", "")));
        if (price < 0) {
            player.sendMessage("§cO preço deve ser maior que 0.");
            return;
        }

        Economy economy = Main.getInstance().getEconomy();
        if (economy.getBalance(player.getName()) < price) {
            player.sendMessage("§cVocê não tem dinheiro suficiente para comprar este terreno.");
            return;
        }

        GuiBuyInventory.openConfirmShop(player, region, price, sign);

    }

    public boolean isSellSign(Sign sign) {
        return sign.getLine(0).equalsIgnoreCase("§e[COMPRAR]");
    }

}
