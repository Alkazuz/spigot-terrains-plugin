package br.alkazuz.terrenos.command.sub;

import br.alkazuz.terrenos.command.SubCommandBase;
import br.alkazuz.terrenos.inventory.GuiInventory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SubcommandMenu extends SubCommandBase {

    public SubcommandMenu() {
        super("menu", "terreno.cmd.menu", "menu", "Abre o menu de terrenos.");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        GuiInventory.openMain(player);

        return true;
    }
}
