package dev.johnvinh.oneinthechamber.arena

import dev.johnvinh.oneinthechamber.OneInTheChamber
import dev.johnvinh.oneinthechamber.copyWorld
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.io.File
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger

/**
 * Manages arena instances.
 */
class ArenaManager(private val plugin: OneInTheChamber) {
    val arenas: MutableList<Arena> = mutableListOf()
    private val instanceCounter = AtomicInteger()

    fun createNewArena() {
        val instanceId = instanceCounter.incrementAndGet()
        val source = File(Bukkit.getWorldContainer(), "oinc")
        val target = File(Bukkit.getWorldContainer(), "oinc-$instanceId")

        copyWorld(source, target)

        val world = Bukkit.getWorld("oinc-$instanceId") ?: throw IOException("Error retrieving created world")

        arenas.add(Arena(plugin, world))
    }

    /**
     * Gets the arena a player is playing in.
     * @param player the player to check
     */
    fun getArena(player: Player): Arena? {
        for (arena in arenas) {
            if (arena.players.contains(player.uniqueId)) {
                return arena
            }
        }
        return null
    }

    /**
     * Gets the arena with the least number of players.
     */
    fun getArenaWithLeastPlayers(): Arena? {
        return arenas.minByOrNull { it.players.size }
    }
}