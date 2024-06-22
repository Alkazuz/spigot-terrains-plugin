package br.alkazuz.terrenos.utils;

import br.alkazuz.terrenos.Main;
import br.alkazuz.terrenos.config.Settings;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class PlayerUtils {
    private static Chat chat;

    public static String getPlayerPrefix(String player) {
        String world = Settings.DEFAULT_WORLD;

        Vault vault = Main.getInstance().getVault();
        if (vault == null) {
            return player;
        }

        if (chat == null) {
            RegisteredServiceProvider<Chat> service = Bukkit.getServicesManager().getRegistration(Chat.class);
            if (service != null) {
                chat = service.getProvider();
            }
        }

        return chat.getPlayerPrefix(world, player).replace("&", "§");
    }
}
