package org.core.coreProgram.Cores.Benzene.coreSystem;

import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.core.Level.LevelingManager;
import org.core.Level.Levels;

import java.util.Map;
import java.util.Set;

public class benzLeveling implements Levels {

    private final JavaPlugin plugin;
    private final Player player;
    private final long exp;

    public benzLeveling(JavaPlugin plugin, Player player, long exp) {
        this.plugin = plugin;
        this.player = player;
        this.exp = exp;
    }

    public Set<Long> requireExp = Set.of(111L, 333L, 666L, 1111L, 1661L, 2222L, 2992L, 3993L, 5115L, 6666L);

    @Override
    public void addLV(Entity entity){

        long current = player.getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin, "level"), PersistentDataType.LONG, 0L);
        player.getPersistentDataContainer().set(new NamespacedKey(plugin, "level"), PersistentDataType.LONG, current + 1);

        long updatedLevel = player.getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin, "level"), PersistentDataType.LONG, 0L);

        AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealth != null) {
            maxHealth.setBaseValue(maxHealth.getBaseValue() + 2.0);

            player.setHealth(maxHealth.getBaseValue());
        }

        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.5f, 1.0f);
        player.sendMessage("§a" + "level " + updatedLevel + " 로 상승!");

    }

    @Override
    public void addExp(Entity entity){

        long currentExp = 0L;
        long currentLevel = player.getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin, "level"), PersistentDataType.LONG, 0L);

        if (currentLevel < requireExp.size()) {
            player.sendMessage("§e" + "exp : " + exp + " 획득");
        }

        for (int i = 0; i < exp; i++) {
            if (currentLevel < requireExp.size()) {
                currentExp = player.getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin, "exp"), PersistentDataType.LONG, 0L);
                player.getPersistentDataContainer().set(new NamespacedKey(plugin, "exp"), PersistentDataType.LONG, currentExp + 1);
                if (requireExp.contains(player.getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin, "exp"), PersistentDataType.LONG, 0L))) {
                    addLV(player);
                }
            }else{
                break;
            }
        }
    }
}
