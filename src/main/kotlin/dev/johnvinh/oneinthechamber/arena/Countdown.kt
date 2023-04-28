package dev.johnvinh.oneinthechamber.arena

import dev.johnvinh.oneinthechamber.OneInTheChamber
import org.bukkit.ChatColor
import org.bukkit.scheduler.BukkitRunnable

/**
 * Handles the countdown sequence of the arena
 */
class Countdown(private val plugin: OneInTheChamber, private val arena: Arena) : BukkitRunnable() {
    var countdownSeconds = 30
    override fun run() {
        if (countdownSeconds == 0) {
            arena.start()
            cancel()
            return
        }

        if ((countdownSeconds % 10) == 0 || countdownSeconds < 10) {
            arena.sendMessage("${ChatColor.GREEN}The game is starting in $countdownSeconds seconds.")
            arena.sendTitle("${ChatColor.GREEN}Game Starting", "${ChatColor.YELLOW}in $countdownSeconds seconds")
        }

        countdownSeconds--
    }

    fun start() {
        runTaskTimer(plugin, 0, 20)
    }
}