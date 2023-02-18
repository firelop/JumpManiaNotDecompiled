package fr.firelop.jumpmania.statics;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class Utils {
    public static void clearEffects (Player player){
        for (PotionEffect effect :player.getActivePotionEffects ()){
            player.removePotionEffect(effect.getType());
        }
    }
}
