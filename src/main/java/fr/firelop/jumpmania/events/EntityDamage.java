package fr.firelop.jumpmania.events;

import fr.firelop.jumpmania.JumpMania;
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
        if(event.getEntity() instanceof Player) {
            if(event.getDamager() instanceof Player) {
                Player damager = (Player) event.getDamager();
                if(damager.getAllowFlight()) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
