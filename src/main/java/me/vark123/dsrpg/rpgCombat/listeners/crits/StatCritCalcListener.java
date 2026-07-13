package me.vark123.dsrpg.rpgCombat.listeners.crits;

import me.vark123.dsrpg.rpgCombat.events.CombatCritCalculateEvent;
import me.vark123.dsrpg.rpgStats.statLogic.RpgEntityStatManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class StatCritCalcListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    private void onCalc(CombatCritCalculateEvent e){
        var attacker = e.getAttacker();
        var stats = RpgEntityStatManager.getInstance().getStats(attacker.getUniqueId());

        var weaponType = e.getWeaponType();
        var stat = stats.getStat(weaponType.critStat());
        if(stat == null)
            return;

        var value = stat.getTotalValue();
        e.addChance(value / 100.0);
    }

}
