package dev.johnvinh.oneinthechamber.arena

import dev.johnvinh.oneinthechamber.arena.Arena
import dev.johnvinh.oneinthechamber.copyWorld
import org.bukkit.Bukkit
import java.io.File
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger

/**
 * Manages arena instances.
 */
class ArenaManager {
    val arenas: MutableList<Arena> = mutableListOf()
    private val instanceCounter = AtomicInteger()

    fun createNewArena() {
        val instanceId = instanceCounter.incrementAndGet()
        val source = File(Bukkit.getWorldContainer(), "oinc")
        val target = File(Bukkit.getWorldContainer(), "oinc-$instanceId")

        copyWorld(source, target)

        val world = Bukkit.getWorld("oinc-$instanceId") ?: throw IOException("Error retrieving created world")

        arenas.add(Arena(world))
    }
}