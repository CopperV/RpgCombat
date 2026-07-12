package me.vark123.dsrpg.rpgCombat.logic.calculators;

import me.vark123.dsrpg.rpgCombat.logic.RpgDamageData;
import me.vark123.dsrpg.rpgStats.statLogic.RpgStatsHolder;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;

import javax.annotation.Nullable;

public interface ICombatCalculator {
    RpgDamageData calculate(EntityDamageEvent event, @Nullable Entity attacker, @Nullable Entity victim);
}
