package org.core.coreProgram.Cores.Saboteur.Skill;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.core.Cool.Cool;
import org.core.Effect.ForceDamage;
import org.core.coreProgram.AbsCoreSystem.SkillBase;
import org.core.coreProgram.Cores.Saboteur.coreSystem.Saboteur;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class Q implements SkillBase {
    private final Saboteur config;
    private final JavaPlugin plugin;
    private final Cool cool;

    public Q(Saboteur config, JavaPlugin plugin, Cool cool) {
        this.config = config;
        this.plugin = plugin;
        this.cool = cool;
    }

    @Override
    public void Trigger(Player player) {
        World world = player.getWorld();
        Block targetBlock = player.getTargetBlockExact(10, FluidCollisionMode.ALWAYS);
        Particle.DustOptions activeFalse = new Particle.DustOptions(Color.fromRGB(80, 80, 80), 1.0f);
        Particle.DustOptions activeTrue = new Particle.DustOptions(Color.fromRGB(255, 0, 0), 1.0f);

        if (targetBlock == null || !targetBlock.getType().isSolid()) {
            world.playSound(player.getLocation().clone(), Sound.ITEM_ARMOR_EQUIP_LEATHER, 1f, 1f);
            cool.updateCooldown(player, "Q", 500L);
            return;
        }

        world.playSound(targetBlock.getLocation().clone(), Sound.BLOCK_ANVIL_USE, 1f, 1f);

        Location particleLoc = targetBlock.getLocation().add(0.5, 1.1, 0.5);
        world.spawnParticle(Particle.DUST, particleLoc, 60, 0.7, 0, 0.7, 0.08, activeFalse);

        config.trapPedalPos.remove(player.getUniqueId());
        config.trapPedalPos.put(player.getUniqueId(), particleLoc);

        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                if (!player.isOnline() || player.isDead() || tick > 240 || !config.trapPedalPos.containsValue(particleLoc)) {
                    config.trapPedalPos.remove(player.getUniqueId(), particleLoc);
                    cancel();
                    return;
                }

                if(tick > 26) {
                    player.spawnParticle(Particle.DUST, particleLoc, 30, 0.7, 0, 0.7, 0.08, activeTrue);
                }

                List<Entity> nearbyEntities = (List<Entity>) particleLoc.getNearbyEntities(1.5, 0.4, 1.5);
                for (Entity entity : nearbyEntities) {
                    if (entity instanceof LivingEntity target && entity != player && tick > 26) {

                        world.playSound(targetBlock.getLocation().clone(), Sound.BLOCK_ANVIL_PLACE, 1f, 1f);

                        world.spawnParticle(Particle.SPIT, target.getLocation().clone().add(0, 0.2, 0), 20, 0.2, 0.3, 0.2, 0.5);
                        target.setVelocity(new Vector(0, 0, 0));

                        if(!config.trapThrowPos.getOrDefault(player.getUniqueId(), new ArrayList<>()).isEmpty()) {
                            config.trapTarget.put(player.getUniqueId(), target);
                        }

                        config.trapPedalPos.remove(player.getUniqueId(), particleLoc);

                        trapActive(player);

                        cancel();
                        return;
                    }
                }

                tick += 2;
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    public void trapActive(Player player){

        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                config.trapActive.put(player.getUniqueId(), true);

                if (!player.isOnline() || player.isDead() || tick > 80 || !config.trapActive.getOrDefault(player.getUniqueId(), false)) {
                    config.trapActive.remove(player.getUniqueId());
                    cancel();
                    return;
                }

                tick++;
            }
        }.runTaskTimer(plugin, 0L, 1L);

    }
}
