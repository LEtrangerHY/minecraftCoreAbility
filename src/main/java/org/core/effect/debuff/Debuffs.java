package org.core.effect.debuff;

import org.bukkit.entity.Entity;

public interface Debuffs {
    void applyEffect(Entity entity);

    void removeEffect(Entity entity);

}
