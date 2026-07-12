package me.vark123.dsrpg.rpgCombat.mmExtensions.mechanics;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import me.vark123.dsrpg.rpgCombat.logic.RpgDamageStatType;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

import java.io.File;

@MythicMechanic(
        author = "CopperV",
        name = "setdefaultdamagetype",
        aliases = {"setdamagetype", "damagetype", "setdamage"},
        description = "Set the default damage type for MythicMob"
)
public class SetDefaultDamageTypeMechanic extends SkillMechanic implements INoTargetSkill {

    private final RpgDamageStatType damageType;

    public SetDefaultDamageTypeMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
        super(manager, file, line, mlc);

        var damageTypeId = mlc.getString(new String[]{"damage-type", "type"}, "BLUDGEONING").toUpperCase();
        damageType = RpgDamageStatType.valueOf(damageTypeId);
    }

    @Override
    public SkillResult cast(SkillMetadata skillMetadata) {
        var caster = skillMetadata.getCaster().getEntity();
        if(!MythicBukkit.inst().getMobManager().isActiveMob(caster))
            return SkillResult.INVALID_TARGET;

        var mob = MythicBukkit.inst().getMobManager().getMythicMobInstance(caster);
        mob.getVariables().putString("damage-type", damageType.name());

        return SkillResult.SUCCESS;
    }
}
