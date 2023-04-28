package dev.johnvinh.oneinthechamber

import org.bukkit.configuration.file.FileConfiguration

class ConfigManager {
    companion object {
        lateinit var config: FileConfiguration

        fun setupConfig(plugin: OneInTheChamber) {
            config = plugin.config
            plugin.saveDefaultConfig()
        }

        fun getMinimumPlayers() = config.getInt("minimum-players")
        fun getMaximumPlayers() = config.getInt("maximum-players")
    }
}