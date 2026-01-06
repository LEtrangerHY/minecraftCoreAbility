package org.core.effect.crowdControl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Invulnerable implements Effects, Listener {
    public static Set<Entity> invulnerablePlayers = new HashSet<>();

    private final Player player;
    private final long duration;

    public Invulnerable(Player player, long duration) {
        this.player = player;
        this.duration = duration;
    }

    @Override
    public void applyEffect(Entity entity) {

        invulnerablePlayers.add(player);
        long endTime = System.currentTimeMillis() + duration;

        entity.setInvulnerable(true);

        new BukkitRunnable() {
            @Override
            public void run() {

                if (System.currentTimeMillis() >= endTime || !player.isOnline()) {
                    removeEffect(player);
                    cancel();
                }

            }
        }.runTaskTimer(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("Core")), 0L, 1L);
    }

    @Override
    public void removeEffect(Entity entity) {
        invulnerablePlayers.remove(entity);
        entity.setInvulnerable(false);
    }

    public static boolean isInvulnerable(Player player) {
        return invulnerablePlayers.contains(player);
    }
}
