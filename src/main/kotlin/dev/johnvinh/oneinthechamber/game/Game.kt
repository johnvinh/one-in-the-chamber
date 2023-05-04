package dev.johnvinh.oneinthechamber.game

import dev.johnvinh.oneinthechamber.arena.Arena
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.UUID
import java.util.logging.Level

const val MAX_KILLS = 20

/**
 * Handles the game's functionality and point tracking
 */
class Game(private val arena: Arena, private val scoreboard: GameScoreboard) {
    val kills = HashMap<UUID, Int>()

    /**
     * Starts the game.
     */
    fun start() {
        arena.sendInstructions()
    }

    /**
     * Reset the game's state.
     */
    fun reset() {
        kills.clear()
        // Give each player a bow and an arrow
        for (uuid in arena.players) {
            val player = arena.plugin.server.getPlayer(uuid) ?: continue
            player.inventory.clear()
            player.inventory.addItem(ItemStack(Material.BOW))
            player.inventory.addItem(ItemStack(Material.ARROW, 1))
            player.health = 20.0
            player.foodLevel = 20
            scoreboard.updateScore(player, 0)
        }
    }

    /**
     * Increment a player's kill count by one.
     * @param player The player whose kill count should be incremented
     */
    fun addKill(player: Player) {
        kills[player.uniqueId] = kills.getOrDefault(player.uniqueId, 0) + 1
        scoreboard.updateScore(player, kills.getOrDefault(player.uniqueId, 0))
        player.sendMessage("${ChatColor.GREEN}You got a kill! You now have ${kills[player.uniqueId]}!")

        val playerHasWon = kills.getOrDefault(player.uniqueId, 0) == MAX_KILLS
        if (playerHasWon) {
            arena.state = ArenaState.ENDING
            arena.sendTitle("${ChatColor.AQUA}${player.name}", "${ChatColor.YELLOW}has won the game!")
            arena.sendMessage("${ChatColor.AQUA}${player.name}${ChatColor.YELLOW}has achieved 20 kills and won the game!")
            arena.sendMessage("${ChatColor.YELLOW}Returning to lobby in 5 seconds...")

            for (uuid in arena.players) {
                Bukkit.getScheduler().runTaskLater(arena.plugin, Runnable {
                    val arenaPlayer = Bukkit.getPlayer(uuid)
                    if (arenaPlayer != null) {
                        arena.plugin.logger.log(Level.INFO, "Removing ${arenaPlayer.name} from the arena...")
                        arenaPlayer.spigot().respawn()
                        arena.removePlayer(arenaPlayer)
                    }
                }, 20 * 5)
            }
            return
        }

        player.inventory.addItem(ItemStack(Material.ARROW))
    }
}