package fr.firelop.jumpmania.commands;

import fr.firelop.jumpmania.JumpMania;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ServerSpectateCommand implements CommandExecutor {
    public JumpMania plugin;
    public ServerSpectateCommand(JumpMania jumpMania) {
        this.plugin = jumpMania;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length != 1) return false;
        if(sender instanceof Player) {
            Player requestedPlayer = Bukkit.getPlayer(args[0]);
            Player player = (Player) sender;

            if(requestedPlayer == null) {
                player.sendMessage(ChatColor.DARK_RED + "Ce joueurs n'existe pas ou n'est pas connecté");
                return true;
            }

            int playerServer = plugin.hidenPlayers.getPlayerServer(requestedPlayer);
            plugin.hidenPlayers.addPlayerToGroup(player, playerServer);
            player.teleport(requestedPlayer.getLocation());
            player.setGameMode(GameMode.SPECTATOR);

        }
        sender.sendMessage(ChatColor.DARK_RED + "Vous devez être un joueur pour executer cette commande");
        return true;
    }
}
