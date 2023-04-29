package dev.johnvinh.oneinthechamber.arena

import dev.johnvinh.oneinthechamber.ConfigManager
import dev.johnvinh.oneinthechamber.Cuboid
import dev.johnvinh.oneinthechamber.OneInTheChamber
import dev.johnvinh.oneinthechamber.game.ArenaState
import dev.johnvinh.oneinthechamber.game.Game
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import java.io.ObjectInputFilter.Config
import java.util.UUID

class GameFullException : Exception()
class TooFewPlayersException : Exception()

/**
 * An instance of an arena.
 */
class Arena(val plugin: OneInTheChamber, val world: World) {
    /**
     * The players currently playing in this arena instance.
     */
    val players: MutableList<UUID> = mutableListOf()
    /**
     * The game for this instance
     */
    val game = Game(this)
    /**
     * The countdown for this instance
     */
    val countdown = Countdown(plugin, this)
    /**
     * A Cuboid representing the play area of the arena.
     */
    val cuboid = Cuboid(
        Location(world, -46.0, -41.0, -17.0),
        Location(world, 8.6, -41.0, -45.0),
    )
    /**
     * The state of the arena.
     */
    var state = ArenaState.RECRUITING

    /**
     * Sends the game instructions to a player.
     */
    fun sendInstructions() {
        val instructions = """
        ${ChatColor.BOLD}${ChatColor.GOLD}-----------------------------------
        ${ChatColor.GREEN}Welcome to One in the Chamber!
        ${ChatColor.AQUA}Instructions:
        ${ChatColor.WHITE}1. Kill other players in one hit using your bow
        ${ChatColor.WHITE}2. If you hit your bow shot, you will get another arrow
        ${ChatColor.WHITE}3. If you miss your bow shot, you will have to kill enemies with your sword
        ${ChatColor.WHITE}4. Any kill will grant you another arrow, including melee kills
        ${ChatColor.YELLOW}Good luck and have fun!
        ${ChatColor.BOLD}${ChatColor.GOLD}-----------------------------------
    """.trimIndent()

        val component = TextComponent(*ComponentBuilder(instructions).create())
        for (uuid in players) {
            Bukkit.getPlayer(uuid)?.spigot()?.sendMessage(component)
        }
    }

    /**
     * Start the arena's game.
     */
    fun start() {
        game.start()
    }

    /**
     * Sends a title to all the players in this arena.
     * @param title The title of the title object
     * @param subtitle The subtitle of the title object
     */
    fun sendTitle(title: String, subtitle: String) {
        for (uuid in players) {
            Bukkit.getPlayer(uuid)?.sendTitle(title, subtitle, 0, 20, 0)
        }
    }

    /**
     * Sends a message to all the players in this arena.
     * @param message The message to send
     */
    fun sendMessage(message: String) {
        for (uuid in players) {
            Bukkit.getPlayer(uuid)?.sendMessage(message)
        }
    }

    /**
     * Add a player to the arena.
     * @param player the player to add
     */
    fun addPlayer(player: Player) {
        val gameIsFull = players.size == ConfigManager.getMaximumPlayers()
        if (gameIsFull) throw GameFullException()

        players.add(player.uniqueId)
        player.teleport(cuboid.randomLocation)

        val minimumPlayersReached = players.size >= ConfigManager.getMinimumPlayers()
        val gameisNotStarting = state != ArenaState.STARTING

        if (minimumPlayersReached && gameisNotStarting) {
            countdown.start()
            state = ArenaState.STARTING
        }
    }

    /**
     * Remove a player from the arena.
     * @param player the player to remove
     */
    fun removePlayer(player: Player) {
        players.remove(player.uniqueId)
        player.teleport(ConfigManager.getLobby())
        player.sendTitle("", "", 0, 20, 0)

        val tooFewPlayers = players.size < ConfigManager.getMinimumPlayers()
        val gameIsLive = state == ArenaState.LIVE
        val gameIsStarting = state == ArenaState.STARTING
        if (tooFewPlayers && gameIsLive) {
            game.reset()
            state = ArenaState.RECRUITING
        } else if (tooFewPlayers && gameIsStarting) {
            countdown.cancel()
            state = ArenaState.RECRUITING
        }
    }
}