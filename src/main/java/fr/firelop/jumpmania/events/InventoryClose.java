package fr.firelop.jumpmania.events;

import fr.firelop.jumpmania.JumpMania;
import fr.firelop.jumpmania.game.Game;
import fr.firelop.jumpmania.game.LootChest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryClose implements Listener {
    private JumpMania plugin;
    public InventoryClose(JumpMania jumpMania) {
        this.plugin = jumpMania;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if(event.getView().getTitle().equals("§6LootBox - Ouverture en cours...")) {
            for(Game game : plugin.games) {
                if(game.playersInGame.contains(event.getPlayer())) {
                    for(LootChest lootChest : game.lootChests) {
                        if(lootChest.player != null && lootChest.player.equals(event.getPlayer())) {
                            lootChest.closed();
                        }
                    }
                }
            }
        }
    }
}
