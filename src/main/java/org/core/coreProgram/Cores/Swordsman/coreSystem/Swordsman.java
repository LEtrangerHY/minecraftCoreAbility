package org.core.coreProgram.Cores.Swordsman.coreSystem;

import it.unimi.dsi.fastutil.Hash;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class Swordsman {
    //CoolHashmap
    public HashMap<UUID, Long> R_COOLDOWN = new HashMap<>();
    public HashMap<UUID, Long> Q_COOLDOWN = new HashMap<>();
    public HashMap<UUID, Long> F_COOLDOWN = new HashMap<>();

    public long frozenCool = 10000;

    //passive
    public HashMap<UUID, Boolean> skillUsing = new HashMap<>();
    public HashMap<UUID, Boolean> laidoSlash = new HashMap<>();

    //R
    public long r_Skill_Cool = 550;
    public double r_Skill_damage = 3;
    public double r_Skill_amp = 0.2;
    public HashMap<UUID, HashSet<Entity>> r_damaged = new HashMap<>();

    //Q
    public long q_Skill_Cool = 5500;
    public double q_Skill_damage = 4;
    public double q_Skill_amp = 0.1;
    public HashMap<UUID, HashSet<Entity>> q_damaged = new HashMap<>();

    //F
    public long f_Skill_Cool = 15500;
    public double f_Skill_amp = 0.2;

    public void variableReset(Player player){

        R_COOLDOWN.remove(player.getUniqueId());
        Q_COOLDOWN.remove(player.getUniqueId());
        F_COOLDOWN.remove(player.getUniqueId());

    }
}