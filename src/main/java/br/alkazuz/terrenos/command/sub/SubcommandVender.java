package br.alkazuz.terrenos.command.sub;

import br.alkazuz.terrenos.Main;
import br.alkazuz.terrenos.command.SubCommandBase;
import br.alkazuz.terrenos.object.Terreno;
import br.alkazuz.terrenos.utils.TerrenoManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChatEvent;

import java.text.NumberFormat;

public class SubcommandVender extends SubCommandBase {

    public SubcommandVender() {
        super("vender", "terreno.subcomand.vender", "vender", "Vende seu terreno");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Terreno region = TerrenoManager.getTerrenoInLocation(player.getLocation());
        if (region == null) {
            player.sendMessage("§cVocê não está em um terreno.");
            return true;
        }

        if (!region.getOwner().equalsIgnoreCase(player.getName())) {
            player.sendMessage("§cVocê não é dono deste terreno.");
            return true;
        }

        player.sendMessage("§aDigite o preço do terreno no chat. Para cancelar, digite §f\"cancelar\"§a.");
        Main.getInstance().getEventWaiter().waitForEvent(PlayerChatEvent.class, EventPriority.HIGHEST,
            event -> !event.isCancelled() &&
                    event.getPlayer().getName().equalsIgnoreCase(player.getName())
                            && (isInt(event.getMessage()) || event.getMessage().equalsIgnoreCase("cancelar")),
            event -> {
                event.setCancelled(true);
                if (event.getMessage().equalsIgnoreCase("cancelar")) {
                    player.sendMessage("§cVocê cancelou a venda do terreno.");
                    return;
                }
                int price = Integer.parseInt(event.getMessage());
                if (price < 0) {
                    player.sendMessage("§cO preço deve ser maior que 0.");
                    return;
                }
                try {
                    TerrenoManager.sellRegion(player, region, price);
                    player.sendMessage("§aVocê colocou seu terreno à venda por §f" + NumberFormat.getInstance().format(price) + "§a. Se você quiser cancelar, basta quebnrar a placa de venda.");
                } catch (Exception e) {
                    e.printStackTrace();
                    player.sendMessage("§c" + e.getMessage());
                }
            },30 * 20, () -> {
                player.sendMessage("§cVocê não digitou o preço a tempo. A venda foi cancelada.");
            }
        );
        return true;
    }

    public boolean isInt(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;

        }
    }

}
