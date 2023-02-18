package fr.firelop.jumpmania.events;

import fr.firelop.jumpmania.JumpMania;
import fr.firelop.jumpmania.game.Lobby;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class DropItem implements Listener {
    JumpMania plugin;
    public DropItem(JumpMania jumpMania) {
        this.plugin = jumpMania;
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        for(Lobby lobby : plugin.lobbys.values()) {
            if(lobby.getPlayersInLobby().contains(player)) {
                event.setCancelled(true);
                return;
            }
        }
    }
}