package me.vark123.dsrpg.rpgCombat.logic.calculators;

import io.lumine.mythic.bukkit.BukkitAdapter;
import me.vark123.dsrpg.rpgCombat.config.RpgCombatConfig;
import me.vark123.dsrpg.rpgCombat.logic.*;
import me.vark123.dsrpg.rpgStats.statLogic.RpgStatsHolder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

import static me.vark123.dsrpg.rpgCombat.config.RpgCombatConstants.*;

public class RangedCombatCalculator extends ACombatCalculator {

    @Override
    protected RpgDamageData calculateEntityDamage(EntityDamageEvent event, Entity attacker, Entity victim, RpgStatsHolder attackerStats, RpgStatsHolder victimStats) {
        var config = RpgCombatConfig.getInstance();

        var force = 1.0f;
        var projectile = event.getDamageSource().getDirectEntity();
        var aProjectile = BukkitAdapter.adapt(projectile);
        if(aProjectile != null && aProjectile.hasMetadata(METADATA_BOW_FORCE_KEY)) {
            force = (float) aProjectile.getMetadata(METADATA_BOW_FORCE_KEY).get();
        }

        var damageType = CombatManager.resolveDamageStatType(event, RpgDamageType.RANGED);
        double dmg = attackerStats.getStat(damageType.dmgStat()).getTotalValue();
        var statSum = 0;
        double def = victimStats.getStat(damageType.defStat()).getTotalValue();
        boolean isCrit = false;

        if (attacker instanceof Player player) {
            if(aProjectile.hasMetadata(METADATA_BOW_ITEM_KEY)){
                ItemStack weapon = (ItemStack) aProjectile.getMetadata(METADATA_BOW_ITEM_KEY).get();
                dmg += CombatManager.getWeaponDamage(weapon);
                statSum = calculateStatSum(attackerStats, weapon);

                isCrit = CombatManager.checkCrit(attacker, victim, (EntityDamageByEntityEvent) event, RpgDamageType.RANGED, CombatManager.resolveWeaponType(weapon));
            }
        } else {
            damageType = resolveMythicSkillDamageType(attacker, victim, damageType);
            if (damageType != CombatManager.resolveDamageStatType(event, RpgDamageType.RANGED)) {
                dmg = event.getDamage();
                def = victimStats.getStat(damageType.defStat()).getTotalValue();
            }
        }

        var dmgModifier = isCrit ?
                config.getCritModifier(RpgDamageType.RANGED) :
                config.getNormalModifier(RpgDamageType.RANGED);
        var calcDmg = (int) Math.max(Math.round(((dmg + statSum) * force - def) * dmgModifier), config.getMinimumDamage());
        return new RpgDamageData(calcDmg, isCrit);
    }

    @Override
    protected RpgDamageData calculateEnvironmentalDamage(EntityDamageEvent event, @Nullable Entity victim, RpgStatsHolder victimStats) {
        var config = RpgCombatConfig.getInstance();

        var damageType = CombatManager.resolveDamageStatType(event, RpgDamageType.CUSTOM);
        var dmg = event.getDamage();
        var def = victimStats.getStat(damageType.defStat()).getTotalValue();

        var calcDmg = (int) Math.max(Math.round((dmg - def) * config.getNormalModifier(RpgDamageType.RANGED)), config.getMinimumDamage());
        return new RpgDamageData(calcDmg);
    }
}
