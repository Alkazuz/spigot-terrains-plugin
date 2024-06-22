package br.alkazuz.terrenos.listeners;

import br.alkazuz.terrenos.utils.TerrenoManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        TerrenoManager.loadPlayerTerrenos(event.getPlayer());
    }

}
