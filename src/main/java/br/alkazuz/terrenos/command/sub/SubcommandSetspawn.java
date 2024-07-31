package br.alkazuz.terrenos.command.sub;

import br.alkazuz.terrenos.command.SubCommandBase;
import br.alkazuz.terrenos.inventory.GuiInventory;
import br.alkazuz.terrenos.object.Terreno;
import br.alkazuz.terrenos.utils.TerrenoManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SubcommandSetspawn extends SubCommandBase {

    public SubcommandSetspawn() {
        super("setspawn", "terreno.cmd.setspawn", "setspawn",
                "Define um local para spawnar mobs no terreno");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        Terreno terreno = TerrenoManager.getTerrenoInLocation(player.getLocation());

        if (terreno == null) {
            player.sendMessage("§cVocê não está em um terreno.");
            return true;
        }

        if (!terreno.getOwner().equalsIgnoreCase(player.getName())) {
            player.sendMessage("§cSomente o dono do terreno pode definir o spawn.");
            return true;
        }

        GuiInventory.openSpawnSet(player, terreno);

        return true;
    }
}
