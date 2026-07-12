package me.vark123.dsrpg.rpgCombat.logic;

import lombok.Getter;

import java.util.Map;

@Getter
public enum RpgWeaponType {
    SWORD_1H(RpgDamageStatType.SLASHING, Map.of(
            "str", 0.7,
            "zr", 0.3
    )),
    DAGGER_1H(RpgDamageStatType.PIERCING, Map.of(
            "str", 0.3,
            "zr", 0.7
    )),
    AXE_1H(RpgDamageStatType.SLASHING, Map.of(
            "str", 1.0
    )),
    MACE_1H(RpgDamageStatType.BLUDGEONING, Map.of(
            "str", 1.0
    )),
    SWORD_2H(RpgDamageStatType.SLASHING, Map.of(
            "str", 0.9,
            "zr", 0.1
    )),
    AXE_2H(RpgDamageStatType.SLASHING, Map.of(
            "str", 1.0
    )),
    HAMMER_2H(RpgDamageStatType.BLUDGEONING, Map.of(
            "str", 1.0
    )),
    STUFF_2H(RpgDamageStatType.BLUDGEONING, Map.of(
            "str", 0.5,
            "pwr", 0.3
    )),
    SPEAR_2H(RpgDamageStatType.PIERCING, Map.of(
            "str", 0.2,
            "zr", 0.8
    )),
    BOW(RpgDamageStatType.PIERCING, Map.of(
            "zr", 1.0
    )),
    CROSSBOW(RpgDamageStatType.PIERCING, Map.of(
            "str", 0.5,
            "zr", 0.5
    )),
    UNDEFINED(RpgDamageStatType.BLUDGEONING, Map.of());

    private final RpgDamageStatType connectedStats;
    private final Map<String, Double> statProportions;

    RpgWeaponType(RpgDamageStatType connectedStats, Map<String, Double> statProportions) {
        this.connectedStats = connectedStats;
        this.statProportions = statProportions;
    }

    public String getDmgStat(){
        return connectedStats.getDmgStat();
    }

    public String getDefStat(){
        return connectedStats.getDefStat();
    }

    public static RpgWeaponType fromString(String str) {
        if(str == null || str.isEmpty())
            return UNDEFINED;

        return switch (str.toLowerCase()) {
            case "1h_sword" -> SWORD_1H;
            case "1h_dagger" -> DAGGER_1H;
            case "1h_axe" -> AXE_1H;
            case "1h_mace" -> MACE_1H;
            case "2h_sword" -> SWORD_2H;
            case "2h_axe" -> AXE_2H;
            case "2h_hammer" -> HAMMER_2H;
            case "2h_stuff" -> STUFF_2H;
            case "2h_spear" -> SPEAR_2H;
            case "bow" -> BOW;
            case "crossbow" -> CROSSBOW;
            default -> UNDEFINED;
        };
    }
}
