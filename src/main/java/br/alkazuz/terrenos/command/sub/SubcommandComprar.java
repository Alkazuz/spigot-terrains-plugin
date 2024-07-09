package br.alkazuz.terrenos.command.sub;

import br.alkazuz.terrenos.command.SubCommandBase;
import br.alkazuz.terrenos.inventory.GuiInventory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SubcommandComprar extends SubCommandBase {

    public SubcommandComprar() {
        super("comprar", "terreno.cmd.comprar", "comprar", "Abre o menu de compra de terrenos.");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        GuiInventory.openShop(player);

        return true;
    }
}
