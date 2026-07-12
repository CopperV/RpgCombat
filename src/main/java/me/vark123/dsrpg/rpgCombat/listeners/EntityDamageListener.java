package me.vark123.dsrpg.rpgCombat.listeners;

import io.lumine.mythic.bukkit.BukkitAdapter;
import me.vark123.dsrpg.rpgCombat.logic.CombatManager;
import me.vark123.dsrpg.rpgCombat.logic.RpgDamageData;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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

    //Apply damage
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onApplyDamage(EntityDamageEvent e) {
        var aVictim = BukkitAdapter.adapt(e.getEntity());
        if(!aVictim.hasMetadata(RPG_DATA_KEY) || e.isCancelled())
            return;

        var data = (RpgDamageData) aVictim.getMetadata(RPG_DATA_KEY).get();
        e.setDamage(data.getDamage());
    }

    //Effects & Cleaning
    @EventHandler(priority = EventPriority.MONITOR)
    private void onApplyEffects(EntityDamageByEntityEvent e) {
        var victim = e.getEntity();
        var aVictim = BukkitAdapter.adapt(victim);
        if(!aVictim.hasMetadata(RPG_DATA_KEY))
            return;

        var data = (RpgDamageData) aVictim.getMetadata(RPG_DATA_KEY).get();
        aVictim.removeMetadata(RPG_DATA_KEY);

        if(e.isCancelled())
            return;

        if(data.isCrit()){
            var attacker = e.getDamageSource().getCausingEntity();

            attacker.getWorld().spawnParticle(
                    Particle.SMALL_FLAME,
                    attacker.getLocation().add(0.5, 1, 0.5),
                    8,
                    0.5f, 1f, 0.5f,
                    0.05f);
            if(attacker instanceof Player player)
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 2, 0.2f);
        }
    }
}
