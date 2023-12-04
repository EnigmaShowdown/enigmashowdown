package com.enigmashowdown.game.conquest.map

import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.enigmashowdown.game.conquest.BarrierType
import com.enigmashowdown.map.MapCoordinate

class LevelMap(
    val tiledMap: TiledMap,
) {
    val tileSize = tiledMap.properties["tilewidth"] as Int
    val mapWidth = tiledMap.properties["width"] as Int
    val mapHeight = tiledMap.properties["height"] as Int

    val backgroundLayerIndex: Int
    val floorLayerIndex: Int
    val foregroundLayerIndex: Int
    val topLayerIndex: Int

    val backgroundLayer: TiledMapTileLayer
    val floorLayer: TiledMapTileLayer
    val foregroundLayer: TiledMapTileLayer
    val topLayer: TiledMapTileLayer

    // TODO maybe instead of exposing a barrierMap, we create a more general type (that lives in :core) that represents the physical layout of the map (could have more data than just barriers)
    val barrierMap: Map<MapCoordinate, BarrierType>
    val fireWaterMap: Map<MapCoordinate, Int>

    init {
        var backgroundLayerIndex: Int? = null
        var floorLayerIndex: Int? = null
        var foregroundLayerIndex: Int? = null
        var topLayerIndex: Int? = null
        for ((i, layer) in tiledMap.layers.withIndex()) {
            when (val layerName = layer.name!!) {
                "Background" -> backgroundLayerIndex = i
                "Floor" -> floorLayerIndex = i
                "Foreground" -> foregroundLayerIndex = i
                "Top" -> topLayerIndex = i
                else -> println("Unknown layer: $layerName")
            }
        }
        // TODO throw a more descriptive exception when a layer is not present
        this.backgroundLayerIndex = backgroundLayerIndex!!
        this.floorLayerIndex = floorLayerIndex!!
        this.foregroundLayerIndex = foregroundLayerIndex!!
        this.topLayerIndex = topLayerIndex!!

        backgroundLayer = tiledMap.layers[backgroundLayerIndex] as TiledMapTileLayer
        floorLayer = tiledMap.layers[floorLayerIndex] as TiledMapTileLayer
        foregroundLayer = tiledMap.layers[foregroundLayerIndex] as TiledMapTileLayer
        topLayer = tiledMap.layers[topLayerIndex] as TiledMapTileLayer

        val barrierMap = mutableMapOf<MapCoordinate, BarrierType>()
        val fireWaterMap = mutableMapOf<MapCoordinate, Int>()
        for (x in 0 until mapWidth) for (y in 0 until mapHeight) {
            val cell = foregroundLayer.getCell(x, y)
            val floorcell = floorLayer.getCell(x, y)
            if (cell != null) {
                val tileId = cell.tile.id % 256 // note that this is 1 based, unlike in the tiled editor, where the ID is 0 based. This means this is +1 from the expected value
                barrierMap[MapCoordinate(x, y)] = when (tileId) {
                    in listOf(68) -> BarrierType.WALL_TRIANGLE_SMOOTH_LOWER_LEFT
                    in listOf(69) -> BarrierType.WALL_TRIANGLE_SMOOTH_LOWER_RIGHT
                    in listOf(38) -> BarrierType.WALL_TRIANGLE_SMOOTH_UPPER_LEFT
                    in listOf(39) -> BarrierType.WALL_TRIANGLE_SMOOTH_UPPER_RIGHT
                    else -> BarrierType.WALL
                }
            }
            if (floorcell != null) {
                val tileId = floorcell.tile.id % 256
                fireWaterMap[MapCoordinate(x, y)] = when (tileId) {
                    in listOf(49) -> 1
                    in listOf(50) -> 2
                    else -> 0
                }
            }
        }
        this.barrierMap = barrierMap
        this.fireWaterMap = fireWaterMap
    }
}
