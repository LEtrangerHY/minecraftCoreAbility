package org.core.coreProgram.Cores.Harvester.Passive;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.core.Cool.Cool;
import org.core.Effect.EffectManager;
import org.core.coreConfig;
import org.core.coreProgram.Cores.Harvester.coreSystem.Harvester;

public class Bountiful {

    private final coreConfig tag;
    private final Harvester config;
    private final JavaPlugin plugin;
    private final Cool cool;
    private EffectManager effectManager = new EffectManager();

    public Bountiful(coreConfig tag, Harvester config, JavaPlugin plugin, Cool cool) {
        this.tag = tag;
        this.config = config;
        this.plugin = plugin;
        this.cool = cool;
    }

    public boolean bushCheck(Player player){
        return false;
    }
}
