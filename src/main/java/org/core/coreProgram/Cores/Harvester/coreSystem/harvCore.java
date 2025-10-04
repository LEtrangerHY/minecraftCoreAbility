package org.core.coreProgram.Cores.Harvester.coreSystem;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.core.Cool.Cool;
import org.core.Core;
import org.core.coreConfig;
import org.core.coreProgram.AbsCoreSystem.ConfigWrapper;
import org.core.coreProgram.AbsCoreSystem.SkillBase;
import org.core.coreProgram.AbsCoreSystem.absCore;
import org.core.coreProgram.Cores.Harvester.Passive.Bountiful;
import org.core.coreProgram.Cores.Harvester.coreSystem.Harvester;
import org.core.coreProgram.Cores.Harvester.Skill.F;
import org.core.coreProgram.Cores.Harvester.Skill.Q;
import org.core.coreProgram.Cores.Harvester.Skill.R;

import static org.bukkit.Bukkit.getLogger;

public class harvCore extends absCore {

    private final Core plugin;
    private final Harvester config;

    private final Bountiful bountiful;

    private final R Rskill;
    private final Q Qskill;
    private final F Fskill;

    public harvCore(Core plugin, coreConfig tag, Harvester config, Cool cool) {
        super(tag, cool);

        this.plugin = plugin;
        this.config = config;

        this.bountiful = new Bountiful(tag, config, plugin, cool);

        this.Rskill = new R(config, plugin, cool);
        this.Qskill = new Q(config, plugin, cool);
        this.Fskill = new F(config, plugin, cool);


        getLogger().info("Harvester downloaded...");
    }

    @EventHandler
    public void invisibleInBush(PlayerMoveEvent event){
        Player player = event.getPlayer();

        if(bountiful.bushCheck(player)) {
            player.setInvisible(true);
        }

    }

    @Override
    protected boolean contains(Player player) {
        return tag.Harvester.contains(player);
    }

    @Override
    protected SkillBase getRSkill() {
        return Rskill;
    }

    @Override
    protected SkillBase getQSkill() {
        return Qskill;
    }

    @Override
    protected SkillBase getFSkill() {
        return Fskill;
    }

    private boolean hasProperItems(Player player) {
        ItemStack main = player.getInventory().getItemInMainHand();
        ItemStack off = player.getInventory().getItemInOffHand();
        return main.getType() == Material.IRON_HOE && off.getType() == Material.IRON_HOE;
    }

    private boolean canUseRSkill(Player player) { return true; }

    private boolean canUseQSkill(Player player) { return true; }

    private boolean canUseFSkill(Player player) { return true; }

    @Override
    protected boolean isItemRequired(Player player){
        return hasProperItems(player);
    }

    @Override
    protected boolean isRCondition(Player player) {
        return canUseRSkill(player);
    }

    @Override
    protected boolean isQCondition(Player player, ItemStack droppedItem) {
        ItemStack off = player.getInventory().getItemInOffHand();
        return droppedItem.getType() == Material.IRON_HOE &&
                off.getType() == Material.AIR &&
                canUseQSkill(player);
    }

    @Override
    protected boolean isFCondition(Player player) {
        return canUseFSkill(player);
    }

    @Override
    protected ConfigWrapper getConfigWrapper() {
        return new ConfigWrapper() {
            @Override
            public void variableReset(Player player) {
                config.variableReset(player);
            }

            @Override
            public void cooldownReset(Player player) {
                cool.updateCooldown(player, "R", config.frozenCool);
                cool.updateCooldown(player, "Q", config.frozenCool);
                cool.updateCooldown(player, "F", config.frozenCool);
            }

            @Override
            public long getRcooldown(Player player) {
                return config.R_COOLDOWN.getOrDefault(player.getUniqueId(), config.r_Skill_Cool);
            }

            @Override
            public long getQcooldown(Player player) {
                return config.Q_COOLDOWN.getOrDefault(player.getUniqueId(), config.q_Skill_Cool);
            }

            @Override
            public long getFcooldown(Player player) {
                return config.F_COOLDOWN.getOrDefault(player.getUniqueId(), config.f_Skill_Cool);
            }
        };
    }
}
