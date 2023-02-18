package fr.firelop.jumpmania.events;

import fr.firelop.jumpmania.JumpMania;
import fr.firelop.jumpmania.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class Movement implements Listener {
    private JumpMania plugin;
    public Movement(JumpMania jumpMania) {
        this.plugin = jumpMania;
    }

    @EventHandler
    public void onFallDamage(PlayerMoveEvent event) {


        Player player = event.getPlayer();
        Block block = player.getWorld().getBlockAt(event.getTo().getBlockX(), event.getTo().getBlockY() - 1, event.getTo().getBlockZ());
        if(block.getType() == Material.BARRIER) {
            for (Game game : plugin.games) {
                if (game.playersInGame.contains(player)) {
                    if(!game.won) {
                        game.respawn(player);
                    }
                }
            }
        }

    }
}
