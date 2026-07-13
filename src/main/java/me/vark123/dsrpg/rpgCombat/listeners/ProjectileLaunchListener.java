package me.vark123.dsrpg.rpgCombat.listeners;

import io.lumine.mythic.bukkit.BukkitAdapter;
import me.vark123.dsrpg.rpgCombat.config.RpgCombatConstants;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;

public class ProjectileLaunchListener implements Listener {

    @EventHandler
    private void onShoot(EntityShootBowEvent event) {
        if(event.isCancelled())
            return;

        var projectile = event.getProjectile();
        var power = event.getForce();
        var bow = event.getBow();

        var aProjectile = BukkitAdapter.adapt(projectile);
        aProjectile.setMetadata(RpgCombatConstants.METADATA_BOW_ITEM_KEY, bow);
        aProjectile.setMetadata(RpgCombatConstants.METADATA_BOW_FORCE_KEY, power);
    }

}
