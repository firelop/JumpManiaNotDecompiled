package fr.firelop.jumpmania.game;

import de.simonsator.partyandfriends.api.pafplayers.OnlinePAFPlayer;
import de.simonsator.partyandfriends.api.pafplayers.PAFPlayerManager;
import de.simonsator.partyandfriends.api.party.PartyManager;
import de.simonsator.partyandfriends.api.party.PlayerParty;
import fr.firelop.jumpmania.JumpMania;
import fr.firelop.jumpmania.statics.Utils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Lobby {
    public int maxPlayers = 4;
    private List<Player> playerInLobby = new ArrayList<>();
    public int defaultTimeBeforeStart = 20;
    public int timeBeforeStart = defaultTimeBeforeStart;
    private String lobbyName;
    public JumpMania plugin;
    boolean isLooping = false;
    public boolean isInGame = false;
    Location location;
    List<Location> gameSpawns;
    public int serverIndex;
    public HashMap<Player, ItemStack[]> inventories = new HashMap<>();


    public Lobby(JumpMania plugin, Location location, List<Location> locations, String name, int maxPlayers) {
        this.plugin = plugin;
        this.location = location;
        this.gameSpawns = locations;
        this.lobbyName = name;
        this.maxPlayers = maxPlayers;
        List<Player> players = new ArrayList<>();
        this.serverIndex = plugin.hidenPlayers.addGroup(players);
    }
    public Lobby(JumpMania plugin, Location location, List<Location> locations, String name) {
        this.plugin = plugin;
        this.location = location;
        this.gameSpawns = locations;
        this.lobbyName = name;
        List<Player> players = new ArrayList<>();
        this.serverIndex = plugin.hidenPlayers.addGroup(players);
    }

    public void addParty(PlayerParty party) {
        Player leader = party.getLeader().getPlayer();
        for(OnlinePAFPlayer player : party.getAllPlayers()) {
            inventories.put(player.getPlayer(), leader.getInventory().getContents());
            addPlayer(player.getPlayer(), true);
        }
    }

    public void addPlayer(Player player) {
        addPlayer(player, false);
    }

    public void addPlayer(Player player, boolean isParty) {
        if(playerInLobby.contains(player)) {
            player.sendMessage(ChatColor.RED + "Vous êtes déjà dans un lobby !");
            return;
        }
        if(!isParty) {
            inventories.put(player, player.getInventory().getContents());
        }

        player.setInvulnerable(true);
        Utils.clearEffects(player);
        player.setGameMode(GameMode.ADVENTURE);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setExp(0);
        player.setLevel(0);

        ItemStack leave = new ItemStack(Material.BARRIER);
        ItemMeta leaveMeta = leave.getItemMeta();
        leaveMeta.setDisplayName(ChatColor.RED + "Quitter le lobby");
        leave.setItemMeta(leaveMeta);
        player.getInventory().clear();
        player.getInventory().setItem(8, leave);

        playerInLobby.add(player);
        plugin.hidenPlayers.addPlayerToGroup(player, this.serverIndex);


        for(Player p : playerInLobby) {
            p.sendMessage("[" + ChatColor.GREEN + "+" + ChatColor.RESET + "] " + player.getDisplayName() + " a rejoint le lobby (" + ChatColor.GOLD + this.playerInLobby.size() + ChatColor.RESET + "/" + ChatColor.GREEN + this.maxPlayers + ChatColor.RESET + ")");
        }
        player.teleport(location);
        if(!isLooping) {
            Bukkit.getScheduler().runTaskTimer(this.plugin, () -> {
                if(playerInLobby.size() >= 2) {
                    if(timeBeforeStart == 0) {
                        for(Player plyer : playerInLobby) {
                            plyer.sendMessage(ChatColor.GREEN + "La partie va commencer !");
                        }
                        launchGame();
                    } else {
                        timeBeforeStart--;
                        updateScoreboard();
                    }
                } else if (timeBeforeStart != defaultTimeBeforeStart) {
                    timeBeforeStart = defaultTimeBeforeStart;
                    updateScoreboard();
                }

            }, 0, 20);
            isLooping = true;
        }
        updateScoreboard();
    }

    public void removePlayer(Player player) {
        player.getInventory().clear();
        player.getInventory().setContents(inventories.get(player));
        inventories.remove(player);
        player.teleport(Bukkit.getWorld("jumpmania").getSpawnLocation());
        playerInLobby.remove(player);
        plugin.hidenPlayers.addPlayerToGroup(player, 0);
        player.setInvulnerable(false);
        player.setGameMode(GameMode.SURVIVAL);
        player.sendMessage(ChatColor.GREEN + "Vous avez quitté le lobby !");

        OnlinePAFPlayer pafPlayer = PAFPlayerManager.getInstance().getPlayer(player);
        PlayerParty party = PartyManager.getInstance().getParty(pafPlayer);
        if(party != null) {
            party.leaveParty(pafPlayer);
            for(OnlinePAFPlayer p : party.getAllPlayers()) {
                p.getPlayer().sendMessage(ChatColor.RED + player.getDisplayName() + " a quitté le lobby !");
            }
        }

        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        updateScoreboard();
    }

    public List<Player> getPlayersInLobby() {
        return playerInLobby;
    }

    public void launchGame() {
        isInGame = true;
        Game game = new Game(this.plugin, this.playerInLobby, this.gameSpawns, this.lobbyName, this.serverIndex, this.inventories);
        plugin.games.add(game);
        this.playerInLobby.clear();
        game.startGame();
    }

    public void updateScoreboard() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        scoreboard.registerNewObjective("mgname", "dummy", ChatColor.GREEN + "JumpMania");
        scoreboard.getObjective("mgname").setDisplaySlot(DisplaySlot.SIDEBAR);
        scoreboard.getObjective("mgname").getScore(" ").setScore(7);
        scoreboard.getObjective("mgname").getScore(ChatColor.WHITE + "Map: " + ChatColor.GOLD + ChatColor.BOLD + this.lobbyName).setScore(6);
        scoreboard.getObjective("mgname").getScore("   ").setScore(5);
        scoreboard.getObjective("mgname").getScore(ChatColor.WHITE + "Joueurs: " + ChatColor.GREEN + ChatColor.BOLD + this.playerInLobby.size() + ChatColor.RESET + "/" + ChatColor.RED + ChatColor.BOLD + this.maxPlayers).setScore(4);
        scoreboard.getObjective("mgname").getScore("    ").setScore(3);
        scoreboard.getObjective("mgname").getScore("Début dans : " + ChatColor.GOLD + ChatColor.BOLD + this.timeBeforeStart).setScore(2);
        scoreboard.getObjective("mgname").getScore("      ").setScore(1);
        scoreboard.getObjective("mgname").getScore(ChatColor.WHITE + "www.skilliogames.fr").setScore(0);

        for(Player p : playerInLobby) {
            p.setScoreboard(scoreboard);
        }
    }


}
