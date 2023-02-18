package fr.firelop.jumpmania.events;

import fr.firelop.jumpmania.JumpMania;
import fr.firelop.jumpmania.game.Game;
import fr.firelop.jumpmania.game.Lobby;
import jdk.internal.ValueBased;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class ItemUse implements Listener {
    private JumpMania plugin;
    public List<ItemStack> loots = new ArrayList<>();
    public ItemUse(JumpMania jumpMania) {
        this.plugin = jumpMania;
        createLoots();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(event.getItem() != null) {
            if(event.getItem().getType() == Material.BARRIER) {
                if(event.getItem().getItemMeta().getDisplayName().equals(ChatColor.RED + "Quitter le lobby")) {
                    for(Lobby lobby : plugin.lobbys.values()) {
                        if(lobby.getPlayersInLobby().contains(event.getPlayer())) {
                            lobby.removePlayer(event.getPlayer());
                            return;
                        }
                    }
                }
            }
            byte index = 0;
            for(ItemStack loot : loots) {
                if(event.getItem().getItemMeta().getDisplayName().equals(loot.getItemMeta().getDisplayName())) {
                    use(index, event);
                    break;
                }
                index++;
            }
        }
    }

    private void use(byte index, PlayerInteractEvent event) {
        Game game = null;
        for(Game game1 : plugin.games) {
            if(game1.playersInGame.contains(event.getPlayer())) {
                game = game1;
            }
        }

        boolean isMalus = false;
        String name = event.getItem().getItemMeta().getDisplayName();
        event.getItem().setAmount(event.getItem().getAmount() - 1);
        switch(index) {
            case 0:
                event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*15, 1));
                break;
            case 1:
                event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20*5, 2));
                break;
            case 2:
                event.getPlayer().setInvulnerable(true);
                Bukkit.getScheduler().runTaskLater(plugin, () -> event.getPlayer().setInvulnerable(false), 100);
                break;

            case 3:
                int speed = 3;
                Vector direction = event.getPlayer().getEyeLocation().getDirection().multiply(speed);
                LargeFireball projectile = event.getPlayer().getWorld().spawn(event.getPlayer().getEyeLocation().add(direction.getX(), direction.getY(), direction.getZ()), LargeFireball.class);
                projectile.setVelocity(direction);
                projectile.setYield(15);
                projectile.setShooter(event.getPlayer());
                break;
            case 4:
                isMalus = true;
                for(Player player : game.playersInGame) {
                    if(player != event.getPlayer()) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*5, 2));
                    }
                }
                break;

            case 5:
                isMalus = true;
                for(Player player : game.playersInGame) {
                    if(player != event.getPlayer()) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20*5, 4));
                    }
                }
                break;
        }
        for(Player player : game.playersInGame) {
            player.sendMessage((isMalus ? ChatColor.RED : ChatColor.GREEN) + event.getPlayer().getName() + " a utilisé " + name);
        }
    }

    public void createLoots() {
        ItemStack speed = new ItemStack(Material.IRON_BOOTS);
        ItemMeta speedMeta = speed.getItemMeta();
        speedMeta.setDisplayName(ChatColor.GREEN + "Speed 15s");
        speed.setItemMeta(speedMeta);
        loots.add(speed);

        ItemStack jump = new ItemStack(Material.FEATHER);
        ItemMeta jumpMeta = jump.getItemMeta();
        jumpMeta.setDisplayName(ChatColor.GREEN + "JumpBoost 5s");
        jump.setItemMeta(jumpMeta);
        loots.add(jump);

        ItemStack invincibility = new ItemStack(Material.TOTEM_OF_UNDYING);
        ItemMeta invincibilityMeta = invincibility.getItemMeta();
        invincibilityMeta.setDisplayName(ChatColor.GREEN + "Invincibilité 5s");
        invincibility.setItemMeta(invincibilityMeta);
        loots.add(invincibility);

        ItemStack firecharge = new ItemStack(Material.FIRE_CHARGE);
        ItemMeta firechargeMeta = firecharge.getItemMeta();
        firechargeMeta.setDisplayName(ChatColor.GREEN + "Firecharge");
        firecharge.setItemMeta(firechargeMeta);
        loots.add(firecharge);

        ItemStack cobeweb = new ItemStack(Material.COBWEB, 1);
        ItemMeta cobewebMeta = cobeweb.getItemMeta();
        cobewebMeta.setDisplayName(ChatColor.GREEN + "Slowness 5s");
        cobeweb.setItemMeta(cobewebMeta);
        loots.add(cobeweb);

        ItemStack enderEye = new ItemStack(Material.ENDER_EYE, 1);
        ItemMeta enderEyeMeta = enderEye.getItemMeta();
        enderEyeMeta.setDisplayName(ChatColor.GREEN + "Blindness 5s");
        enderEye.setItemMeta(enderEyeMeta);
        loots.add(enderEye);

    }
}
