package org.core.coreProgram.Cores.VOL2.Burst.Skill;

import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.core.Cool.Cool;
import org.core.Effect.ForceDamage;
import org.core.Effect.Invulnerable;
import org.core.coreProgram.AbsCoreSystem.SkillBase;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.core.coreProgram.Cores.VOL2.Burst.coreSystem.Burst;

public class F implements SkillBase {
    private final Burst config;
    private final JavaPlugin plugin;
    private final Cool cool;

    public F(Burst config, JavaPlugin plugin, Cool cool) {
        this.config = config;
        this.plugin = plugin;
        this.cool = cool;
    }

    @Override
    public void Trigger(Player player){
        World world = player.getWorld();

        world.spawnParticle(Particle.EXPLOSION, player.getLocation().clone().add(0, 1, 0), 4, 0.3, 0.3, 0.3, 1);
        world.playSound(player.getLocation().clone(), Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 1.0f);

        Vector upward = new Vector(0, config.f_Skill_Jump, 0);

        player.setVelocity(upward);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.setVelocity(new Vector(0, 0, 0));
            SaturationBomb(player);
        }, 6L);
    }

    public void SaturationBomb(Player player){
        player.swingMainHand();

        Location startLocation = player.getLocation();

        Vector direction = startLocation.getDirection().normalize().multiply(config.f_Skill_dash);

        player.setVelocity(direction);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, 1.0f);

        Invulnerable invulnerable = new Invulnerable(player, 600);
        invulnerable.applyEffect(player);
    }
}
