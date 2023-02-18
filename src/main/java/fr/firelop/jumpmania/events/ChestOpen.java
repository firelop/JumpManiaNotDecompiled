package fr.firelop.jumpmania.events;

import fr.firelop.jumpmania.JumpMania;
import fr.firelop.jumpmania.game.Game;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class ChestOpen implements Listener {
    private JumpMania plugin;
    public ChestOpen(JumpMania jumpMania) {
        this.plugin = jumpMania;
    }

    @EventHandler
    public void onChestOpen(InventoryOpenEvent event) {
        if(event.getInventory().getHolder() instanceof Chest) {
            for (Game game : plugin.games) {
                if (game.playersInGame.contains((Player) event.getPlayer())) {
                    event.setCancelled(true);
                    game.openLootChest((Player) event.getPlayer(), (Chest) event.getInventory().getHolder());
                }
            }
        }
    }

}
