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

class Arena(val world: World) {
    val players: MutableList<UUID> = mutableListOf()
    val cuboid = Cuboid(
        Location(world, -46.0, -41.0, -17.0),
        Location(world, 8.6, -41.0, -45.0),
    )

    fun addPlayer(player: Player) {
        val gameIsFull = players.size == ConfigManager.getMaximumPlayers()
        if (gameIsFull) throw GameFullException()

        players.add(player.uniqueId)
        player.teleport(cuboid.randomLocation)
    }

    fun removePlayer(player: Player) {
        val tooFewPlayers = players.size < ConfigManager.getMinimumPlayers()
        if (tooFewPlayers) throw TooFewPlayersException()

        players.remove(player.uniqueId)
        player.teleport(ConfigManager.getLobby())
    }
}