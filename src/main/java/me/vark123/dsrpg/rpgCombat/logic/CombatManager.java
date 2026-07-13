package me.vark123.dsrpg.rpgCombat.logic;

import io.lumine.mythic.bukkit.MythicBukkit;
import me.vark123.dsrpg.rpgCombat.config.RpgCombatConfig;
import me.vark123.dsrpg.rpgCombat.config.RpgCombatConfig.DamageTypeData;
import me.vark123.dsrpg.rpgCombat.config.RpgCombatConfig.WeaponTypeData;
import me.vark123.dsrpg.rpgCombat.config.RpgCombatConstants;
import me.vark123.dsrpg.rpgCombat.logic.calculators.ICombatCalculator;
import me.vark123.dsrpg.rpgCombat.logic.calculators.MagicCombatCalculator;
import me.vark123.dsrpg.rpgCombat.logic.calculators.MeleeCombatCalculator;
import me.vark123.dsrpg.rpgCombat.logic.calculators.RangedCombatCalculator;
import me.vark123.dsrpg.rpgCombat.events.CombatCritCalculateEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public final class CombatManager {

    private static final Map<RpgDamageType, ICombatCalculator> combatCalculators = new HashMap<>();
    private static final Random rand  = new Random();

    static {
        combatCalculators.put(RpgDamageType.MELEE, new MeleeCombatCalculator());
        combatCalculators.put(RpgDamageType.RANGED, new RangedCombatCalculator());
        combatCalculators.put(RpgDamageType.MAGIC, new MagicCombatCalculator());
    }

    private CombatManager() {

    }

    public static RpgDamageData calculateDamage(EntityDamageEvent e) {
        var damager = e.getDamageSource().getCausingEntity();
        var victim = e.getEntity();

        if (damager == null)
            return new RpgDamageData((int) e.getDamage());

        var damageType = resolveDamageType(e);
        if (damageType.equals(RpgDamageType.CUSTOM))
            return new RpgDamageData((int) e.getDamage());

        var calculator = combatCalculators.get(damageType);
        if (calculator == null)
            return new RpgDamageData((int) e.getDamage());

        return calculator.calculate(e, damager, victim);
    }

    public static WeaponTypeData resolveWeaponType(ItemStack item) {
        var config = RpgCombatConfig.getInstance();

        if (item == null)
            return config.getUndefinedWeaponType();

        if (!MythicBukkit.inst().getItemManager().isMythicItem(item) || !item.hasItemMeta())
            return config.getUndefinedWeaponType();

        var meta = item.getItemMeta();
        var pdc = meta.getPersistentDataContainer();

        if (!pdc.has(RpgCombatConstants.WEAPON_TYPE_KEY, PersistentDataType.STRING))
            return config.getUndefinedWeaponType();

        var itemTypeId = pdc.get(RpgCombatConstants.WEAPON_TYPE_KEY, PersistentDataType.STRING);
        return config.getWeaponTypeData(itemTypeId);
    }

    public static int getWeaponDamage(ItemStack item) {
        if (item == null)
            return 0;

        if (!MythicBukkit.inst().getItemManager().isMythicItem(item) || !item.hasItemMeta())
            return 0;

        var meta = item.getItemMeta();
        var pdc = meta.getPersistentDataContainer();

        if (!pdc.has(RpgCombatConstants.DAMAGE_KEY, PersistentDataType.INTEGER))
            return 0;

        return pdc.get(RpgCombatConstants.DAMAGE_KEY, PersistentDataType.INTEGER);
    }

    public static RpgDamageType resolveDamageType(EntityDamageEvent e) {
        var cause = e.getCause();
        if (cause.equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK) ||
                cause.equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK))
            return RpgDamageType.MELEE;

        if (cause.equals(EntityDamageEvent.DamageCause.PROJECTILE))
            return RpgDamageType.RANGED;

        return RpgDamageType.CUSTOM;
    }

    public static boolean checkCrit(
            Entity attacker, Entity target,
            EntityDamageByEntityEvent connectedEvent,
            RpgDamageType damageType, WeaponTypeData weaponType
    ) {
        var event = new CombatCritCalculateEvent(attacker, target, 0, connectedEvent, damageType, weaponType);
        Bukkit.getPluginManager().callEvent(event);

        var chance = event.getChance();
        var draw = rand.nextDouble();
        return chance > draw;
    }

    public static DamageTypeData resolveDamageStatType(EntityDamageEvent e, RpgDamageType damageType) {
        var config = RpgCombatConfig.getInstance();

        if (damageType.equals(RpgDamageType.CUSTOM))
            return config.getDamageTypeData("");

        var damager = e.getDamageSource().getCausingEntity();
        if (damager instanceof Player player) {
            switch (damageType) {
                case MELEE -> {
                    var weapon = player.getInventory().getItemInMainHand();
                    return config.getDamageTypeData(resolveWeaponType(weapon).damageType());
                }
                case RANGED -> {
                    return config.getDamageTypeData("PIERCING");
                }
                case MAGIC -> {
                    return config.getDamageTypeData("MAGIC");
                }
            }
        } else if (MythicBukkit.inst().getMobManager().isMythicMob(damager)) {
            var mob = MythicBukkit.inst().getMobManager().getMythicMobInstance(damager);
            var variables = mob.getVariables();

            if (variables.has(RpgCombatConstants.MYTHICMOB_DAMAGE_TYPE_KEY)) {
                return config.getDamageTypeData(variables.getString(RpgCombatConstants.MYTHICMOB_DAMAGE_TYPE_KEY));
            }
        } else {
            return config.getDamageTypeData("");
        }

        return config.getDamageTypeData("");
    }

}
