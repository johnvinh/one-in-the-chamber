package dev.johnvinh.oneinthechamber

import dev.johnvinh.oneinthechamber.arena.ArenaManager
import org.bukkit.plugin.java.JavaPlugin

class InvalidConfigurationException: Exception()

class OneInTheChamber : JavaPlugin() {
    val arenaManager = ArenaManager(this)
    override fun onEnable() {
        ConfigManager.setupConfig(this)
    }

    override fun onDisable() {

    }
}