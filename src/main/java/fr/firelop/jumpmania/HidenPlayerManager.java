package fr.firelop.jumpmania;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HidenPlayerManager {
    private List<List<Player>> groups = new ArrayList<>();
    JumpMania plugin;
    public HidenPlayerManager(JumpMania jumpMania) {
        this.plugin = jumpMania;
    }

    public int addGroup(List<Player> players) {
        this.groups.add(players);
        return this.groups.size() - 1;
    }

    public void updateServers() {
        int in = 0;
        for(List<Player> server : groups) {
            for(Player player : server) {
                for(int i = 0; i<groups.size(); i++)  {
                    List<Player> serverPlayers = groups.get(i);
                    if(i == in) {
                        for(Player serverPlayer : serverPlayers) {
                            serverPlayer.showPlayer(plugin, player);
                            player.showPlayer(plugin, serverPlayer);
                        }
                        continue;
                    }

                    for(Player serverPlayer : serverPlayers) {
                        serverPlayer.hidePlayer(plugin, player);
                        player.hidePlayer(plugin, serverPlayer);
                    }
                }
                player.showPlayer(plugin, player);
            }
            in++;
        }
    }


    // Ce code est magnifique
    public void addPlayerToGroup(Player player, int index) {
        for(List<Player> server : groups) {
            if(server.contains(player)) server.remove(player);
        }
        groups.get(index).add(player);
        updateServers();
        /*for(Player op : groups.get(index)) {
            op.showPlayer(plugin, player);
        }


        for(int i = 0; i<groups.size(); i++) {
            if(i == index) continue;
            if(groups.get(i).contains(player)) groups.get(i).remove(player);
            for(Player op : groups.get(i)) {
                op.hidePlayer(plugin, player);
                player.hidePlayer(plugin, op);
            }
        }
        */
    }

    public int getPlayerServer(Player requestedPlayer) {
        for(int i = 0; i<groups.size(); i++) {
            if(groups.get(i).contains(requestedPlayer)) return i;
        }
        return -1;
    }

    public void sendServerPlayersMessage(int server, String s) {
        for(Player player : groups.get(server)) {
            player.sendMessage(s);
        }
    }

    public void leaveServer(Player player) {
        int server = getPlayerServer(player);
        if(server == -1) return;
        groups.get(server).remove(player);
        for(Player op : groups.get(server)) {
            op.hidePlayer(plugin, player);
        }
        updateServers();
    }
}

