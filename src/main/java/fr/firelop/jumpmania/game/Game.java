package fr.firelop.jumpmania.game;

import de.simonsator.partyandfriends.api.pafplayers.OnlinePAFPlayer;
import de.simonsator.partyandfriends.api.pafplayers.PAFPlayerManager;
import de.simonsator.partyandfriends.api.party.PartyManager;
import de.simonsator.partyandfriends.api.party.PlayerParty;
import fr.firelop.jumpmania.JumpMania;
import fr.firelop.jumpmania.statics.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

public class Game {
    public JumpMania plugin;
    public List<Player> playersInGame = new ArrayList<>();
    private final Location[] gameSpawn;
    private final String lobbyName;
    public boolean won = false;
    public Player playerOnEmerald = null;
    public int timeForEmerald = 0;
    public List<LootChest> lootChests = new ArrayList<>();
    public RoundManager rounds = new RoundManager();
    public int serverIndex;
    public int ticks;
    public HashMap<Player, ItemStack[]> inventories;



    public Game(JumpMania plugin, List<Player> playersInGame, List<Location> gameSpawns, String lobbyName, int serverIndex, HashMap<Player, ItemStack[]> inventories) {
        this.plugin = plugin;
        this.playersInGame.addAll(playersInGame);
        plugin.getLogger().info(playersInGame.size() + " ");
        this.gameSpawn = gameSpawns.toArray(new Location[0]);
        this.lobbyName = lobbyName;
        this.serverIndex = serverIndex;
        this.inventories = inventories;
    }

