package org.core.coreProgram.Cores.Saboteur.Skill;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.core.Cool.Cool;
import org.core.Effect.ForceDamage;
import org.core.Effect.Stun;
import org.core.coreProgram.AbsCoreSystem.SkillBase;
import org.core.coreProgram.Cores.Saboteur.coreSystem.Saboteur;

import java.util.ArrayList;
import java.util.List;

public class R implements SkillBase {
    private final Saboteur config;
    private final JavaPlugin plugin;
    private final Cool cool;

    public R(Saboteur config, JavaPlugin plugin, Cool cool) {
        this.config = config;
        this.plugin = plugin;
        this.cool = cool;
    }

    @Override
    public void Trigger(Player player) {
        World world = player.getWorld();
        Particle.DustOptions poisonGreen = new Particle.DustOptions(Color.fromRGB(64, 253, 20), 1.0f);
        Particle.DustOptions activeFalse = new Particle.DustOptions(Color.fromRGB(80, 80, 80), 1.0f);

        if(config.trapType.getOrDefault(player.getUniqueId(), 1) == 2) {
            Block targetBlock = player.getTargetBlockExact(24, FluidCollisionMode.ALWAYS);

            if (targetBlock == null || !targetBlock.getType().isSolid()) {
                world.playSound(player.getLocation().clone(), Sound.ITEM_ARMOR_EQUIP_LEATHER, 1f, 1f);
                cool.updateCooldown(player, "R", 500L);
                return;
            }

            Location particleLoc = targetBlock.getLocation().add(0.5, 1.5, 0.5);
            config.trapThrowPos.putIfAbsent(player.getUniqueId(), new ArrayList<>());

            List<Location> list = config.trapThrowPos.get(player.getUniqueId());
            if (list.contains(particleLoc)) {
                list.remove(particleLoc);
                player.sendActionBar(Component.text("Trap removed : throw").color(NamedTextColor.YELLOW));
                world.playSound(player.getLocation().clone(), Sound.ITEM_ARMOR_EQUIP_LEATHER, 1f, 1f);
                cool.updateCooldown(player, "R", 500L);
                return;
            }

            player.sendActionBar(Component.text("Trap set! : throw").color(NamedTextColor.GREEN));

            player.playSound(targetBlock.getLocation().clone(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
            if(list.size() < 4) {
                list.add(particleLoc);
            }else{
                list.removeFirst();
                list.add(particleLoc);
            }

            new BukkitRunnable() {

                @Override
                public void run() {
                    if (!player.isOnline() || player.isDead() || !config.trapThrowPos.get(player.getUniqueId()).contains(particleLoc)) {
                        config.trapThrowPos.get(player.getUniqueId()).remove(particleLoc);
                        cancel();
                        return;
                    }

                    if (config.trapActive.getOrDefault(player.getUniqueId(), false)) {

                        LivingEntity target = config.trapTarget.get(player.getUniqueId());
                        if (target != null && target.isValid() && !target.isDead()) {
                            trapThrowActive(player, particleLoc.clone(), target);
                        }

                        config.trapThrowPos.get(player.getUniqueId()).remove(particleLoc);
                        cancel();
                        return;
                    }

                    player.spawnParticle(Particle.DUST, particleLoc, 4, 0.2, 0.2, 0.2, 0.08, poisonGreen);
                    player.spawnParticle(Particle.ENCHANTED_HIT, particleLoc, 6, 0.3, 0.3, 0.3, 0);
                }
            }.runTaskTimer(plugin, 0L, 2L);

        }else{
            Block targetBlock = player.getTargetBlockExact(8, FluidCollisionMode.ALWAYS);

            if (targetBlock == null || !targetBlock.getType().isSolid()) {
                world.playSound(player.getLocation().clone(), Sound.ITEM_ARMOR_EQUIP_LEATHER, 1f, 1f);
                cool.updateCooldown(player, "R", 500L);
                return;
            }

            Location particleLoc = targetBlock.getLocation().add(0.5, 1.1, 0.5);
            config.trapSpikePos.putIfAbsent(player.getUniqueId(), new ArrayList<>());

            List<Location> list = config.trapSpikePos.get(player.getUniqueId());
            if (list.contains(particleLoc)) {
                list.remove(particleLoc);
                player.sendActionBar(Component.text("Trap removed : spike").color(NamedTextColor.YELLOW));
                world.playSound(player.getLocation().clone(), Sound.ITEM_ARMOR_EQUIP_LEATHER, 1f, 1f);
                cool.updateCooldown(player, "R", 500L);
                return;
            }

            player.sendActionBar(Component.text("Trap set! : spike").color(NamedTextColor.GREEN));

            player.playSound(targetBlock.getLocation().clone(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
            if(list.size() < 3) {
                list.add(particleLoc);
            }else{
                list.removeFirst();
                list.add(particleLoc);
            }

            BlockData blood = Material.REDSTONE_BLOCK.createBlockData();

            new BukkitRunnable() {

                int tick = 0;

                @Override
                public void run() {
                    if (!player.isOnline() || player.isDead() || !config.trapSpikePos.get(player.getUniqueId()).contains(particleLoc)) {
                        config.trapSpikePos.get(player.getUniqueId()).remove(particleLoc);
                        cancel();
                        return;
                    }

                    if(tick >= 8) {
                        List<Entity> nearbyEntities = (List<Entity>) particleLoc.getNearbyEntities(3.5, 0.7, 3.5);

                        if (nearbyEntities.isEmpty()) {
                            config.trapSpikeDamage.remove(player.getUniqueId());
                        } else {
                            config.trapSpikeDamage.put(player.getUniqueId(), true);
                        }

                        if(!config.trapSpikeDamage.containsKey(player.getUniqueId())) {
                            player.spawnParticle(Particle.ENCHANTED_HIT, particleLoc, 30, 1.5, 0, 1.5, 0);
                            if(config.trapActive.getOrDefault(player.getUniqueId(), false)){
                                player.spawnParticle(Particle.DUST, particleLoc, 10, 1.5, 0, 1.5, 0.08, poisonGreen);
                            }
                        }else{
                            world.spawnParticle(Particle.ENCHANTED_HIT, particleLoc, 30, 1.5, 0, 1.5, 0);
                            if(config.trapActive.getOrDefault(player.getUniqueId(), false)){
                                world.spawnParticle(Particle.DUST, particleLoc, 10, 1.5, 0, 1.5, 0.08, poisonGreen);
                            }
                        }

                        player.spawnParticle(Particle.DUST, particleLoc, 4, 0.2, 0.2, 0.2, 0.08, poisonGreen);

                        for (Entity entity : nearbyEntities) {
                            if (entity instanceof LivingEntity target && entity != player) {

                                if (tick % 10 == 0) {
                                    world.playSound(target.getLocation().clone(), Sound.ITEM_TRIDENT_HIT, 1.0f, 1.0f);

                                    world.spawnParticle(Particle.BLOCK, target.getLocation().clone().add(0, 0.1, 0), 13, 0.2, 0.2, 0.2,
                                            blood);

                                    PotionEffect slowness = new PotionEffect(PotionEffectType.SLOWNESS, 10, 2, false, false);
                                    target.addPotionEffect(slowness);

                                    if(config.trapActive.getOrDefault(player.getUniqueId(), false)){
                                        world.playSound(target.getLocation().clone(), Sound.ITEM_HONEY_BOTTLE_DRINK, 1.0f, 1.0f);
                                        PotionEffect poison = new PotionEffect(PotionEffectType.POISON, 260, 5, false, true);
                                        target.addPotionEffect(poison);
                                    }

                                    ForceDamage forceDamage = new ForceDamage(target, 0.6);
                                    forceDamage.applyEffect(player);
                                    target.setVelocity(new Vector(0, 0, 0));
                                }
                            }
                        }
                    }else{
                        player.spawnParticle(Particle.DUST, particleLoc, 4, 0.2, 0.2, 0.2, 0.08, activeFalse);
                    }

                    tick += 2;
                }
            }.runTaskTimer(plugin, 0L, 2L);
        }
    }

    private void trapThrowActive(Player player, Location startLoc, LivingEntity target) {
        World world = player.getWorld();

        Vector direction = target.getLocation().clone().add(0, 1.2, 0).toVector()
                .subtract(startLoc.toVector())
                .normalize()
                .multiply(2.4);

        Item shard = world.dropItem(startLoc, new ItemStack(Material.IRON_NUGGET));
        shard.setVelocity(direction);
        shard.setPickupDelay(1000);
        shard.setGravity(false);

        world.playSound(startLoc, Sound.ITEM_TRIDENT_THROW, 1.0f, 1.0f);

        new BukkitRunnable() {
            int life = 60;

            @Override
            public void run() {
                if (shard.isDead() || !shard.isValid()) {
                    cancel();
                    return;
                }

                Location loc = shard.getLocation();

                world.spawnParticle(Particle.CRIT, loc, 2, 0, 0, 0, 0.01);
                world.spawnParticle(Particle.DUST, loc, 3, 0.05, 0.05, 0.05, 0.05, new Particle.DustOptions(Color.GRAY, 1));

                for (Entity nearby : shard.getNearbyEntities(0.6, 0.6, 0.6)) {
                    if (nearby instanceof LivingEntity hit && nearby != player) {

                        world.playSound(loc, Sound.ITEM_TRIDENT_HIT, 1.0f, 1.0f);
                        world.spawnParticle(Particle.BLOCK, hit.getLocation().add(0, 1.0, 0),
                                14, 0.3, 0.3, 0.3, Material.REDSTONE_BLOCK.createBlockData());

                        PotionEffect poison = new PotionEffect(PotionEffectType.POISON, 260, 5, false, true);
                        hit.addPotionEffect(poison);

                        Stun stun = new Stun(hit, 2000);
                        stun.applyEffect(player);

                        ForceDamage forceDamage = new ForceDamage(hit, 6);
                        forceDamage.applyEffect(player);
                        hit.setVelocity(new Vector(0, 0, 0));

                        shard.remove();
                        cancel();
                        return;
                    }
                }

                if (!loc.clone().add(direction.clone().multiply(0.5)).getBlock().isPassable()) {
                    world.playSound(loc, Sound.ITEM_TRIDENT_HIT_GROUND, 1.0f, 1.0f);
                    world.spawnParticle(Particle.BLOCK, loc, 10, 0.3, 0.3, 0.3, Material.IRON_BLOCK.createBlockData());
                    shard.remove();
                    cancel();
                    return;
                }

                if (life-- <= 0) {
                    shard.remove();
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }


}
