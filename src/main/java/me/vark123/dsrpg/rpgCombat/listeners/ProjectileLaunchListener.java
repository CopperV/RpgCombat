package me.vark123.dsrpg.rpgCombat.listeners;

import io.lumine.mythic.bukkit.BukkitAdapter;
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
        aProjectile.setMetadata("bow", bow);
        aProjectile.setMetadata("force", power);
    }

}
