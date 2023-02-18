package fr.firelop.jumpmania.events;

import fr.firelop.jumpmania.JumpMania;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerConnect implements Listener {
    public JumpMania plugin;
    public PlayerConnect(JumpMania jumpMania) {
        this.plugin = jumpMania;
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerJoinEvent event) {
        this.plugin.hidenPlayers.addPlayerToGroup(event.getPlayer(), 0);
    }
}
