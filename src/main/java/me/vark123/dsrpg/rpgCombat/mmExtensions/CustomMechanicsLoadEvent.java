package me.vark123.dsrpg.rpgCombat.mmExtensions;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.core.skills.SkillMechanic;
import me.vark123.dsrpg.rpgCombat.mmExtensions.mechanics.SetDefaultDamageTypeMechanic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CustomMechanicsLoadEvent implements Listener {

    @EventHandler
    private void onMechanicLoad(MythicMechanicLoadEvent e) {
        var skillManager = MythicBukkit.inst().getSkillManager();
        ;
        var file = e.getContainer().getFile();
        var line = e.getConfig().getLine();
        var config = e.getConfig();

        var name = e.getMechanicName().toLowerCase();
        SkillMechanic mechanic = switch (name) {
            case "setdefaultdamagetype", "setdamagetype", "damagetype", "setdamage" ->
                    new SetDefaultDamageTypeMechanic(skillManager, file, line, config);
            default -> null;
        };

        if (mechanic != null)
            e.register(mechanic);
    }

}
