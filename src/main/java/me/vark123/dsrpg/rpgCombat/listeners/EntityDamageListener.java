package me.vark123.dsrpg.rpgCombat.listeners;

import io.lumine.mythic.bukkit.BukkitAdapter;
import me.vark123.dsrpg.rpgCombat.logic.CombatManager;
import me.vark123.dsrpg.rpgCombat.logic.RpgDamageData;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageListener implements Listener {

    //Calculating
    @EventHandler(priority = EventPriority.HIGH)
    private void onDamage(EntityDamageEvent e) {
        if(e.isCancelled()) {
            Bukkit.broadcast(Component.text("Cancelled"));
            return;
        }

//        double finalDamage = CombatManager.calculateDamage(e);
        var data = CombatManager.calculateDamage(e);
        if(data.getDamage() > 0){
            e.setDamage(data.getDamage());

            var aVictim = BukkitAdapter.adapt(e.getEntity());
            aVictim.setMetadata("rpg-damage-data", data);
        } else {
            e.setCancelled(true);
        }
    }

    //Apply
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onApplyDamage(EntityDamageEvent e) {
        var aVictim = BukkitAdapter.adapt(e.getEntity());
        if(!aVictim.hasMetadata("rpg-damage-data"))
            return;

        var data = (RpgDamageData) aVictim.getMetadata("rpg-damage-data").get();
        aVictim.removeMetadata("rpg-damage-data");
        if(e.isCancelled())
            return;

        e.setDamage(data.getDamage());
    }
}
