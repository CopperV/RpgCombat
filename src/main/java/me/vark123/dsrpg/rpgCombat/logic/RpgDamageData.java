package me.vark123.dsrpg.rpgCombat.logic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RpgDamageData {
    private int damage;
    private boolean isCrit;

    public RpgDamageData(){
        this(-1);
    }

    public RpgDamageData(int damage) {
        this(damage, false);
    }
}
