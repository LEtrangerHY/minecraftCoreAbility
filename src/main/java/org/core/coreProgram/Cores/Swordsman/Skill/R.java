package org.core.coreProgram.Cores.Swordsman.Skill;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.core.Cool.Cool;
import org.core.coreProgram.AbsCoreSystem.SkillBase;
import org.core.coreProgram.Cores.Swordsman.coreSystem.Swordsman;

public class R implements SkillBase {
    private final Swordsman config;
    private final JavaPlugin plugin;
    private final Cool cool;

    public R(Swordsman config, JavaPlugin plugin, Cool cool) {
        this.config = config;
        this.plugin = plugin;
        this.cool = cool;
    }

    @Override
    public void Trigger(Player player){

    }
}