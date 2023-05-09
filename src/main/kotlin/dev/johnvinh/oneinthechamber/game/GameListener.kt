package dev.johnvinh.oneinthechamber.game

import dev.johnvinh.oneinthechamber.ConfigManager
import dev.johnvinh.oneinthechamber.OneInTheChamber
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDamageEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerPickupArrowEvent
import org.bukkit.inventory.ItemStack

class GameListener(private val plugin: OneInTheChamber) : Listener {

    /**
     * Checks if two arenas are both in an arena, in the same arena, and that arena is live.
     * @param damaged The damaged player.
     * @param damager The player who is damaging the damaged player.
     */
    private fun playerIsNotInArenaOrNotLive(damager: Player, damaged: Player): Boolean {
        val damagerArena = plugin.arenaManager.getArena(damager)
        val damagedArena = plugin.arenaManager.getArena(damaged)

        if (damagedArena == null || damagerArena == null) {
            return true
        } else if (damagerArena != damagedArena) {
            return true
        } else if (damagerArena.state != ArenaState.LIVE) {
            return true
        }
        return false
    }

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
                if (playerIsNotInArenaOrNotLive(shooter, damaged)) {
                    event.isCancelled = true
                    return
                }
                damaged.health = 0.0
            }
        }
        // Other damage
        else if (damaged is Player && damager is Player) {
            if (playerIsNotInArenaOrNotLive(damager, damaged)) {
                event.isCancelled = true
                return
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
        val player = event.entity
        val arena = plugin.arenaManager.getArena(player) ?: return
        event.drops.clear()
        event.entity.setBedSpawnLocation(arena.cuboid.randomLocation, true)
        plugin.server.scheduler.runTaskLater(plugin, Runnable {
            player.spigot().respawn()
            player.inventory.clear()
            arena.game.resetPlayerItems(player)
        }, 40)
    }

    /**
     * Prevent players from moving if the game is ending.
     */
    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val arena = plugin.arenaManager.getArena(event.player) ?: return
        if (arena.state == ArenaState.ENDING) event.isCancelled = true
    }

    /**
     * Prevent players from damaging blocks inside arenas.
     */
    @EventHandler
    fun onBlockDamage(event: BlockDamageEvent) {
        if (plugin.arenaManager.getArena(event.player) != null) event.isCancelled = true
    }
}