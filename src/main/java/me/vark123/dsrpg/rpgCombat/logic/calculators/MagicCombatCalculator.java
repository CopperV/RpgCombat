package me.vark123.dsrpg.rpgCombat.logic.calculators;

import me.vark123.dsrpg.rpgCombat.logic.RpgDamageData;
import me.vark123.dsrpg.rpgStats.statLogic.RpgStatsHolder;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jspecify.annotations.Nullable;

public class MagicCombatCalculator extends ACombatCalculator {

    @Override
    protected RpgDamageData calculateEntityDamage(EntityDamageEvent event, Entity attacker, Entity victim, RpgStatsHolder attackerStats, RpgStatsHolder victimStats) {
        return new RpgDamageData((int) event.getDamage());
    }

    @Override
    protected RpgDamageData calculateEnvironmentalDamage(EntityDamageEvent event, @Nullable Entity victim, RpgStatsHolder victimStats) {
        return new RpgDamageData((int) event.getDamage());
    }
}
