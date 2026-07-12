package me.vark123.dsrpg.rpgCombat.logic.calculators;

import io.lumine.mythic.api.skills.damage.DamageMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import me.vark123.dsrpg.rpgCombat.logic.*;
import me.vark123.dsrpg.rpgStats.statLogic.RpgEntityStatManager;
import me.vark123.dsrpg.rpgStats.statLogic.RpgStatsHolder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jspecify.annotations.Nullable;

public class MeleeCombatCalculator extends ACombatCalculator {

    protected static final double NORMAL_DAMAGE_MODIFIER = 0.25;
    protected static final double CRIT_DAMAGE_MODIFIER = 1;

    @Override
    protected RpgDamageData calculateEntityDamage(EntityDamageEvent event, Entity attacker, Entity victim, RpgStatsHolder attackerStats, RpgStatsHolder victimStats) {
        var damageType = CombatManager.resolveDamageStatType(event, RpgDamageType.MELEE);
        double dmg = attackerStats.getStat(damageType.getDmgStat()).getTotalValue();
        var statSum = 0;
        double def = victimStats.getStat(damageType.getDefStat()).getTotalValue();
        boolean isCrit = false;

        if (attacker instanceof Player player) {
            var weapon = player.getEquipment().getItemInMainHand();
            dmg += CombatManager.getWeaponDamage(weapon);
            statSum = calculateStatSum(attackerStats, weapon);

            isCrit = CombatManager.checkCrit(attacker, victim, (EntityDamageByEntityEvent) event, RpgDamageType.MELEE, CombatManager.resolveWeaponType(weapon));
        } else {
            damageType = resolveMythicSkillDamageType(attacker, victim, damageType);
            if (damageType != CombatManager.resolveDamageStatType(event, RpgDamageType.MELEE)) {
                dmg = event.getDamage();
                def = victimStats.getStat(damageType.getDefStat()).getTotalValue();
            }
        }

        var dmgModifier = isCrit ? CRIT_DAMAGE_MODIFIER : NORMAL_DAMAGE_MODIFIER;
        var calcDmg = (int) Math.max(Math.round((dmg + statSum - def) * dmgModifier), MINIMUM_DAMAGE_VALUE);
        return new RpgDamageData(calcDmg, isCrit);
    }

    @Override
    protected RpgDamageData calculateEnvironmentalDamage(EntityDamageEvent event, @Nullable Entity victim, RpgStatsHolder victimStats) {
        var damageType = CombatManager.resolveDamageStatType(event, RpgDamageType.CUSTOM);
        var dmg = event.getDamage();
        var def = victimStats.getStat(damageType.getDefStat()).getTotalValue();

        var calcDmg = (int) Math.max(Math.round((dmg - def) * NORMAL_DAMAGE_MODIFIER), MINIMUM_DAMAGE_VALUE);
        return new RpgDamageData(calcDmg);
    }
}
