package me.vark123.dsrpg.rpgCombat.logic.calculators;

import me.vark123.dsrpg.rpgCombat.config.RpgCombatConfig;
import me.vark123.dsrpg.rpgCombat.logic.*;
import me.vark123.dsrpg.rpgStats.statLogic.RpgStatsHolder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jspecify.annotations.Nullable;

public class MeleeCombatCalculator extends ACombatCalculator {

    @Override
    protected RpgDamageData calculateEntityDamage(EntityDamageEvent event, Entity attacker, Entity victim, RpgStatsHolder attackerStats, RpgStatsHolder victimStats) {
        var config = RpgCombatConfig.getInstance();

        var damageType = CombatManager.resolveDamageStatType(event, RpgDamageType.MELEE);
        double dmg = attackerStats.getStat(damageType.dmgStat()).getTotalValue();
        var statSum = 0;
        double def = victimStats.getStat(damageType.defStat()).getTotalValue();
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
                def = victimStats.getStat(damageType.defStat()).getTotalValue();
            }
        }

        var dmgModifier = isCrit ?
                config.getCritModifier(RpgDamageType.MELEE) :
                config.getNormalModifier(RpgDamageType.MELEE);
        var calcDmg = (int) Math.max(Math.round((dmg + statSum - def) * dmgModifier), config.getMinimumDamage());
        return new RpgDamageData(calcDmg, isCrit);
    }

    @Override
    protected RpgDamageData calculateEnvironmentalDamage(EntityDamageEvent event, @Nullable Entity victim, RpgStatsHolder victimStats) {
        var config = RpgCombatConfig.getInstance();

        var damageType = CombatManager.resolveDamageStatType(event, RpgDamageType.CUSTOM);
        var dmg = event.getDamage();
        var def = victimStats.getStat(damageType.defStat()).getTotalValue();

        var calcDmg = (int) Math.max(Math.round((dmg - def) * config.getNormalModifier(RpgDamageType.MELEE)), config.getMinimumDamage());
        return new RpgDamageData(calcDmg);
    }
}
