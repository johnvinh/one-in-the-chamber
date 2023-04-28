package dev.johnvinh.oneinthechamber.arena

import dev.johnvinh.oneinthechamber.ConfigManager
import dev.johnvinh.oneinthechamber.Cuboid
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
class Arena(val world: World) {
    /**
     * The players currently playing in this arena instance.
     */
    val players: MutableList<UUID> = mutableListOf()

    /**
     * A Cuboid representing the play area of the arena.
     */
    val cuboid = Cuboid(
        Location(world, -46.0, -41.0, -17.0),
        Location(world, 8.6, -41.0, -45.0),
    )

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
    }
}