package me.vark123.dsrpg.rpgCombat;

import lombok.Getter;
import me.vark123.dsrpg.rpgCombat.config.RpgCombatConfig;
import me.vark123.dsrpg.rpgCombat.listeners.EntityDamageListener;
import me.vark123.dsrpg.rpgCombat.listeners.ProjectileLaunchListener;
import me.vark123.dsrpg.rpgCombat.listeners.ProjectileTrajectoryModifyListener;
import me.vark123.dsrpg.rpgCombat.listeners.crits.StatCritCalcListener;
import me.vark123.dsrpg.rpgCombat.mmExtensions.CustomMechanicsLoadEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class RpgCombat extends JavaPlugin {

    @Getter
    private static RpgCombat instance;

    @Override
    public void onEnable() {
        initialize();
        registerListeners();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void initialize() {
        instance = this;
        RpgCombatConfig.load(this);
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new EntityDamageListener(), this);
        Bukkit.getPluginManager().registerEvents(new CustomMechanicsLoadEvent(), this);
        Bukkit.getPluginManager().registerEvents(new ProjectileLaunchListener(), this);
        Bukkit.getPluginManager().registerEvents(new ProjectileTrajectoryModifyListener(), this);

        Bukkit.getPluginManager().registerEvents(new StatCritCalcListener(), this);
    }
}
