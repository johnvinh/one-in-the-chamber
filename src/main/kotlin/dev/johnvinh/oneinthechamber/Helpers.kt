package dev.johnvinh.oneinthechamber

import com.onarandombox.MultiverseCore.api.MultiverseWorld
import org.bukkit.Bukkit
import org.bukkit.WorldCreator
import java.io.File
import java.io.IOException
import java.util.logging.Level

/*
fun copyWorld(plugin: OneInTheChamber, sourceWorldName: String, targetWorldName: String) {
    val sourceWorld = Bukkit.getWorld(sourceWorldName) ?: throw IOException("Error retrieving source world")

    val source = File(sourceWorld.worldFolder.path)
    val target = File(Bukkit.getWorldContainer(), targetWorldName)

    if (target.exists()) {
        throw IOException("Target world already exists")
    }

    // Unload the source world so it can be copied
    Bukkit.unloadWorld(sourceWorld, false)

    // Copy the world
    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, {
        try {
            source.copyRecursively(target)
        } catch (e: IOException) {
            plugin.logger.log(Level.SEVERE, "Failed to copy world", e)
        } finally {
            // Load the source world again
            Bukkit.createWorld(WorldCreator(sourceWorldName))
        }
    }, 200)
}*/

fun copyWorld(plugin: OneInTheChamber, sourceWorldName: String, targetWorldName: String) {
    val worldManager = plugin.core.mvWorldManager
    val sourceWorld = worldManager.getMVWorld(sourceWorldName)

    if (sourceWorld == null) {
        throw IOException("Source world not found")
    }

    if (worldManager.isMVWorld(targetWorldName)) {
        throw IOException("Target world already exists")
    }

    val success = worldManager.cloneWorld(sourceWorldName, targetWorldName)
    if (!success) {
        throw IOException("Failed to copy world")
    }
}