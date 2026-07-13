package me.vark123.dsrpg.rpgCombat.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.vark123.dsrpg.rpgCombat.logic.RpgDamageType;
import me.vark123.dsrpg.rpgCombat.config.RpgCombatConfig.WeaponTypeData;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

@Getter
@AllArgsConstructor
public class CombatCritCalculateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Entity attacker;
    private Entity target;
    @Setter
    private double chance;

    private EntityDamageByEntityEvent event;

    private RpgDamageType damageType;
    private WeaponTypeData weaponType;

    public void addChance(double chance) {
        this.chance += chance;
    }

    public void subtractChance(double chance) {
        this.chance -= chance;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
