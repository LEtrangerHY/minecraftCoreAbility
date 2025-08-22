package org.core.coreProgram.Cores.Blaze.Skill;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.core.Cool.Cool;
import org.core.coreProgram.Abs.SkillBase;
import org.core.coreProgram.Cores.Blaze.Passive.BlueFlame;
import org.core.coreProgram.Cores.Blaze.coreSystem.Blaze;

public class R implements SkillBase {
    private final Blaze config;
    private final JavaPlugin plugin;
    private final Cool cool;
    private final BlueFlame blueFlame;

    public R(Blaze config, JavaPlugin plugin, Cool cool, BlueFlame blueFlame) {
        this.config = config;
        this.plugin = plugin;
        this.cool = cool;
        this.blueFlame = blueFlame;
    }

    @Override
    public void Trigger(Player player) {
        World world = player.getWorld();

        new BukkitRunnable(){
            int tick = 0;

            @Override
            public void run(){
                if(tick > 10){

                    cancel();
                    return;
                }

                tick++;
            }

        }.runTaskTimer(plugin, 0L, 1L);

    }
}
