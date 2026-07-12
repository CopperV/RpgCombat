package me.vark123.dsrpg.rpgCombat.listeners;

import me.vark123.dsrpg.rpgCombat.logic.CombatManager;
import me.vark123.dsrpg.rpgCombat.logic.RpgWeaponType;
import me.vark123.dsrpg.rpgStats.statLogic.RpgEntityStatManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class ProjectileTrajectoryModifyListener implements Listener {

    private static final double MAX_SPREAD_DEGREES_X = 30.0;
    private static final double MAX_SPREAD_DEGREES_Y = 10.0;

    @EventHandler(priority = EventPriority.HIGH)
    private void onShoot(EntityShootBowEvent e) {
        if (e.isCancelled() || !(e.getEntity() instanceof Player player))
            return;

        var stats = RpgEntityStatManager.getInstance().getStats(player.getUniqueId());
        var weapon = e.getBow();
        var weaponType = CombatManager.resolveWeaponType(weapon);

        if (weaponType == RpgWeaponType.UNDEFINED)
            return;

        var stat = stats.getStat(weaponType.getCritStat());
        if (stat == null)
            return;

        var value = stat.getTotalValue();
        var masteryRatio = Math.min(1.0, Math.max(0.0, value / 100.0));

        if (masteryRatio >= 1.0)
            return;

        double maxSpreadX = MAX_SPREAD_DEGREES_X * (1.0 - masteryRatio);
        double maxSpreadY = MAX_SPREAD_DEGREES_Y * (1.0 - masteryRatio);
        var velocity = e.getProjectile().getVelocity();
        var speed = velocity.length();

        Vector spreadVelocity = applySpread(velocity.normalize(), maxSpreadX, maxSpreadY).multiply(speed);
        e.getProjectile().setVelocity(spreadVelocity);
    }

    private Vector applySpread(@NotNull Vector direction, double maxAngleSpreadX, double maxAngleSpreadY) {
        if(maxAngleSpreadX <= 0)
            return direction.clone();

        var random = ThreadLocalRandom.current();

        Vector worldUp = Math.abs(direction.getY()) > 0.95 ? new Vector(1, 0, 0) : new Vector(0, 1, 0);
        Vector right = direction.clone().crossProduct(worldUp).normalize();
        Vector up = right.clone().crossProduct(direction).normalize();

        double spreadFactorX = Math.tan(Math.toRadians(maxAngleSpreadX));
        double spreadFactorY = Math.tan(Math.toRadians(maxAngleSpreadY));
        double sigmaX = spreadFactorX / 3.0;
        double sigmaY = spreadFactorY / 3.0;

        double offsetX = (random.nextGaussian() - 0.5) * 2 * sigmaX;
        double offsetY = (random.nextGaussian() - 0.5) * 2 * sigmaY;

        Vector spreadDirection = direction.clone()
                .add(right.multiply(offsetX))
                .add(up.multiply(offsetY))
                .normalize();

        return spreadDirection;
    }

}
