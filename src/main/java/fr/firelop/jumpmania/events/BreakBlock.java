package fr.firelop.jumpmania.events;

import fr.firelop.jumpmania.JumpMania;
import fr.firelop.jumpmania.game.Game;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BreakBlock implements Listener {
    JumpMania plugin;
    public BreakBlock(JumpMania jumpMania) {
        this.plugin = jumpMania;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        for(Game game : plugin.games) {
            if(game.playersInGame.contains(player)) {
                player.sendMessage(ChatColor.RED + "Vous ne pouvez pas casser de blocs pendant une partie.");
                event.setCancelled(true);
                break;
            }
        }
    }
}
