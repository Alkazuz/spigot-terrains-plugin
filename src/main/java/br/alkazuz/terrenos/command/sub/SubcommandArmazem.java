package br.alkazuz.terrenos.command.sub;

import br.alkazuz.terrenos.command.SubCommandBase;
import br.alkazuz.terrenos.inventory.GuiStorageInventory;
import br.alkazuz.terrenos.object.Terreno;
import br.alkazuz.terrenos.utils.TerrenoManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SubcommandArmazem extends SubCommandBase {

    public SubcommandArmazem() {
        super("armazem", "terreno.cmd.armazem", "armazem", "Abre o menu de armazém do terreno.");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        Terreno terreno = TerrenoManager.getTerrenoInLocation(player.getLocation());

        if (terreno == null) {
            player.sendMessage("§cVocê não está em um terreno.");
            return true;
        }

        if (!terreno.getOwner().equals(player.getName())) {
            player.sendMessage("§cVocê não é o dono deste terreno.");
            return true;
        }

        GuiStorageInventory.open(player, terreno);

        return true;
    }
}
