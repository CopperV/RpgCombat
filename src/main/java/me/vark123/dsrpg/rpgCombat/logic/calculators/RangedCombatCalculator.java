package me.vark123.dsrpg.rpgCombat.logic.calculators;

import io.lumine.mythic.api.skills.damage.DamageMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import me.vark123.dsrpg.rpgCombat.logic.*;
import me.vark123.dsrpg.rpgStats.statLogic.RpgStatsHolder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

public class RangedCombatCalculator extends ACombatCalculator {

    protected static final double NORMAL_DAMAGE_MODIFIER = 0.25;
    protected static final double CRIT_DAMAGE_MODIFIER = 1;

    @Override
    protected RpgDamageData calculateEntityDamage(EntityDamageEvent event, Entity attacker, Entity victim, RpgStatsHolder attackerStats, RpgStatsHolder victimStats) {
        var force = 1.0f;
        var projectile = event.getDamageSource().getDirectEntity();
        var aProjectile = BukkitAdapter.adapt(projectile);
        if(aProjectile != null && aProjectile.hasMetadata("force")) {
            force = (float) aProjectile.getMetadata("force").get();
        }

        var damageType = CombatManager.resolveDamageStatType(event, RpgDamageType.RANGED);
        double dmg = attackerStats.getStat(damageType.getDmgStat()).getTotalValue();
        var statSum = 0;
        double def = victimStats.getStat(damageType.getDefStat()).getTotalValue();

        if (attacker instanceof Player player) {
            if(aProjectile.hasMetadata("bow")){
                ItemStack weapon = (ItemStack) aProjectile.getMetadata("bow").get();
                dmg += CombatManager.getWeaponDamage(weapon);
                statSum = calculateStatSum(attackerStats, weapon);
            }
        } else {
            damageType = resolveMythicSkillDamageType(attacker, victim, damageType);
            if (damageType != CombatManager.resolveDamageStatType(event, RpgDamageType.RANGED)) {
                dmg = event.getDamage();
                def = victimStats.getStat(damageType.getDefStat()).getTotalValue();
            }
        }

        var calcDmg = (int) Math.max(Math.round(((dmg + statSum) * force - def) * NORMAL_DAMAGE_MODIFIER), MINIMUM_DAMAGE_VALUE);
        Bukkit.broadcast(Component.text("Test10 RANGED " + dmg + " " + def + " " + calcDmg));
        return new RpgDamageData(calcDmg, false);
    }

    @Override
    protected RpgDamageData calculateEnvironmentalDamage(EntityDamageEvent event, @Nullable Entity victim, RpgStatsHolder victimStats) {
        var damageType = CombatManager.resolveDamageStatType(event, RpgDamageType.CUSTOM);
        var dmg = event.getDamage();
        var def = victimStats.getStat(damageType.getDefStat()).getTotalValue();

        var calcDmg = (int) Math.max(Math.round((dmg - def) * NORMAL_DAMAGE_MODIFIER), MINIMUM_DAMAGE_VALUE);
        Bukkit.broadcast(Component.text("Test11 RANGED " + dmg + " " + def + " " + calcDmg));

        return new RpgDamageData(calcDmg);
    }
}
