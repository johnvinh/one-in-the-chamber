package dev.johnvinh.oneinthechamber

import dev.johnvinh.oneinthechamber.arena.GameFullException
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * Handles the command for joining and leaving the game
 */
class OincCommand(val plugin: OneInTheChamber) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false

        val join = args.size == 1 && args[0].equals("join", true)
        val leave = args.size == 1 && args[0].equals("leave", true)

        if (join) {
            var arenaToJoin = plugin.arenaManager.getArenaWithLeastPlayers()
            val alreadyPlaying = plugin.arenaManager.getArena(sender) != null

            if (alreadyPlaying) {
                sender.sendMessage("${ChatColor.RED}You are already in a game.")
                return true
            } else if (arenaToJoin == null) {
                sender.sendMessage("${ChatColor.YELLOW}Creating new arena...")
                plugin.arenaManager.createNewArena()
                arenaToJoin = plugin.arenaManager.getArenaWithLeastPlayers()
                arenaToJoin?.addPlayer(sender)
            } else {
                try {
                    arenaToJoin.addPlayer(sender)
                } catch (e: GameFullException) {
                    sender.sendMessage("${ChatColor.YELLOW}Creating new arena...")
                    plugin.arenaManager.createNewArena()
                    arenaToJoin = plugin.arenaManager.getArenaWithLeastPlayers()
                    arenaToJoin?.addPlayer(sender)
                }
            }
            return true
        } else if (leave) {
            val arena = plugin.arenaManager.getArena(sender) ?: run {
                sender.sendMessage("${ChatColor.RED}You are not in a game.")
                return true
            }
            arena.removePlayer(sender)
            return true
        } else {
            sender.sendMessage("${ChatColor.RED}Invalid command. Usage: /oinc <join|leave>")
            return true
        }
    }
}