    public void startGame() {
        for(Player player : playersInGame) {


            player.setScoreboard(Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard());
            player.sendMessage(ChatColor.WHITE + "[JumpMania] " + ChatColor.GREEN +  "Début de la manche 1/2");
            player.setInvulnerable(false);
            player.setHealth(20);
            respawn(player);
        }

        updateScoreboard();
        updateTicks();

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if(this.playerOnEmerald != null && this.playerOnEmerald.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.EMERALD_BLOCK) {
                this.timeForEmerald++;


                if(timeForEmerald >= ticks) {
                    win(this.playerOnEmerald);
                } else {
                    if(timeForEmerald % 20 == 0 && !won) {
                        for(Player player : playersInGame) {
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Le joueur " + ChatColor.WHITE + this.playerOnEmerald.getName() + ChatColor.GREEN + " est sur l'émeraude ! (Restant: " + ChatColor.WHITE + (ticks - timeForEmerald) / 20 + ChatColor.GREEN + " secondes)"));
                        }
                    }
                }
            } else {
                if(this.playerOnEmerald != null) {
                    for(Player player : playersInGame) {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
                    }
                }
                this.playerOnEmerald = null;
                this.timeForEmerald = 0;
                for (Player player : playersInGame) {
                    Block blockUnder = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
                    if (blockUnder.getType() == Material.EMERALD_BLOCK && !player.getAllowFlight()) {
                        this.playerOnEmerald = player;
                        this.timeForEmerald = 1;
                        updateTicks();
                    }
                }
            }
            for (Player player : playersInGame) {
                Block blockUnder = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
                if (blockUnder.getType() == Material.EMERALD_BLOCK && !player.getAllowFlight()) {
                    if(player.isInvulnerable()) player.setInvulnerable(false);
                }
                if(player.isInvulnerable()) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 2, 1, false, false, false));
                }
            }
            for(LootChest lootChest : lootChests) {
                lootChest.updateHologram();
            }
        }, 0, 1);
    }

    private void updateTicks() {
        switch(this.playersInGame.size()) {
            case 2:
                ticks = 100;
                break;
            case 3:
                ticks = 80;
                break;
            default:
                ticks = 60;
                break;
        }
    }

    public Location getRandomSpawn() {
        return gameSpawn[(int) (Math.random() * gameSpawn.length)];
    }

    public void respawn(Player player) {
        respawn(player, false);
    }
    public void respawn(Player player, boolean lostLife) {
        if(lostLife) {
            double health = player.getHealth();
            if(health - 2 > 0) {
                player.setHealth(health - 2);
            }  else  {
                player.setAllowFlight(true);
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 200, 0, false, false, false));
                player.setHealth(20);
                // Disable pvp
                player.setInvulnerable(true);
                // Prevent player from hitting other players
                player.setCollidable(false);
                player.getInventory().clear();

                player.sendTitle(ChatColor.RED + "Vous êtes mort !", ChatColor.GRAY + "Vous réaparraîtrez dans 10 secondes.", 10, 70, 20);
                Bukkit.getScheduler().runTaskLater(plugin, () -> respawn(player), 200);
                return;
            }
        } else {
        	player.setHealth(20);
        }
        player.setCollidable(true);
        player.setAllowFlight(false);
        player.teleport(getRandomSpawn());
        player.setFireTicks(0);
        player.setGameMode(GameMode.ADVENTURE);
        player.setFoodLevel(20);
        player.setSaturation(200);
        player.getInventory().clear();
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta bowMeta = bow.getItemMeta();
        assert bowMeta != null;
        bowMeta.addEnchant(Enchantment.ARROW_KNOCKBACK, 2, true);
        bow.setItemMeta(bowMeta);
        player.getInventory().addItem(bow);
        player.getInventory().addItem(new ItemStack(Material.ARROW, 5));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999, 255, false, false, false));
        player.setInvulnerable(true);


        Bukkit.getScheduler().runTaskLater(plugin, () -> player.setInvulnerable(false), 30);
    }

    public void winMessage(Player player) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "addexp jumpmania "+ player.getName() + " 200");

        won = true;
        updateScoreboard();
        for (Player plyer : playersInGame) {
            plyer.sendMessage(ChatColor.WHITE + "[JumpMania] " + ChatColor.BOLD + ChatColor.GOLD + "Le joueur " + player.getName() + " a gagné !");
            if(plyer != player) {
                plyer.sendTitle(ChatColor.RED + "Vous avez perdu !", ChatColor.GRAY + "Le joueur " + player.getName() + " a gagné !", 10, 70, 20);
            }
            this.plugin.hidenPlayers.addPlayerToGroup(plyer, 0);
        }
        player.sendTitle(ChatColor.GREEN + "Vous avez gagné !", ChatColor.GRAY + "Vous avez gagné 200 points d'expérience !", 10, 70, 20);
        // Put a firework on the player
        Firework firework = player.getWorld().spawn(player.getLocation(), Firework.class);
        FireworkMeta fm = firework.getFireworkMeta();
        fm.setPower(1);
        fm.addEffect(FireworkEffect.builder().withTrail().withColor(org.bukkit.Color.RED).with(org.bukkit.FireworkEffect.Type.BALL_LARGE).build());
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Player plyer : playersInGame) {
                plyer.teleport(Objects.requireNonNull(Bukkit.getWorld("jumpmania")).getSpawnLocation());
                plyer.getInventory().setContents(inventories.get(plyer));
                inventories.remove(plyer);
                plyer.setScoreboard(Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard());
                plyer.setGameMode(GameMode.ADVENTURE);
                plyer.setHealth(20);
                plyer.setFoodLevel(20);
                plyer.setExp(0);
                plyer.setLevel(0);
                Utils.clearEffects(plyer);

            }
            plugin.lobbys.get(lobbyName).isInGame = false;
            plugin.games.remove(this);

        }, 100);
    }

    public void win(Player player) {
        if(!won) {
            rounds.winners.add(player);
            Player winner = rounds.isFinished();

            if(winner != null) {
                winMessage(winner);
            } else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "addexp jumpmania "+ player.getName() + " 50");
                for(Player p : playersInGame) {
                    p.sendMessage(ChatColor.WHITE + "[JumpMania] " + ChatColor.BOLD + ChatColor.GOLD + "Le joueur " + player.getName() + " a gagné la manche !");
                    p.sendTitle(ChatColor.GOLD + "Manche " + (rounds.winners.size() + 1) + "/" + (rounds.winners.size()<2?"2":rounds.winners.size() + 1), ChatColor.GREEN + "Le joueur " + player.getName() + " a remporté la manche !", 10, 70, 20);
                    p.sendMessage(ChatColor.WHITE + "[JumpMania] " + ChatColor.GREEN +  "Début de la manche " + (rounds.winners.size() + 1) + "/" + (rounds.winners.size()<2?"2":rounds.winners.size() + 1));
                    respawn(p);
                }
                updateScoreboard();

            }
        }
    }

    public void updateScoreboard() {
        Scoreboard scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard();
        scoreboard.registerNewObjective("mgname", "dummy", ChatColor.RED + "JumpMania");
        Objects.requireNonNull(scoreboard.getObjective("mgname")).setDisplaySlot(DisplaySlot.SIDEBAR);
        Objects.requireNonNull(scoreboard.getObjective("mgname")).getScore(" ").setScore(9);
        Objects.requireNonNull(scoreboard.getObjective("mgname")).getScore(ChatColor.WHITE + "Map: " + ChatColor.GOLD + ChatColor.BOLD + this.lobbyName).setScore(10);
        Objects.requireNonNull(scoreboard.getObjective("mgname")).getScore("   ").setScore(7);
        Objects.requireNonNull(scoreboard.getObjective("mgname")).getScore(ChatColor.WHITE + "Joueurs: " + ChatColor.GREEN + ChatColor.BOLD + this.playersInGame.size()).setScore(6);
        Objects.requireNonNull(scoreboard.getObjective("mgname")).getScore("    ").setScore(5);
        Objects.requireNonNull(scoreboard.getObjective("mgname")).getScore("     ").setScore(-1);
        Objects.requireNonNull(scoreboard.getObjective("mgname")).getScore(ChatColor.WHITE + "www.skilliogames.fr").setScore(-2);



        Objects.requireNonNull(scoreboard.getObjective("mgname")).getScore(ChatColor.WHITE + "Manche: " + ChatColor.GOLD + (!won ? this.rounds.winners.size() + 1 : this.rounds.winners.size()) + "/" + (rounds.winners.size()<2 ? "2" : (won ? rounds.winners.size() : rounds.winners.size() + 1))).setScore(8);
        HashMap<Player, Integer> playerWins = new HashMap<>();
        for(Player winner : this.rounds.winners) {
            playerWins.put(winner, playerWins.getOrDefault(winner, 0) + 1);
        }

        for(Player p : this.playersInGame) {
            if(!playerWins.containsKey(p)) {
                playerWins.put(p, 0);
            }
        }
        for(Map.Entry<Player, Integer> winner : playerWins.entrySet()) {
            plugin.getLogger().info(winner.getKey().getDisplayName());
            Objects.requireNonNull(scoreboard.getObjective("mgname")).getScore(ChatColor.RESET + winner.getKey().getName() + " (" + ChatColor.GREEN + winner.getValue() + ChatColor.RESET + ")").setScore(winner.getValue());
        }

        for(Player p : playersInGame) {
            p.setScoreboard(scoreboard);
        }
    }

    public void openLootChest(Player player, Chest holder) {
        boolean found = false;
        for(LootChest lootChest : lootChests) {
            if(lootChest.holder.equals(holder)) {
                found = true;
                lootChest.open(player);
            }
        }
        if(!found) {
            LootChest lootChest = new LootChest(holder, plugin);
            lootChests.add(lootChest);
            lootChest.open(player);
        }
    }

    public void playerLeave(Player player) {
        player.getInventory().setContents(inventories.get(player));
        inventories.remove(player);
        player.setScoreboard(Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard());
        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setExp(0);

        player.setLevel(0);
        Utils.clearEffects(player);
        this.plugin.hidenPlayers.addPlayerToGroup(player, 0);

        OnlinePAFPlayer pafPlayer = PAFPlayerManager.getInstance().getPlayer(player);
        PlayerParty party = PartyManager.getInstance().getParty(pafPlayer);
        if(party != null) {
            party.leaveParty(pafPlayer);
            for(OnlinePAFPlayer p : party.getAllPlayers()) {
                p.getPlayer().sendMessage(ChatColor.RED + player.getDisplayName() + " a quitté le lobby !");
            }
        }

        player.teleport(Objects.requireNonNull(Bukkit.getWorld("jumpmania")).getSpawnLocation());
        playersInGame.remove(player);
        for(Player p : playersInGame) {
            p.sendMessage("[JumpMania] [" + ChatColor.RED + "-" + ChatColor.RESET + "] Le joueur " + ChatColor.AQUA + player.getDisplayName() + " a quitté votre partie.");
        }
        updateScoreboard();
        if(playersInGame.size() == 1) {
            winMessage(playersInGame.get(0));
        }
    }

}
