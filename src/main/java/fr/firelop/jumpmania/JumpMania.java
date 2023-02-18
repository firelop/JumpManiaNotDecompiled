package fr.firelop.jumpmania;

import fr.firelop.jumpmania.commands.JumpManiaJoinCommand;
import fr.firelop.jumpmania.commands.JumpManiaLeaveCommand;
import fr.firelop.jumpmania.commands.ServerSpectateCommand;
import fr.firelop.jumpmania.events.*;
import fr.firelop.jumpmania.expansions.LobbysExpansion;
import fr.firelop.jumpmania.game.Game;
import fr.firelop.jumpmania.game.Lobby;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class JumpMania extends JavaPlugin {

    public HashMap<String, Lobby> lobbys = new HashMap<>();
    public List<Game> games = new ArrayList<>();
    public HidenPlayerManager hidenPlayers = new HidenPlayerManager(this);
    public HolographicDisplaysAPI hdAPI;

    @Override
    public void onEnable() {
        if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
            getLogger().severe("*** HolographicDisplays is not installed or not enabled. ***");
            getLogger().severe("*** This plugin will be disabled. ***");
            this.setEnabled(false);
            return;
        }
        hdAPI = HolographicDisplaysAPI.get(this);
        getLogger().info("Le plugin a été activé !");
        List<Player> players = new ArrayList<>();
        for(Player pla : Bukkit.getOnlinePlayers()) {
            players.add(pla);
        }
        getLogger().info(String.valueOf(hidenPlayers.addGroup(players)));
        System.out.println(hidenPlayers);
        // -239 4 271 test
        getCommand("jumpmania").setExecutor(new JumpManiaJoinCommand(this));
        getCommand("quitter").setExecutor(new JumpManiaLeaveCommand(this));
        getCommand("spectate").setExecutor(new ServerSpectateCommand(this));
        Bukkit.getPluginManager().registerEvents(new Movement(this), this);
        // On chest open
        Bukkit.getPluginManager().registerEvents(new ChestOpen(this), this);
        // On player interact with an inventory
        Bukkit.getPluginManager().registerEvents(new InventoryClick(this), this);
        // On player close inventory
        Bukkit.getPluginManager().registerEvents(new InventoryClose(this), this);
        // On player use item
        Bukkit.getPluginManager().registerEvents(new ItemUse(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDisconnect(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerSendMessage(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerConnect(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerCommand(this), this);
        Bukkit.getPluginManager().registerEvents(new BreakBlock(this), this);
        Bukkit.getPluginManager().registerEvents(new DropItem(this), this);


        saveDefaultConfig();
        // Small check to make sure that PlaceholderAPI is installed
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new LobbysExpansion(this).register();
        }

    }

    @Override
    public void onDisable() {
        getLogger().info("Le plugin a été activé !");
    }
}
