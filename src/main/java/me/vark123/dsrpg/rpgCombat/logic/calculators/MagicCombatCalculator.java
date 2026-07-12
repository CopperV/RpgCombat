package me.vark123.dsrpg.rpgCombat.logic.calculators;

import me.vark123.dsrpg.rpgCombat.logic.RpgDamageData;
import me.vark123.dsrpg.rpgStats.statLogic.RpgEntityStatManager;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jspecify.annotations.Nullable;

public class MagicCombatCalculator implements ICombatCalculator {

    private RpgEntityStatManager statManager;

    public MagicCombatCalculator() {
        statManager = RpgEntityStatManager.getInstance();
    }

    @Override
    public RpgDamageData calculate(EntityDamageEvent event, @Nullable Entity attacker, @Nullable Entity victim) {
        if(attacker != null) {
            var attackerStats = statManager.getStats(attacker.getUniqueId());
            var victimStats = statManager.getStats(victim.getUniqueId());
        } else {

        }

        return new RpgDamageData(-1, false);
    }
}
