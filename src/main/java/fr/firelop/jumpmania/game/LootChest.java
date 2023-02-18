package fr.firelop.jumpmania.game;

import fr.firelop.jumpmania.JumpMania;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.PlaceholderSetting;
import me.filoghost.holographicdisplays.api.hologram.VisibilitySettings;
import me.filoghost.holographicdisplays.api.hologram.line.TextHologramLine;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class LootChest {
    public Chest holder;
    public int defaultCooldown = 15000;
    public long lastUsedTimestamp = 0;
    public boolean isBeeingUsed = false;
    int numberOfBars = 0;
    public Player player;
    private BukkitTask progressTask;
    private BukkitTask successTask;
    public List<ItemStack> loots = new ArrayList<>();
    public Hologram holo;
    public TextHologramLine holoLine;

    public LootChest(Chest holder, JumpMania plugin) {
        this.holder = holder;
        this.createLoots();
        this.holo = plugin.hdAPI.createHologram(holder.getLocation().add(0.5, 1.5, 0.5));
        this.holoLine = holo.getLines().appendText("");
        this.holo.getVisibilitySettings().setGlobalVisibility(VisibilitySettings.Visibility.VISIBLE);
    }

    public boolean canBeUsed() {
        return (lastUsedTimestamp + defaultCooldown) <= System.currentTimeMillis();
    }

    public void open(Player player) {
        if (canBeUsed()) {
            if(isBeeingUsed) {
                player.sendMessage(ChatColor.RED + "Ce coffre est déjà ouvert par quelqu'un d'autre !");
                return;
            }

            isBeeingUsed = true;
            this.player = player;
            Inventory proggressInv = Bukkit.createInventory(player, 27, "§6LootBox - Ouverture en cours...");
            player.openInventory(proggressInv);
            Bukkit.getLogger().info("LootChest: " + player.getName() + " opened a chest");


            this.progressTask = Bukkit.getScheduler().runTaskTimer(Bukkit.getPluginManager().getPlugin("JumpMania"), new Runnable() {
                @Override
                public void run() {
                    proggressInv.setContents(getProgressBar(numberOfBars));
                    incrementBar();
                }
            }, 0, 2);

            this.successTask = Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("JumpMania"), new Runnable() {
                @Override
                public void run() {
                    progressTask.cancel();
                    toggleBeeingUsed();
                    updateTimestamp();
                    resetBar();
                    player.closeInventory();
                    player.getInventory().addItem(getRandomLoot());
                }
            }, 14);
            return;
        }
        player.sendMessage(ChatColor.RED + "Ce coffre est en cooldown !");
    }

    private void resetBar() {
        numberOfBars = 0;
    }

    public void incrementBar() {
        numberOfBars++;
    }

    public void toggleBeeingUsed() {
        isBeeingUsed = false;
    }

    public void closed() {
        this.progressTask.cancel();
        this.successTask.cancel();
        this.toggleBeeingUsed();
        this.resetBar();
        this.player = null;
    }

    public void updateTimestamp() {
        lastUsedTimestamp = System.currentTimeMillis();
    }

    public ItemStack[] getProgressBar(int numberOfBars) {
        ItemStack[] progressBar = new ItemStack[27];
        for (int i = 10; i < 17; i++) {
            if(numberOfBars > 0) {
                progressBar[i] = new ItemStack(org.bukkit.Material.LIME_STAINED_GLASS_PANE);
                numberOfBars--;
            } else {
                progressBar[i] = new ItemStack(org.bukkit.Material.GRAY_STAINED_GLASS_PANE);
            }
        }
        return progressBar;
    }

    public ItemStack getRandomLoot() {
        int random = (int) (Math.random() * loots.size());
        ItemStack loot = loots.get(random);
        return loot;
    }

    public void updateHologram() {
        if(!canBeUsed()) {
            holoLine.setText(ChatColor.RED + "Cooldown: " + (int) ((defaultCooldown - (System.currentTimeMillis() - lastUsedTimestamp)) / 1000) + "s");
        } else {
            holoLine.setText("");
        }
    }


    public void createLoots() {
        ItemStack speed = new ItemStack(Material.IRON_BOOTS);
        ItemMeta speedMeta = speed.getItemMeta();
        speedMeta.setDisplayName(ChatColor.GREEN + "Speed 15s");
        speed.setItemMeta(speedMeta);
        loots.add(speed);

        ItemStack jump = new ItemStack(Material.FEATHER);
        ItemMeta jumpMeta = jump.getItemMeta();
        jumpMeta.setDisplayName(ChatColor.GREEN + "JumpBoost 5s");
        jump.setItemMeta(jumpMeta);
        loots.add(jump);

        ItemStack invincibility = new ItemStack(Material.TOTEM_OF_UNDYING);
        ItemMeta invincibilityMeta = invincibility.getItemMeta();
        invincibilityMeta.setDisplayName(ChatColor.GREEN + "Invincibilité 5s");
        invincibility.setItemMeta(invincibilityMeta);
        loots.add(invincibility);

        ItemStack firecharge = new ItemStack(Material.FIRE_CHARGE, 3);
        ItemMeta firechargeMeta = firecharge.getItemMeta();
        firechargeMeta.setDisplayName(ChatColor.GREEN + "Firecharge");
        firecharge.setItemMeta(firechargeMeta);
        loots.add(firecharge);

        ItemStack cobeweb = new ItemStack(Material.COBWEB, 1);
        ItemMeta cobewebMeta = cobeweb.getItemMeta();
        cobewebMeta.setDisplayName(ChatColor.GREEN + "Slowness 5s");
        cobeweb.setItemMeta(cobewebMeta);
        loots.add(cobeweb);

        ItemStack enderEye = new ItemStack(Material.ENDER_EYE, 1);
        ItemMeta enderEyeMeta = enderEye.getItemMeta();
        enderEyeMeta.setDisplayName(ChatColor.GREEN + "Blindness 5s");
        enderEye.setItemMeta(enderEyeMeta);
        loots.add(enderEye);

    }


}
