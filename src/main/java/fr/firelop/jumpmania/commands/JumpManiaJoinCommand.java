package fr.firelop.jumpmania.commands;

import de.simonsator.partyandfriends.api.pafplayers.OnlinePAFPlayer;
import de.simonsator.partyandfriends.api.pafplayers.PAFPlayerManager;
import de.simonsator.partyandfriends.api.party.PartyManager;
import de.simonsator.partyandfriends.api.party.PlayerParty;
import fr.firelop.jumpmania.JumpMania;
import fr.firelop.jumpmania.game.Lobby;
import fr.firelop.jumpmania.statics.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class JumpManiaJoinCommand implements CommandExecutor {
    public JumpMania plugin;
    private final Location lobby = new Location(Bukkit.getWorld("test"), -239, 4, 271);
    public JumpManiaJoinCommand(JumpMania jumpMania) {
        this.plugin = jumpMania;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length != 1) return false;
        if(sender instanceof Player) {
            String name = args[0];
            Player player = (Player) sender;

            if(plugin.lobbys.containsKey(name)) {
                Lobby lobby = plugin.lobbys.get(name);
                if(lobby.getPlayersInLobby().size() < lobby.maxPlayers) {
                    if(lobby.isInGame) {
                        player.sendMessage(ChatColor.DARK_RED + "La partie est déjà commencée.");
                    } else {
                        OnlinePAFPlayer pafPlayer = PAFPlayerManager.getInstance().getPlayer(player);
                        PlayerParty party = PartyManager.getInstance().getParty(pafPlayer);
                        if(party != null) {
                            if(party.isLeader(pafPlayer)) {
                                if(party.getAllPlayers().size() - lobby.getPlayersInLobby().size() > lobby.maxPlayers) {
                                    player.sendMessage(ChatColor.RED + "Votre party est trop grand pour rejoindre ce lobby.");
                                    return true;
                                }
                                lobby.addParty(party);
                            } else {
                                player.sendMessage(ChatColor.RED + "Vous devez être le leader de votre party pour rejoindre ce lobby.");
                            }
                        } else {
                            if(lobby.getPlayersInLobby().size() + 1 > lobby.maxPlayers) {
                                player.sendMessage(ChatColor.RED + "Ce lobby est complet.");
                                return true;
                            }
                            lobby.addPlayer(player);
                        }
                    }
                } else {
                    player.sendMessage(ChatColor.DARK_RED + "Ce lobby est complet.");
                }
            } else {
                if(plugin.getConfig().contains(name+".lobby")) {
                    List<?> locationsEr = plugin.getConfig().getList(name+".spawns");

                    plugin.getLogger().info(String.valueOf(locationsEr.size()));
                    plugin.getLogger().info(String.valueOf(locationsEr.get(0)));

                    List<Location> locations = new ArrayList<>();



                    for (Object locationUncasted : locationsEr) {
                        if (locationUncasted instanceof fr.firelop.jumpmania.config.Location) {
                            fr.firelop.jumpmania.config.Location location = (fr.firelop.jumpmania.config.Location) locationUncasted;
                            Location loca = new Location(Bukkit.getWorld(location.world), location.x, location.y, location.z, location.yaw, location.pitch);
                            locations.add(loca);
                        } else if (locationUncasted instanceof LinkedHashMap) {
                            LinkedHashMap<String, Object> locationMap = (LinkedHashMap<String, Object>) locationUncasted;
                            fr.firelop.jumpmania.config.Location location = new fr.firelop.jumpmania.config.Location();
                            location.world = (String) locationMap.get("world");
                            location.x = (int) locationMap.get("x");
                            location.y = (int) locationMap.get("y");
                            location.z = (int) locationMap.get("z");
                            location.yaw = (int) locationMap.get("yaw") / 1f;
                            location.pitch = (int) locationMap.get("pitch") / 1f;

                            Location loca = new Location(Bukkit.getWorld(location.world), location.x, location.y, location.z, location.yaw, location.pitch);
                            locations.add(loca);
                        }
                    }




                    String world = plugin.getConfig().getString(name+".lobby.world");
                    int x = plugin.getConfig().getInt(name+".lobby.x");
                    int y = plugin.getConfig().getInt(name+".lobby.y");
                    int z = plugin.getConfig().getInt(name+".lobby.z");
                    int yaw = plugin.getConfig().getInt(name+".lobby.yaw");
                    int pitch = plugin.getConfig().getInt(name+".lobby.pitch");

                    Location loca = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
                    Lobby lobby = new Lobby(plugin, loca, locations, name);
                    plugin.lobbys.put(name, lobby);


                    OnlinePAFPlayer pafPlayer = PAFPlayerManager.getInstance().getPlayer(player);
                    PlayerParty party = PartyManager.getInstance().getParty(pafPlayer);
                    if(party != null) {
                        if(party.isLeader(pafPlayer)) {
                            if(party.getAllPlayers().size() - lobby.getPlayersInLobby().size() > lobby.maxPlayers) {
                                player.sendMessage(ChatColor.RED + "Votre party est trop grand pour rejoindre ce lobby.");
                                return true;
                            }
                            lobby.addParty(party);
                        } else {
                            player.sendMessage(ChatColor.RED + "Vous devez être le leader de votre party pour rejoindre ce lobby.");
                        }
                    } else {
                        if(lobby.getPlayersInLobby().size() + 1 > lobby.maxPlayers) {
                            player.sendMessage(ChatColor.RED + "Ce lobby est complet.");
                            return true;
                        }
                        lobby.addPlayer(player);
                    }


                } else {
                    player.sendMessage(ChatColor.RED + "Ce lobby n'existe pas.");
                }
            }


            player.setInvulnerable(true);
        } else {
            sender.sendMessage("Vous devez être un joueur pour exécuter cette commande !");
        }
        return true;
    }

}
