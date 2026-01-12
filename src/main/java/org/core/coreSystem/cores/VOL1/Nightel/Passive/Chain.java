package org.core.coreSystem.cores.VOL1.Nightel.Passive;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.core.cool.Cool;
import org.core.main.coreConfig;
import org.core.coreSystem.cores.VOL1.Nightel.coreSystem.Nightel;

public class Chain implements Listener {

    private final Nightel config;
    private final JavaPlugin plugin;
    private final Cool cool;
    private final coreConfig tag;

    public Chain(Nightel config, coreConfig tag, JavaPlugin plugin, Cool cool) {
        this.config = config;
        this.plugin = plugin;
        this.cool = cool;
        this.tag = tag;
    }

    public void chainCount(Player player, long coolTime, String skill) {
        int point = config.chainCount.getOrDefault(player.getUniqueId(), 0);

        long cools = (long) Math.min(coolTime * Math.pow(2.2, point), 6000);
        cool.updateCooldown(player, skill, cools);

        if(point < 6) {

            World world = player.getWorld();
            Location playerLoc = player.getLocation();
            BlockData chain = Material.IRON_CHAIN.createBlockData();

            int addPoint = config.chainSkill.containsKey(player.getUniqueId()) &&
                    ((skill.equals("R") && !config.chainSkill.getOrDefault(player.getUniqueId(), "").equals(skill))
                    || (skill.equals("Q") && config.chainSkill.getOrDefault(player.getUniqueId(), "").equals(skill))) ? 2 : 1;

            for(int i = 0; i < addPoint; i++) {
                if(config.chainCount.getOrDefault(player.getUniqueId(), 0) < 6) {
                    config.chainCount.put(player.getUniqueId(), config.chainCount.getOrDefault(player.getUniqueId(), 0) + 1);
                }else{
                    break;
                }
            }

            world.playSound(playerLoc, Sound.BLOCK_CHAIN_PLACE, 1.6f, 1.0f);
            world.spawnParticle(Particle.BLOCK, playerLoc.clone().add(0, 1.2, 0), 33, 0.6, 0.6, 0.6, chain);
            if(addPoint == 2) world.spawnParticle(Particle.ENCHANTED_HIT, playerLoc.clone().add(0, 1.2, 0), 33, 0.6, 0.6, 0.6, 1);

            if (config.chainCount.getOrDefault(player.getUniqueId(), 0) < 6) {
                int displayCount = config.chainCount.getOrDefault(player.getUniqueId(), 0);
                String hex = "⬡ ".repeat(displayCount).trim();
                player.sendActionBar(Component.text(hex).color(NamedTextColor.GRAY));
            } else {
                hexaChainLoad(player);
            }
        }

        config.chainSkill.put(player.getUniqueId(), skill);
    }

    public void hexaChainLoad(Player player){

        World world = player.getWorld();

        world.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.6f, 1.0f);

        player.setWalkSpeed((float) 0.2 * ((float) 4/3));

        new BukkitRunnable(){
            int tick = 0;

            @Override
            public void run(){

                if(tick > 60 || player.isDead() || config.fskill_using.getOrDefault(player.getUniqueId(), false)){
                    player.setWalkSpeed((float) 0.2);
                    removePoint(player);
                    cancel();
                    return;
                }

                String hex = "⌬ ".repeat(6 - tick / 10).trim();
                player.sendActionBar(Component.text(hex).color(NamedTextColor.DARK_GRAY));

                tick++;

                Location playerLoc = player.getLocation().clone().add(0, 1.2, 0);

                if(tick % 10 == 0) {
                    world.spawnParticle(Particle.ENCHANTED_HIT, playerLoc, 22, 0.6, 0.6, 0.6, 1);
                    world.playSound(playerLoc, Sound.BLOCK_CHAIN_BREAK, 1.2f, 1.0f);
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    public void removePoint(Player player){

        long cools = 0;
        cool.updateCooldown(player, "R", cools);
        cool.updateCooldown(player, "Q", cools);

        long updatedCool = Math.max(config.chainCount.getOrDefault(player.getUniqueId(), 0) * 1000, 600);
        cool.updateCooldown(player, "F", updatedCool);

        config.chainCount.remove(player.getUniqueId());
        config.chainSkill.remove(player.getUniqueId());

        player.sendActionBar(Component.text("⌬").color(NamedTextColor.GRAY));

    }
}

