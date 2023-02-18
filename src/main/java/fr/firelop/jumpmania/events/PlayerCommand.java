package fr.firelop.jumpmania.events;

import fr.firelop.jumpmania.JumpMania;
import fr.firelop.jumpmania.game.Game;
import fr.firelop.jumpmania.game.Lobby;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;

import javax.smartcardio.CommandAPDU;

public class PlayerCommand implements Listener {
    public JumpMania plugin;
    public PlayerCommand(JumpMania jumpMania) {
        this.plugin = jumpMania;
    }

    @EventHandler
    public void sendCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        for(Lobby lobby : plugin.lobbys.values()) {
            if(lobby.getPlayersInLobby().contains(player)) {
                event.setCancelled(true);
                return;
            }
        }

        for(Game game : plugin.games) {
            if(game.playersInGame.contains(player)) {
                player.sendMessage(ChatColor.DARK_RED + "Vous ne pouvez pas executer de commandes pendant une partie.");
                event.setCancelled(true);
                break;
            }
        }
    }
}
