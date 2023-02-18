package fr.firelop.jumpmania.events;

import fr.firelop.jumpmania.JumpMania;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClick implements Listener {
    private JumpMania plugin;
    public InventoryClick(JumpMania jumpMania) {
        this.plugin = jumpMania;
    }

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent event) {

        if(event.getWhoClicked().getOpenInventory().getTitle().equals("§6LootBox - Ouverture en cours...")) {
            event.setCancelled(true);
        }

        if(event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.RED + "Quitter le lobby")) {
            event.setCancelled(true);
        }
    }
}
