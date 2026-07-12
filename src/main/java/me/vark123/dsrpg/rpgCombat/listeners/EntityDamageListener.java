package me.vark123.dsrpg.rpgCombat.listeners;

import io.lumine.mythic.bukkit.BukkitAdapter;
import me.vark123.dsrpg.rpgCombat.logic.CombatManager;
import me.vark123.dsrpg.rpgCombat.logic.RpgDamageData;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageListener implements Listener {

    private static final String RPG_DATA_KEY = "rpg-damage-data";

    //Calculating
    @EventHandler(priority = EventPriority.HIGH)
    private void onDamage(EntityDamageEvent e) {
        if(e.isCancelled()) {
            return;
        }

        var data = CombatManager.calculateDamage(e);
        var aVictim = BukkitAdapter.adapt(e.getEntity());

        if(data.getDamage() > 0){
            e.setDamage(data.getDamage());
            aVictim.setMetadata(RPG_DATA_KEY, data);
        } else {
            e.setCancelled(true);
            aVictim.removeMetadata(RPG_DATA_KEY);
        }
    }

    //Apply
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onApplyDamage(EntityDamageEvent e) {
        var aVictim = BukkitAdapter.adapt(e.getEntity());
        if(!aVictim.hasMetadata(RPG_DATA_KEY))
            return;

        var data = (RpgDamageData) aVictim.getMetadata(RPG_DATA_KEY).get();
        aVictim.removeMetadata(RPG_DATA_KEY);

        if(e.isCancelled())
            return;

        e.setDamage(data.getDamage());
    }
}
