package fr.firelop.jumpmania.events;

import fr.firelop.jumpmania.JumpMania;
import fr.firelop.jumpmania.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerSendMessage implements Listener {
    JumpMania plugin;
    public PlayerSendMessage(JumpMania jumpMania) {
        this.plugin = jumpMania;
    }

    @EventHandler
    public void onMessage(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        int playerServer = plugin.hidenPlayers.getPlayerServer(player);
        if(playerServer > 0) {
            event.setCancelled(true);
            plugin.hidenPlayers.sendServerPlayersMessage(playerServer, player.getPlayerListName() + ": " + event.getMessage());
            plugin.getLogger().info("[srv" + playerServer + "] " + player.getDisplayName() + ": " + event.getMessage());
        }
    }
}
