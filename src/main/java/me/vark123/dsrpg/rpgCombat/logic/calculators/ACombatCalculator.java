package me.vark123.dsrpg.rpgCombat.logic.calculators;

import io.lumine.mythic.api.skills.damage.DamageMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import me.vark123.dsrpg.rpgCombat.config.RpgCombatConfig;
import me.vark123.dsrpg.rpgCombat.config.RpgCombatConfig.DamageTypeData;
import me.vark123.dsrpg.rpgCombat.logic.CombatManager;
import me.vark123.dsrpg.rpgCombat.logic.RpgDamageData;
import me.vark123.dsrpg.rpgStats.statLogic.RpgEntityStatManager;
import me.vark123.dsrpg.rpgStats.statLogic.RpgStatsHolder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

public abstract class ACombatCalculator implements ICombatCalculator {

    protected final RpgEntityStatManager statManager = RpgEntityStatManager.getInstance();

    @Override
    public final RpgDamageData calculate(EntityDamageEvent event, @Nullable Entity attacker, @Nullable Entity victim) {
        if (victim == null)
            return new RpgDamageData();

        var victimStats = statManager.getStats(victim.getUniqueId());

        if (attacker == null)
            return calculateEnvironmentalDamage(event, victim, victimStats);

        var attackerStats = statManager.getStats(attacker.getUniqueId());
        return calculateEntityDamage(event, attacker, victim, attackerStats, victimStats);
    }

    protected abstract RpgDamageData calculateEntityDamage(
            EntityDamageEvent event,
            Entity attacker,
            Entity victim,
            RpgStatsHolder attackerStats,
            RpgStatsHolder victimStats
    );

    protected abstract RpgDamageData calculateEnvironmentalDamage(
            EntityDamageEvent event,
            @Nullable Entity victim,
            RpgStatsHolder victimStats
    );

    protected int calculateStatSum(RpgStatsHolder attackerStats, ItemStack weapon) {
        var config = RpgCombatConfig.getInstance();
        var weaponType = CombatManager.resolveWeaponType(weapon);
        if (weaponType.equals(config.getUndefinedWeaponType()))
            return 0;

        var proportions = weaponType.statProportions();
        if (proportions.size() > 1) {
            int totalStatSum = 0;
            for (String statKey : weaponType.statProportions().keySet()) {
                var stat = attackerStats.getStat(statKey);
                if (stat != null)
                    totalStatSum += stat.getTotalValue();
            }

            double maxErrorSum = 0;
            for (double val : weaponType.statProportions().values()) {
                maxErrorSum += val * val;
            }
            double maxError = Math.sqrt(maxErrorSum);

            double currentStatSum = 0;
            double defectionSum = 0;
            for (var entry : weaponType.statProportions().entrySet()) {
                var statData = attackerStats.getStat(entry.getKey());
                if (statData == null)
                    continue;

                int statValue = statData.getTotalValue();
                double targetRatio = entry.getValue();

                double currentRatio = totalStatSum > 0 ? (double) statValue / totalStatSum : 0;
                defectionSum += Math.pow(currentRatio - targetRatio, 2);

                currentStatSum += statValue * targetRatio;
            }

            double defection = Math.sqrt(defectionSum);
            double boost = Math.max(1.0, 1.5 - (defection / maxError));
            return (int) (currentStatSum * boost);
        } else {
            double currentStatSum = 0;
            for (var entry : weaponType.statProportions().entrySet()) {
                var statData = attackerStats.getStat(entry.getKey());
                if (statData != null) {
                    currentStatSum += statData.getTotalValue() * entry.getValue();
                }
            }
            return (int) currentStatSum;
        }
    }

    protected DamageTypeData resolveMythicSkillDamageType(Entity attacker, Entity victim, DamageTypeData fallback) {
        var config = RpgCombatConfig.getInstance();

        var aAttacker = BukkitAdapter.adapt(attacker);
        var aVictim = BukkitAdapter.adapt(victim);

        if (aAttacker.hasMetadata("doing-skill-damage") && aVictim.hasMetadata("skill-damage")) {
            if (aVictim.getMetadata("skill-damage").get() instanceof DamageMetadata data) {
                var skillDamageType = data.getElement();
                if (skillDamageType != null) {
                    return config.getDamageTypeData(skillDamageType);
                }
            }
        }
        return fallback;
    }

}
