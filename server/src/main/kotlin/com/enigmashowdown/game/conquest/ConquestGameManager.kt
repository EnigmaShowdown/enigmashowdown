package com.enigmashowdown.game.conquest

import com.enigmashowdown.game.GameManager
import com.enigmashowdown.game.GameMove
import com.enigmashowdown.game.PlayerAction
import com.enigmashowdown.game.conquest.action.ConquestAction
import com.enigmashowdown.game.conquest.map.ConquestLevelInfo
import com.enigmashowdown.game.conquest.map.LevelMap
import com.enigmashowdown.game.conquest.state.ConquestStateView
import com.enigmashowdown.util.getLogger
import java.util.UUID

private class LevelData(
    val conquestLevelInfo: ConquestLevelInfo,
    val levelMap: LevelMap,
)

class ConquestGameManager : GameManager<ConquestStateView, ConquestAction> {

    private var levelData: LevelData? = null
    private var currentLevel: ConquestLevel? = null

    override fun checkPlayerAction(playerAction: PlayerAction): ConquestAction? {
        return playerAction as? ConquestAction
    }

    /** Ends the current active level if there is one, otherwise does nothing*/
    private fun endCurrentLevel() {
        currentLevel = null // TODO we might need some "ending" logic when activeLevel is forcibly nulled in the middle of a game
    }

    override fun initializeLevel(levelId: UUID) {
        val level = ConquestLevelInfo.fromLevelId(levelId)
        if (level == null) {
            logger.warn("Invalid level id: {}", levelId)
            return
        }
        endCurrentLevel()

        // TODO do we want to call createLevelMap() in its own thread?
        val newLevelData = LevelData(level, level.createLevelMap())
        levelData?.levelMap?.tiledMap?.dispose() // TODO later redo this disposal logic to be cleaner
        levelData = newLevelData
    }

    override val levelId: UUID?
        get() = levelData?.conquestLevelInfo?.levelId

    override fun startLevel(players: Collection<UUID>): Boolean {
        val levelData = levelData
        if (levelData == null) {
            logger.error("Cannot call startLevel before calling initializeLevel()")
            return false
        }
        if (players.size != 1) {
            logger.info("Tried starting level when players.size was: ${players.size}")
            return false
        }
        endCurrentLevel()
        currentLevel = ConquestLevel(players.toSet(), levelData.conquestLevelInfo, levelData.levelMap)
        return true
    }

    override val players: Set<UUID>?
        get() = currentLevel?.playerIds
    override val gameStateView: ConquestStateView?
        get() = currentLevel?.gameStateView

    override fun move(gameMove: GameMove<ConquestAction>) {
        currentLevel!!.move(gameMove)
    }

    companion object {
        private val logger = getLogger()
    }
}
