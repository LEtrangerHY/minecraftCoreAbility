package org.core.coreProgram.Cores.Harvester.Skill;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.core.Cool.Cool;
import org.core.coreProgram.AbsCoreSystem.SkillBase;
import org.core.coreProgram.Cores.Harvester.coreSystem.Harvester;

public class Q implements SkillBase {

    public final Harvester config;
    private final JavaPlugin plugin;
    private final Cool cool;

    public Q(Harvester config, JavaPlugin plugin, Cool cool){
        this.config = config;
        this.plugin = plugin;
        this.cool = cool;
    }

    @Override
    public void Trigger(Player player) {

    }
}
