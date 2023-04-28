package dev.johnvinh.oneinthechamber.arena

import dev.johnvinh.oneinthechamber.ConfigManager
import dev.johnvinh.oneinthechamber.Cuboid
import dev.johnvinh.oneinthechamber.OneInTheChamber
import dev.johnvinh.oneinthechamber.game.Game
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import java.io.ObjectInputFilter.Config
import java.util.UUID

class GameFullException : Exception()
class TooFewPlayersException : Exception()

/**
 * An instance of an arena.
 */
class Arena(val plugin: OneInTheChamber, val world: World) {
    /**
     * The players currently playing in this arena instance.
     */
    val players: MutableList<UUID> = mutableListOf()
    /**
     * The game for this instance
     */
    val game = Game()
    /**
     * The countdown for this instance
     */
    val countdown = Countdown(plugin, this)
    /**
     * A Cuboid representing the play area of the arena.
     */
    val cuboid = Cuboid(
        Location(world, -46.0, -41.0, -17.0),
        Location(world, 8.6, -41.0, -45.0),
    )

    /**
     * Start the arena's game.
     */
    fun start() {
        game.start()
    }

    /**
     * Sends a title to all the players in this arena.
     * @param title The title of the title object
     * @param subtitle The subtitle of the title object
     */
    fun sendTitle(title: String, subtitle: String) {
        for (uuid in players) {
            Bukkit.getPlayer(uuid)?.sendTitle(title, subtitle, 0, 20, 0)
        }
    }

    /**
     * Sends a message to all the players in this arena.
     * @param message The message to send
     */
    fun sendMessage(message: String) {
        for (uuid in players) {
            Bukkit.getPlayer(uuid)?.sendMessage(message)
        }
    }

    /**
     * Add a player to the arena.
     * @param player the player to add
     */
    fun addPlayer(player: Player) {
        val gameIsFull = players.size == ConfigManager.getMaximumPlayers()
        if (gameIsFull) throw GameFullException()

        players.add(player.uniqueId)
        player.teleport(cuboid.randomLocation)
    }

    /**
     * Remove a player from the arena.
     * @param player the player to remove
     */
    fun removePlayer(player: Player) {
        val tooFewPlayers = players.size < ConfigManager.getMinimumPlayers()
        if (tooFewPlayers) throw TooFewPlayersException()

        players.remove(player.uniqueId)
        player.teleport(ConfigManager.getLobby())
        player.sendTitle("", "", 0, 20, 0)
    }
}