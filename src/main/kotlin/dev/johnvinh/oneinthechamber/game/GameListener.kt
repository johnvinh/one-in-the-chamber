package dev.johnvinh.oneinthechamber.game

import dev.johnvinh.oneinthechamber.ConfigManager
import dev.johnvinh.oneinthechamber.OneInTheChamber
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerPickupArrowEvent
import org.bukkit.inventory.ItemStack

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
                if (plugin.arenaManager.getArena(shooter) == null || plugin.arenaManager.getArena(damaged) == null) {
                    return
                }
                damaged.health = 0.0
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

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val arena = plugin.arenaManager.getArena(event.entity) ?: return
        event.drops.clear()
        event.entity.setBedSpawnLocation(arena.cuboid.randomLocation, true)
        plugin.server.scheduler.runTaskLater(plugin, Runnable {
            event.entity.spigot().respawn()
            event.entity.inventory.clear()
            event.entity.inventory.addItem(ItemStack(Material.ARROW))
            event.entity.inventory.addItem(ItemStack(Material.BOW))
        }, 40)
    }
}