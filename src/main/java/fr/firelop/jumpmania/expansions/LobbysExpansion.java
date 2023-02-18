package fr.firelop.jumpmania.expansions;

import fr.firelop.jumpmania.JumpMania;
import fr.firelop.jumpmania.game.Lobby;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class LobbysExpansion extends PlaceholderExpansion {
    public JumpMania plugin;
    public LobbysExpansion(JumpMania jumpMania) {
        this.plugin = jumpMania;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "jumpmania";
    }

    @Override
    public @NotNull String getAuthor() {
        return "firelop";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }
    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        String name = params.split("_")[0];
        String lobbyName = params.split("_")[1];

        if(name.equalsIgnoreCase("etat")){
            if(plugin.lobbys.containsKey(lobbyName)) {
                Lobby lobby = plugin.lobbys.get(lobbyName);
                if(lobby.isInGame) {
                    return ChatColor.DARK_RED + "Partie en cours";
                }
                if(lobby.getPlayersInLobby().size() == 0) {
                    return ChatColor.GRAY + "En attente";
                } else if(lobby.getPlayersInLobby().size() < 2) {
                    return ChatColor.GREEN + "En attente";
                } else if(lobby.getPlayersInLobby().size() == lobby.maxPlayers) {
                    return ChatColor.DARK_RED + "Complet";

                } else {
                    return ChatColor.GOLD + "Démarre bientôt";
                }

            } else {
                return ChatColor.GRAY + "En attente";
            }
        }

        if(name.equalsIgnoreCase("joueurs")) {
            if(plugin.lobbys.containsKey(lobbyName)) {
                Lobby lobby = plugin.lobbys.get(lobbyName);
                if(lobby.getPlayersInLobby().size() == 0) {
                    return ChatColor.GRAY + "0/"+lobby.maxPlayers;
                } else if(lobby.getPlayersInLobby().size() < lobby.maxPlayers) {
                    return ChatColor.GREEN + "" + lobby.getPlayersInLobby().size() + "/" + lobby.maxPlayers;
                } else {
                    return ChatColor.DARK_RED + "" + lobby.getPlayersInLobby().size() + "/" + lobby.maxPlayers;
                }

            } else {
                return ChatColor.GRAY + "0/4";
            }
        }

        return null; // Placeholder is unknown by the Expansion
    }
}
