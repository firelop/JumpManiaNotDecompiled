package fr.firelop.jumpmania.events;

import fr.firelop.jumpmania.JumpMania;
import fr.firelop.jumpmania.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamage implements Listener {
    JumpMania plugin;
    public EntityDamage(JumpMania jumpMania) {
        this.plugin = jumpMania;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();

            if(damager.getAllowFlight()) {
                for(Game game : plugin.games) {
                    if(game.playersInGame.contains(damager)) {
                        event.setCancelled(true);
                    }
                }

            }
        }

    }
}
