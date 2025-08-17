package org.core.coreProgram.Cores.Nox.Passive;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;
import org.core.Cool.Cool;
import org.core.coreConfig;
import org.core.coreProgram.Cores.Nox.coreSystem.Nox;

import java.util.*;

public class Dream implements Listener {

    private final Nox config;
    private final JavaPlugin plugin;
    private final Cool cool;
    private final coreConfig tag;

    public Dream(Nox config, coreConfig tag, JavaPlugin plugin, Cool cool) {
        this.config = config;
        this.plugin = plugin;
        this.cool = cool;
        this.tag = tag;
    }

    private final Map<String, BossBar> activeDreamBars = new HashMap<>();
    private final Map<String, BukkitRunnable> activeDreamTasks = new HashMap<>();

    public void wanderersDream(Player player, String skill) {

        long duration = switch (skill) {
            case "F" -> 12000;
            case "R" -> 3000;
            default -> 6000;
        };

        String key = player.getUniqueId().toString() + "_" + skill;

        if (activeDreamBars.containsKey(key)) {
            BossBar oldBar = activeDreamBars.get(key);
            oldBar.removeAll();
            activeDreamBars.remove(key);
        }

        if (activeDreamTasks.containsKey(key)) {
            BukkitRunnable oldTask = activeDreamTasks.get(key);
            if (!oldTask.isCancelled()) oldTask.cancel();
            activeDreamTasks.remove(key);
        }

        BossBar bossBar = Bukkit.createBossBar(skill + " wanderersDream", BarColor.PURPLE, BarStyle.SOLID);
        bossBar.addPlayer(player);
        activeDreamBars.put(key, bossBar);

        long cooldownEndTime = System.currentTimeMillis() + duration;
        long finalDuration = duration;

        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                long remainingTime = cooldownEndTime - System.currentTimeMillis();
                if (remainingTime <= 0) {
                    bossBar.setProgress(1.0);
                    bossBar.removePlayer(player);
                    activeDreamBars.remove(key);
                    activeDreamTasks.remove(key);
                    cancel();
                } else {
                    Map<String, Double> skillMap = config.dreamPoint
                            .computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());
                    double elapsed = finalDuration - remainingTime;
                    skillMap.putIfAbsent(skill, 1.0);
                    skillMap.put(skill, elapsed / 1000);
                    double progress = elapsed / finalDuration;
                    bossBar.setProgress(Math.min(1.0, Math.max(0.0, progress)));
                }
            }
        };

        task.runTaskTimer(plugin, 0L, 1L);
        activeDreamTasks.put(key, task);
    }
}
