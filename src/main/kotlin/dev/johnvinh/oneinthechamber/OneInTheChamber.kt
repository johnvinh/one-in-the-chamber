package dev.johnvinh.oneinthechamber

import com.onarandombox.MultiverseCore.MultiverseCore
import dev.johnvinh.oneinthechamber.arena.ArenaManager
import dev.johnvinh.oneinthechamber.game.GameListener
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class InvalidConfigurationException(private val error: String): Exception(error)

class OneInTheChamber : JavaPlugin() {
    val arenaManager = ArenaManager(this)
    val core = Bukkit.getServer().pluginManager.getPlugin("Multiverse-Core") as MultiverseCore

    override fun onEnable() {
        ConfigManager.setupConfig(this)
        Bukkit.getPluginManager().registerEvents(GameListener(this), this)

        getCommand("oinc")?.setExecutor(OincCommand(this))
    }

    override fun onDisable() {
        // Delete all multicore instances starting with oinc-
        core.mvWorldManager.mvWorlds.filter { it.name.startsWith("oinc-") }.forEach {
            core.mvWorldManager.deleteWorld(it.name)
        }
    }
}