package dev.johnvinh.oneinthechamber.game

import dev.johnvinh.oneinthechamber.arena.Arena
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.UUID

/**
 * Handles the game's functionality and point tracking
 */
class Game(private val arena: Arena) {
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
    }

    /**
     * Increment a player's kill count by one.
     * @param player The player whose kill count should be incremented
     */
    fun addKill(player: Player) {
        kills[player.uniqueId] = kills.getOrDefault(player.uniqueId, 0) + 1
        player.sendMessage("${ChatColor.GREEN}You got a kill! You now have ${kills[player.uniqueId]}!")
        player.inventory.addItem(ItemStack(Material.ARROW))
    }
}