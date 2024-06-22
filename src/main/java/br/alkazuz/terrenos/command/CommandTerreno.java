package br.alkazuz.terrenos.command;

import br.alkazuz.terrenos.config.Settings;
import br.alkazuz.terrenos.config.inventory.GuiInventory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

public class CommandTerreno implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof org.bukkit.entity.Player)) {
            commandSender.sendMessage("§cApenas jogadores podem executar este comando.");
            return true;
        }
        Player player = (Player) commandSender;

        if (!player.getLocation().getWorld().getName().equals(Settings.TERRAIN_WORLD)) {
            player.sendMessage("§cVocê não pode executar este comando neste mundo.");
            return true;
        }

        if (strings.length == 0) {
            sendHelp(commandSender);
            return true;
        }

        SubCommandBase subCommandBase = SubCommands.getSubCommand(strings[0]);
        if (subCommandBase == null) {
            commandSender.sendMessage("§cComando não encontrado.");
            sendHelp(commandSender);
            return true;
        }

        if (!player.hasPermission(subCommandBase.getPermission())) {
            player.sendMessage("§cVocê não tem permissão para executar este comando.");
            return true;
        }

        if (!subCommandBase.execute(player, strings)) {
            player.sendMessage("§cUtilize /terreno " + subCommandBase.getUsage());
        }

        return true;
    }

    private void sendHelp(CommandSender sender) {
        StringBuilder stringBuilder = new StringBuilder(
                "§eTerrenos - Ajuda\n"+
                "§7Comandos disponíveis:\n"
        );
        for (SubCommandBase subCommandBase1 : SubCommands.getSubCommands()) {
            if (sender.hasPermission(subCommandBase1.getPermission())) {
                stringBuilder.append("§f/terreno ")
                        .append(subCommandBase1.getUsage()).append(" §7- ").append(subCommandBase1.getDescription()).append("\n");
            }
        }
        sender.sendMessage(stringBuilder.toString());
    }
}
