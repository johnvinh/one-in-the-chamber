package dev.johnvinh.oneinthechamber.arena

import com.onarandombox.MultiverseCore.api.MultiverseWorld
import com.sk89q.worldedit.MaxChangedBlocksException
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader
import com.sk89q.worldedit.function.operation.ForwardExtentCopy
import com.sk89q.worldedit.function.operation.Operations
import com.sk89q.worldedit.math.Vector3
import dev.johnvinh.oneinthechamber.OneInTheChamber
import dev.johnvinh.oneinthechamber.copyWorld
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.WorldType
import org.bukkit.entity.Player
import java.io.File
import java.io.FileInputStream
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
        val instanceName = "oinc-$instanceId"

        // Create a new world for an arena instance using Multiverse-Core
        val worldManager = plugin.core.mvWorldManager
        val worldCreated = worldManager.addWorld(
            instanceName,
            World.Environment.NORMAL,
            null,
            WorldType.FLAT,
            false,
            null
        )
        if (!worldCreated) {
            throw IOException("Failed to create world for arena instance")
        }
        val newArenaInstance: MultiverseWorld = worldManager.getMVWorld(instanceName) ?: throw IOException("Failed to get world for arena instance")

        // Load the schematic file
        val schematicFile = File(plugin.dataFolder.parent + "/WorldEdit/schematics/oinc.schem")
        val clipboard: BlockArrayClipboard
        var fis: FileInputStream? = null
        var reader: ClipboardReader? = null
        try {
            fis = FileInputStream(schematicFile)
            reader = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getReader(fis)
            clipboard = reader.read() as BlockArrayClipboard
        } catch (e: IOException) {
            e.printStackTrace()
            return
        } finally {
            try {
                reader?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                fis?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        // Get the world instance created with Multiverse-Core
        val world: World = newArenaInstance.cbWorld

        // Set the paste location
        val pasteLocation = Vector3.at(0.0, 0.0, 0.0)

        // Paste the schematic into the world
        val editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world))
        try {
            val copy = ForwardExtentCopy(
                clipboard,
                clipboard.region,
                clipboard.origin,
                editSession,
                pasteLocation.toBlockPoint()
            )
            try {
                Operations.complete(copy)
            } catch (e: MaxChangedBlocksException) {
                e.printStackTrace()
            }
        } finally {
            editSession.close()
        }

        // Add the new arena instance to the list of arenas
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