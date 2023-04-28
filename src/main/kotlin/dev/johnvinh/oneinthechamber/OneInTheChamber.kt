package dev.johnvinh.oneinthechamber

import dev.johnvinh.oneinthechamber.arena.ArenaManager
import dev.johnvinh.oneinthechamber.game.GameListener
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class InvalidConfigurationException: Exception()

class OneInTheChamber : JavaPlugin() {
    val arenaManager = ArenaManager(this)
    override fun onEnable() {
        ConfigManager.setupConfig(this)
        Bukkit.getPluginManager().registerEvents(GameListener(this), this)
    }

    override fun onDisable() {

    }
}