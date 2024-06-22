package br.alkazuz.terrenos.command.sub;

import br.alkazuz.terrenos.command.SubCommandBase;
import br.alkazuz.terrenos.inventory.GuiFlagsInventory;
import br.alkazuz.terrenos.inventory.GuiPermsInventory;
import br.alkazuz.terrenos.object.PlayerTerreno;
import br.alkazuz.terrenos.object.PlayerTerrenoManager;
import br.alkazuz.terrenos.object.Terreno;
import br.alkazuz.terrenos.utils.TerrenoManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SubcommandFlags extends SubCommandBase {

    public SubcommandFlags() {
        super("flags", "terreno.cmd.flags", "flags", "Atualiza as flags de pvp/interação do terreno.");
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

        GuiFlagsInventory.openInventory(player, terreno);

        return true;
    }
}
