package br.alkazuz.terrenos.command.sub;

import br.alkazuz.terrenos.command.SubCommandBase;
import br.alkazuz.terrenos.config.Settings;
import org.bukkit.command.CommandSender;

public class SubcommandReload extends SubCommandBase {

    public SubcommandReload() {
        super("reload", "terreno.subcomand.reload", "reload", "Recarrega as configurações do plugin.");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Settings.load();
        sender.sendMessage("§aConfigurações recarregadas com sucesso.");
        return true;
    }

}
