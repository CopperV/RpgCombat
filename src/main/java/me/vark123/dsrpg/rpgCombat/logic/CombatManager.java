package me.vark123.dsrpg.rpgCombat.logic;

import io.lumine.mythic.bukkit.MythicBukkit;
import me.vark123.dsrpg.rpgCombat.logic.calculators.ICombatCalculator;
import me.vark123.dsrpg.rpgCombat.logic.calculators.MagicCombatCalculator;
import me.vark123.dsrpg.rpgCombat.logic.calculators.MeleeCombatCalculator;
import me.vark123.dsrpg.rpgCombat.logic.calculators.RangedCombatCalculator;
import me.vark123.dsrpg.rpgCombat.events.CombatCritCalculateEvent;
import me.vark123.dsrpg.rpgStats.statLogic.RpgEntityStatManager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
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

    private static RpgEntityStatManager statManager;
    private static final Map<RpgDamageType, ICombatCalculator> combatCalculators = new HashMap<>();
    private static final Random rand  = new Random();

    private static final NamespacedKey WEAPON_TYPE_KEY = new NamespacedKey("dsrpg", "weapon-type");
    private static final NamespacedKey DAMAGE_KEY = new NamespacedKey("dsrpg", "damage");

    static {
        statManager = RpgEntityStatManager.getInstance();
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

    public static RpgWeaponType resolveWeaponType(ItemStack item) {
        if (item == null)
            return RpgWeaponType.UNDEFINED;

        if (!MythicBukkit.inst().getItemManager().isMythicItem(item) || !item.hasItemMeta())
            return RpgWeaponType.UNDEFINED;

        var meta = item.getItemMeta();
        var pdc = meta.getPersistentDataContainer();

        if (!pdc.has(WEAPON_TYPE_KEY, PersistentDataType.STRING))
            return RpgWeaponType.UNDEFINED;

        var itemTypeId = pdc.get(WEAPON_TYPE_KEY, PersistentDataType.STRING);
        return RpgWeaponType.fromString(itemTypeId);
    }

    public static int getWeaponDamage(ItemStack item) {
        if (item == null)
            return 0;

        if (!MythicBukkit.inst().getItemManager().isMythicItem(item) || !item.hasItemMeta())
            return 0;

        var meta = item.getItemMeta();
        var pdc = meta.getPersistentDataContainer();

        if (!pdc.has(DAMAGE_KEY, PersistentDataType.INTEGER))
            return 0;

        return pdc.get(DAMAGE_KEY, PersistentDataType.INTEGER);
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
            RpgDamageType damageType, RpgWeaponType weaponType
    ) {
        var event = new CombatCritCalculateEvent(attacker, target, 0, connectedEvent, damageType, weaponType);
        Bukkit.getPluginManager().callEvent(event);

        var chance = event.getChance();
        var draw = rand.nextDouble();
        return chance > draw;
    }

    public static RpgDamageStatType resolveDamageStatType(EntityDamageEvent e, RpgDamageType damageType) {
        if (damageType.equals(RpgDamageType.CUSTOM))
            return RpgDamageStatType.BLUDGEONING;

        var damager = e.getDamageSource().getCausingEntity();
        if (damager instanceof Player player) {
            switch (damageType) {
                case MELEE -> {
                    var weapon = player.getInventory().getItemInMainHand();
                    return resolveWeaponType(weapon).getConnectedStats();
                }
                case RANGED -> {
                    return RpgDamageStatType.PIERCING;
                }
                case MAGIC -> {
                    return RpgDamageStatType.MAGIC;
                }
            }
        } else if (MythicBukkit.inst().getMobManager().isMythicMob(damager)) {
            var mob = MythicBukkit.inst().getMobManager().getMythicMobInstance(damager);
            var variables = mob.getVariables();

            if (variables.has("damage-type")) {
                try {
                    var result = RpgDamageStatType.valueOf(variables.getString("damage-type").toUpperCase());
                    return result;
                } catch (IllegalArgumentException ex) {
                    return RpgDamageStatType.BLUDGEONING;
                }
            }
        } else {
            return RpgDamageStatType.BLUDGEONING;
        }

        return RpgDamageStatType.BLUDGEONING;
    }

}
