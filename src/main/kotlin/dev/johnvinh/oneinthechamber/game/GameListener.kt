package dev.johnvinh.oneinthechamber.game

import dev.johnvinh.oneinthechamber.ConfigManager
import dev.johnvinh.oneinthechamber.OneInTheChamber
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerPickupArrowEvent

class GameListener(private val plugin: OneInTheChamber) : Listener {
    /**
     * Kill a player instantly with a bow shot and increment point
     */
    @EventHandler
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        val damaged = event.entity
        val damager = event.damager

        // One shot bow kills
        if (damaged is Player && damager is Arrow) {
            val shooter = damager.shooter

            if (shooter is Player) {
                damaged.health = 0.0
                val arena = plugin.arenaManager.getArena(shooter) ?: return
                arena.game.addKill(shooter)
            }
        }
    }

    /**
     * Increment point on a melee kill
     */
    @EventHandler
    fun onPlayerKill(event: PlayerDeathEvent) {
        val victim = event.entity
        val killer = victim.killer ?: return

        val arena = plugin.arenaManager.getArena(killer) ?: return
        arena.game.addKill(killer)
    }

    /**
     * Prevent players from picking up arrows
     */
    @EventHandler
    fun onItemPickup(event: PlayerPickupArrowEvent) {
        val player = event.player
        val arena = plugin.arenaManager.getArena(player) ?: return
        if (arena.state == ArenaState.LIVE) {
            event.isCancelled = true
        }
    }

    /**
     * Teleport players to the lobby on join
     */
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        event.player.teleport(ConfigManager.getLobby())
    }
}