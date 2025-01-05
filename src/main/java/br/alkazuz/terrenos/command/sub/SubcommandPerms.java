package br.alkazuz.terrenos.command.sub;

import br.alkazuz.terrenos.command.SubCommandBase;
import br.alkazuz.terrenos.inventory.GuiPermsInventory;
import br.alkazuz.terrenos.object.PlayerTerreno;
import br.alkazuz.terrenos.object.PlayerTerrenoManager;
import br.alkazuz.terrenos.object.Terreno;
import br.alkazuz.terrenos.utils.TerrenoManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SubcommandPerms extends SubCommandBase {

    public SubcommandPerms() {
        super("perms", "terreno.cmd.perms", "perms <jogador>", "Visualiza as permissões de um jogador.");
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
            player.sendMessage("§cVocê não é o dono deste terreno.");
            return true;
        }

        if (args.length != 2) {
            player.sendMessage("§cUtilize /terreno perms <jogador>");
            return true;
        }

        String target = args[1];

        PlayerTerreno playerTerreno = PlayerTerrenoManager.getPlayerTerreno(target, terreno);

        GuiPermsInventory.openInventory(player, terreno, playerTerreno);

        return true;
    }
}
