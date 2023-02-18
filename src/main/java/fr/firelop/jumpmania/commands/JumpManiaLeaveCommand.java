package fr.firelop.jumpmania.commands;

import fr.firelop.jumpmania.JumpMania;
import fr.firelop.jumpmania.game.Lobby;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class JumpManiaLeaveCommand implements CommandExecutor {
    public JumpMania plugin;
    public JumpManiaLeaveCommand(JumpMania jumpMania) {
        this.plugin = jumpMania;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            for(Map.Entry<String, Lobby> lEntry : plugin.lobbys.entrySet()) {
                if (lEntry.getValue().getPlayersInLobby().contains(player)) {
                    lEntry.getValue().removePlayer(player);

                    return true;
                }
            }
            player.sendMessage(ChatColor.DARK_RED + "Vous n'êtes pas dans un lobby. Rejoignez en un en tapant /jumpmania");
        } else {
            sender.sendMessage(ChatColor.DARK_RED + "Vous devez être un joueur pour exécuter cette commande !");
        }
        return true;
    }

}
