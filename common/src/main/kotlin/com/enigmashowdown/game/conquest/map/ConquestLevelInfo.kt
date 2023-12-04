package com.enigmashowdown.game.conquest.map

import com.badlogic.gdx.assets.loaders.resolvers.ClasspathFileHandleResolver
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.enigmashowdown.game.conquest.LevelId
import java.util.UUID

/**
 * Represents unique parts of a given level
 */
enum class ConquestLevelInfo(
    val levelId: UUID,
    val tiledMapPath: String,
) {
    LEVEL_1(LevelId.LEVEL_1, "conquest/level1.tmx"),
    LEVEL_2(LevelId.LEVEL_2, "conquest/level2.tmx"),
    LEVEL_3(LevelId.LEVEL_3, "conquest/level3.tmx"),
    LEVEL_4(LevelId.LEVEL_4, "conquest/level4.tmx"),
    LEVEL_5(LevelId.LEVEL_5, "conquest/level5.tmx"),
    LEVEL_6(LevelId.LEVEL_6, "conquest/level6.tmx"),
    BETA_1(LevelId.BETA_1, "conquest/beta1.tmx"),
    ;

    fun createLevelMapNoTextures(): LevelMap {
//        Gdx.files.classpath()
//        val tiledMap = TmxMapLoader(ClasspathFileHandleResolver()).load(tiledMapPath)
        val tiledMap = SimpleTmxMapLoader(ClasspathFileHandleResolver()).loadNoTextures(tiledMapPath)
        return LevelMap(tiledMap)
    }
    fun createLevelMap(): LevelMap {
        val tiledMap = TmxMapLoader(ClasspathFileHandleResolver()).load(tiledMapPath)
        return LevelMap(tiledMap)
    }

    companion object {
        fun fromLevelId(levelId: UUID): ConquestLevelInfo? {
            return entries.firstOrNull { it.levelId == levelId }
        }
    }
}
