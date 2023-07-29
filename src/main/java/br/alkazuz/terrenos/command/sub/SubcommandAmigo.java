package br.alkazuz.terrenos.command.sub;

import br.alkazuz.terrenos.command.SubCommandBase;
import br.alkazuz.terrenos.utils.TerrenoManager;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SubcommandAmigo extends SubCommandBase {

    public SubcommandAmigo() {
        super("amigo", "terreno.subcomand.amigo", "amigo <add|remove> <nick>", "Adiciona um amigo ao terreno");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        ProtectedRegion region = TerrenoManager.getProtectedRegion(player.getLocation());
        if (region == null) {
            player.sendMessage("§cVocê não está em um terreno.");
            return true;
        }

        if (args.length != 3) {
            return false;
        }

        if (args[1].equalsIgnoreCase("add")) {

            if (!region.getOwners().contains(player.getName())) {
                player.sendMessage("§cVocê não é dono deste terreno.");
                return true;
            }

            if (region.getOwners().contains(args[2])) {
                player.sendMessage("§cEste jogador já é dono do terreno.");
                return true;
            }

            if (region.getMembers().contains(args[2])) {
                player.sendMessage("§cEste jogador já é membro do terreno.");
                return true;
            }

            region.getMembers().addPlayer(args[2]);
            try {
                TerrenoManager.saveRegion(player.getLocation());
                player.sendMessage("§aVocê adicionou o jogador §f" + args[2] + " §acomo membro do terreno.");

            } catch (ProtectionDatabaseException e) {
                e.printStackTrace();
                player.sendMessage("§cOcorreu um erro ao salvar o terreno.");
            }

            return true;
        } else if (args[1].equalsIgnoreCase("remove")) {

            if (!region.getOwners().contains(player.getName())) {
                player.sendMessage("§cVocê não é dono deste terreno.");
                return true;
            }

            if (!region.getMembers().contains(args[2])) {
                player.sendMessage("§cEste jogador não é membro do terreno.");
                return true;
            }

            region.getMembers().removePlayer(args[2]);
            try {
                TerrenoManager.saveRegion(player.getLocation());
                player.sendMessage("§aVocê removeu o jogador §f" + args[2] + " §ado terreno.");

            } catch (ProtectionDatabaseException e) {
                e.printStackTrace();
                player.sendMessage("§cOcorreu um erro ao salvar o terreno.");
            }

            return true;
        } else {
            player.sendMessage("§cUtilize /terreno amigo <add|remove> <nick>");
        }
        return true;
    }
}
