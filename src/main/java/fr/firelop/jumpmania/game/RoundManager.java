package fr.firelop.jumpmania.game;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoundManager {
    public HashMap<Player, Integer> wins = new HashMap<>();
    public List<Player> winners = new ArrayList<>();

    public Player isFinished() {
        HashMap<Player, Integer> playerWins = new HashMap<>();

        for(Player winner : winners) {
            playerWins.put(winner, playerWins.getOrDefault(winner, 0) + 1);
        }

        for(Map.Entry<Player, Integer> playerWin : playerWins.entrySet()) {
            if(playerWin.getValue() >= 2) {
                return playerWin.getKey();
            }
        }
        return null;
    }
}
