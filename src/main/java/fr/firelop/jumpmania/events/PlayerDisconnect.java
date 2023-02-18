package fr.firelop.jumpmania.events;

import fr.firelop.jumpmania.JumpMania;
import fr.firelop.jumpmania.game.Game;
import fr.firelop.jumpmania.game.Lobby;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerDisconnect implements Listener {
    public JumpMania plugin;
    public PlayerDisconnect(JumpMania jumpMania) {
        this.plugin = jumpMania;
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        this.plugin.hidenPlayers.addPlayerToGroup(event.getPlayer(), 0);
        Player player = event.getPlayer();
        for(Game game : plugin.games) {
            if(game.playersInGame.contains(player)) {
                game.playerLeave(player);
                plugin.hidenPlayers.leaveServer(player);
                return;
            }
        }
        for(Lobby lobby : plugin.lobbys.values()) {
            if(lobby.getPlayersInLobby().contains(player)) {
                lobby.removePlayer(player);
                plugin.hidenPlayers.leaveServer(player);
                return;
            }
        }
        plugin.hidenPlayers.leaveServer(player);
    }
}
