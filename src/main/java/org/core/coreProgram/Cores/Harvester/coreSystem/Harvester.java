package org.core.coreProgram.Cores.Harvester.coreSystem;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class Harvester {

    public HashMap<UUID, Long> R_COOLDOWN = new HashMap<>();
    public HashMap<UUID, Long> Q_COOLDOWN = new HashMap<>();
    public HashMap<UUID, Long> F_COOLDOWN = new HashMap<>();

    public long frozenCool = 10000;

    //R
    public long r_Skill_Cool = 600;

    //Q
    public long q_Skill_Cool = 10000;

    //F
    public long f_Skill_Cool = 100000;

    public void variableReset(Player player) {

    }
}
