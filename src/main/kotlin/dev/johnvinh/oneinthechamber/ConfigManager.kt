package dev.johnvinh.oneinthechamber

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.WorldCreator
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

        fun getLobby(): Location {
            val worldName = config.getString("lobby.world") ?: throw InvalidConfigurationException("The lobby world is not set")
            val world = Bukkit.getWorld(worldName) ?: Bukkit.createWorld(WorldCreator(worldName))
            ?: throw InvalidConfigurationException("Error loading or creating the world")

            return Location(
                Bukkit.getWorld(worldName),
                config.getDouble("lobby.x"),
                config.getDouble("lobby.y"),
                config.getDouble("lobby.z"),
                config.getDouble("lobby.yaw").toFloat(),
                config.getDouble("lobby.pitch").toFloat(),
            )
        }
    }
}