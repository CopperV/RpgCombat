package me.vark123.dsrpg.rpgCombat.logic;

import lombok.Getter;

@Getter
public enum RpgDamageStatType {

    SLASHING("dmg_slashing", "def_slashing"),
    BLUDGEONING("dmg_bludgeoning", "def_bludgeoning"),
    PIERCING("dmg_piercing", "def_piercing"),
    MAGIC("dmg_magic", "def_magic"),
    FIRE("dmg_fire", "def_fire");

    private final String dmgStat;
    private final String defStat;

    RpgDamageStatType(String dmgStat, String defStat) {
        this.dmgStat = dmgStat;
        this.defStat = defStat;
    }
}
