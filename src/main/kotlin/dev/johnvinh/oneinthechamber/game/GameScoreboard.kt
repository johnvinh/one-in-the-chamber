package dev.johnvinh.oneinthechamber.game

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Scoreboard

class GameScoreboard {
    private lateinit var scoreboard: Scoreboard
    private val objective: Objective

    init {
        val scoreBoardManager = Bukkit.getScoreboardManager()
        if (scoreBoardManager != null) {
            scoreboard = scoreBoardManager.newScoreboard
        }
        objective = scoreboard.registerNewObjective("kills", "dummy", "${ChatColor.YELLOW}Kills")
        objective.displaySlot = DisplaySlot.SIDEBAR
    }

    fun updateScore(player: Player, kills: Int) {
        val score = objective.getScore(player.name)
        score.score = kills
        player.scoreboard = scoreboard
    }

    fun getPlayerScore(player: Player): Int {
        return objective.getScore(player.name).score
    }
}