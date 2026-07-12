package me.vark123.dsrpg.rpgCombat;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class TestListeners implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    private void onDamage1(EntityDamageByEntityEvent e) {
        Bukkit.broadcast(Component.text("List of handlers with prorities:"));
        var handlers = e.getHandlers();
        for(var listener : handlers.getRegisteredListeners()){
            Bukkit.broadcast(Component.text("§c- "+listener.getPlugin().getName()+" "+listener.getListener().getClass().getSimpleName()+" "+listener.getPriority().name()));
        }

        var damage = e.getFinalDamage();
        var entity = e.getEntity();
        var damager = e.getDamager();

        Bukkit.broadcast(Component.text("Lowest priority [Cancelled: "+e.isCancelled()+"]: "+damager.getName()+" -> "+entity.getName()+": "+damage));
    }
    @EventHandler(priority = EventPriority.LOW)
    private void onDamage2(EntityDamageByEntityEvent e) {
        var damage = e.getFinalDamage();
        var entity = e.getEntity();
        var damager = e.getDamager();

        Bukkit.broadcast(Component.text("Low priority [Cancelled: "+e.isCancelled()+"]: "+damager.getName()+" -> "+entity.getName()+": "+damage));
    }
    @EventHandler(priority = EventPriority.NORMAL)
    private void onDamage3(EntityDamageByEntityEvent e) {
        var damage = e.getFinalDamage();
        var entity = e.getEntity();
        var damager = e.getDamager();

        Bukkit.broadcast(Component.text("Normal priority [Cancelled: "+e.isCancelled()+"]: "+damager.getName()+" -> "+entity.getName()+": "+damage));
    }
    @EventHandler(priority = EventPriority.HIGH)
    private void onDamage4(EntityDamageByEntityEvent e) {
        var damage = e.getFinalDamage();
        var entity = e.getEntity();
        var damager = e.getDamager();

        Bukkit.broadcast(Component.text("High priority [Cancelled: "+e.isCancelled()+"]: "+damager.getName()+" -> "+entity.getName()+": "+damage));
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onDamage5(EntityDamageByEntityEvent e) {
        var damage = e.getFinalDamage();
        var entity = e.getEntity();
        var damager = e.getDamager();

        Bukkit.broadcast(Component.text("Highest priority [Cancelled: "+e.isCancelled()+"]: "+damager.getName()+" -> "+entity.getName()+": "+damage));
    }
    @EventHandler(priority = EventPriority.MONITOR)
    private void onDamage6(EntityDamageByEntityEvent e) {
        var damage = e.getFinalDamage();
        var entity = e.getEntity();
        var damager = e.getDamager();

        Bukkit.broadcast(Component.text("Monitor priority [Cancelled: "+e.isCancelled()+"]: "+damager.getName()+" -> "+entity.getName()+": "+damage));
    }

}
