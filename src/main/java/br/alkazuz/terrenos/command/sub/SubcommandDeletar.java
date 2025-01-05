package br.alkazuz.terrenos.command.sub;

import br.alkazuz.terrenos.Main;
import br.alkazuz.terrenos.command.SubCommandBase;
import br.alkazuz.terrenos.inventory.GuiDeleteInventory;
import br.alkazuz.terrenos.object.Terreno;
import br.alkazuz.terrenos.utils.TerrenoManager;
import br.alkazuz.terrenos.workload.ClearTerrenoWorkload;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.scheduler.BukkitTask;

import java.text.NumberFormat;

public class SubcommandDeletar extends SubCommandBase {

    public SubcommandDeletar() {
        super("deletar", "terreno.subcomand.deletar", "deletar", "Deleta seu terreno");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Terreno region = TerrenoManager.getTerrenoInLocation(player.getLocation());
        if (region == null) {
            player.sendMessage("§cVocê não está em um terreno.");
            return true;
        }

        if (!region.getOwner().equalsIgnoreCase(player.getName())) {
            player.sendMessage("§cVocê não é dono deste terreno.");
            return true;
        }

        GuiDeleteInventory.open(player, region);
        return true;
    }

}
