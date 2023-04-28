package dev.johnvinh.oneinthechamber.game

import dev.johnvinh.oneinthechamber.OneInTheChamber
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerPickupArrowEvent

class GameListener(private val plugin: OneInTheChamber) : Listener {
    @EventHandler
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        val damaged = event.entity
        val damager = event.damager

        if (damaged is Player && damager is Arrow) {
            val shooter = damager.shooter

            if (shooter is Player) {
                damaged.health = 0.0
                val arena = plugin.arenaManager.getArena(shooter) ?: return
                arena.game.addKill(shooter)
            }
        }
    }
}