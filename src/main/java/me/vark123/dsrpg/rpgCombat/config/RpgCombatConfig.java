package me.vark123.dsrpg.rpgCombat.config;

import lombok.Getter;
import me.vark123.dsrpg.rpgCombat.logic.RpgDamageType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

@Getter
public class RpgCombatConfig {
    @Getter
    private static RpgCombatConfig instance;

    private final int minimumDamage;
    private final double rangedMaxSpreadDegreesX;
    private final double rangedMaxSpreadDegreesY;

    private final Map<RpgDamageType, Double> normalModifiers = new EnumMap<>(RpgDamageType.class);
    private final Map<RpgDamageType, Double> critModifiers = new EnumMap<>(RpgDamageType.class);

    private final Map<String, DamageTypeData> damageTypes = new HashMap<>();
    private final Map<String, WeaponTypeData> weaponTypes = new HashMap<>();

    private final WeaponTypeData undefinedWeaponType = new WeaponTypeData("UNDEFINED", "BLUDGEONING", "", Map.of());

    private RpgCombatConfig(FileConfiguration config) {
        // Combat Settings
        this.minimumDamage = config.getInt("combat-settings.minimum-damage", 1);
        this.rangedMaxSpreadDegreesX = config.getDouble("combat-settings.ranged-max-spreed-degrees.x", 30.0);
        this.rangedMaxSpreadDegreesY = config.getDouble("combat-settings.ranged-max-spreed-degrees.y", 10.0);

        // Modifiers
        loadModifiers(config, "melee", RpgDamageType.MELEE);
        loadModifiers(config, "ranged", RpgDamageType.RANGED);
        loadModifiers(config, "magic", RpgDamageType.MAGIC);

        // Damage Types
        var damageTypesSec = config.getConfigurationSection("damage-types");
        if (damageTypesSec != null) {
            for (String key : damageTypesSec.getKeys(false)) {
                String dmgStat = damageTypesSec.getString(key + ".dmg-stat");
                String defStat = damageTypesSec.getString(key + ".def-stat");
                damageTypes.put(key.toUpperCase(), new DamageTypeData(key.toUpperCase(), dmgStat, defStat));
            }
        }

        // Weapon Types
        var weaponTypesSec = config.getConfigurationSection("weapon-types");
        if (weaponTypesSec != null) {
            for (String key : weaponTypesSec.getKeys(false)) {
                String dmgType = weaponTypesSec.getString(key + ".damage-type");
                String critStat = weaponTypesSec.getString(key + ".crit-stat");

                Map<String, Double> proportions = new HashMap<>();
                var propSec = weaponTypesSec.getConfigurationSection(key + ".stat-proportions");
                if (propSec != null) {
                    for (String statKey : propSec.getKeys(false)) {
                        proportions.put(statKey, propSec.getDouble(statKey));
                    }
                }

                weaponTypes.put(key.toUpperCase(), new WeaponTypeData(key.toUpperCase(), dmgType, critStat, proportions));
            }
        }
    }

    public static void load(Plugin plugin) {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        instance = new RpgCombatConfig(plugin.getConfig());
    }

    public WeaponTypeData getWeaponTypeData(String key) {
        return weaponTypes.getOrDefault(key.toUpperCase(), undefinedWeaponType);
    }

    public DamageTypeData getDefaultDamageTypeData(){
        return getDamageTypeData("BLUDGEONING");
    }

    public DamageTypeData getDamageTypeData(String key) {
        key = key.toUpperCase();
        return damageTypes.containsKey(key) ?
                damageTypes.get(key) :
                damageTypes.get("BLUDGEONING");
    }

    public double getNormalModifier(RpgDamageType type) {
        return normalModifiers.getOrDefault(type, 0.25);
    }

    public double getCritModifier(RpgDamageType type) {
        return critModifiers.getOrDefault(type, 1.0);
    }

    private void loadModifiers(ConfigurationSection section, String path, RpgDamageType type) {
        normalModifiers.put(type, section.getDouble("combat-settings.damage-modifiers." + path + ".normal", 0.25));
        critModifiers.put(type, section.getDouble("combat-settings.damage-modifiers." + path + ".crit", 1.0));
    }

    public record DamageTypeData(String name, String dmgStat, String defStat) {
    }

    public record WeaponTypeData(String name, String damageType, String critStat, Map<String, Double> statProportions) {
    }
}
