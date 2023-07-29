package br.alkazuz.terrenos.command.sub;

import br.alkazuz.terrenos.command.SubCommandBase;
import br.alkazuz.terrenos.utils.TerrenoManager;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SubcommandInfo extends SubCommandBase {

    public SubcommandInfo() {
        super("info", "terreno.cmd.info", "info", "Mostra informações sobre o terreno.");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Location location = player.getLocation();

        player.sendMessage(TerrenoManager.getRegionMessageInfo(location));

        return true;
    }
}
