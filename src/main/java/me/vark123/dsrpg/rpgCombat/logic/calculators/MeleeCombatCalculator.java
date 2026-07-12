package me.vark123.dsrpg.rpgCombat.logic.calculators;

import io.lumine.mythic.api.skills.damage.DamageMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.vark123.dsrpg.rpgCombat.logic.*;
import me.vark123.dsrpg.rpgStats.statLogic.RpgEntityStatManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class MeleeCombatCalculator implements ICombatCalculator {

    private RpgEntityStatManager statManager;

    public MeleeCombatCalculator() {
        statManager = RpgEntityStatManager.getInstance();
    }

    @Override
    public RpgDamageData calculate(EntityDamageEvent event, @Nullable Entity attacker, @Nullable Entity victim) {
        var rpgData = new RpgDamageData(-1, false);

        if (attacker != null) {
            var attackerStats = statManager.getStats(attacker.getUniqueId());
            var victimStats = statManager.getStats(victim.getUniqueId());

            var damageType = CombatManager.resolveDamageStatType(event, RpgDamageType.MELEE);
            var dmg = attackerStats.getStat(damageType.getDmgStat()).getTotalValue();
            var statSum = 0;
            var def = victimStats.getStat(damageType.getDefStat()).getTotalValue();

            if (attacker instanceof Player player) {
                var weapon = player.getEquipment().getItemInMainHand();

                dmg += CombatManager.getWeaponDamage(weapon);

                var weaponType = CombatManager.resolveWeaponType(weapon);
                if (weaponType != RpgWeaponType.UNDEFINED) {
                    if (weaponType.getStatProportions().size() > 1) {
                        int totalStatSum = 0;
                        for (String statKey : weaponType.getStatProportions().keySet()) {
                            var stat = attackerStats.getStat(statKey);
                            if (stat != null)
                                totalStatSum += stat.getTotalValue();
                        }

                        double maxErrorSum = 0;
                        for (double val : weaponType.getStatProportions().values()) {
                            maxErrorSum += val * val;
                        }
                        double maxError = Math.sqrt(maxErrorSum);

                        double currentStatSum = 0;
                        double defectionSum = 0;
                        for (var entry : weaponType.getStatProportions().entrySet()) {
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
                        statSum = (int) (currentStatSum * boost);

                    } else {
                        double currentStatSum = 0;
                        for (var entry : weaponType.getStatProportions().entrySet()) {
                            var statData = attackerStats.getStat(entry.getKey());
                            if (statData != null) {
                                currentStatSum += statData.getTotalValue() * entry.getValue();
                            }
                        }
                        statSum = (int) currentStatSum;
                    }
                }
            } else {
                var aVictim = BukkitAdapter.adapt(victim);
                var aAttacker = BukkitAdapter.adapt(attacker);
                if(aAttacker.hasMetadata("doing-skill-damage") && aVictim.hasMetadata("skill-damage")) {
                    DamageMetadata data = (DamageMetadata) aVictim.getMetadata("skill-damage").get();
                    var skillDamageType = data.getElement();
                    if(skillDamageType != null){
                        try{
                            damageType = RpgDamageStatType.valueOf(skillDamageType.toUpperCase());
                            dmg = (int) event.getDamage();
                            def = victimStats.getStat(damageType.getDefStat()).getTotalValue();
                        } catch(IllegalArgumentException ex) { }
                    }
                }
            }

            Bukkit.broadcast(Component.text("Test10 " + dmg + " " + statSum + " " + def));
            var calcDmg = (int) Math.max(Math.round((dmg + statSum - def) * 0.25), 1);

            rpgData.setDamage(calcDmg);
        } else {
            var victimStats = statManager.getStats(victim.getUniqueId());

            var damageType = CombatManager.resolveDamageStatType(event, RpgDamageType.CUSTOM);
            var dmg = event.getDamage();
            var def = victimStats.getStat(damageType.getDefStat()).getTotalValue();

            Bukkit.broadcast(Component.text("Test11 " + dmg + " " + def));
            var calcDmg = (int) Math.max(Math.round((dmg - def) * 0.25), 1);

            rpgData.setDamage(calcDmg);
        }

        return rpgData;
    }
}
