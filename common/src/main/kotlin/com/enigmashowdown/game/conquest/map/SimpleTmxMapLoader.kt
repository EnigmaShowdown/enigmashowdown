package com.enigmashowdown.game.conquest.map

import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.MapLayers
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile
import com.badlogic.gdx.utils.XmlReader

/**
 * A [TmxMapLoader] that is a hacky way around not being able to load a tiledmap in a separate thread.
 *
 * This implementation is ONLY for loading a tiledmap without any textures.
 */
class SimpleTmxMapLoader(fileHandleResolver: FileHandleResolver) : TmxMapLoader(fileHandleResolver) {

    fun loadNoTextures(fileName: String): TiledMap {
        val tmxFile = resolve(fileName)

        this.root = xml.parse(tmxFile)

        // remove all tileset elements so that when loading the map it won't try to load textures (this is a bit hacky)
        this.root.getChildrenByName("tileset").forEach { element -> this.root.removeChild(element) }

        // Parameter is allowed to be null. Note that imageResolver being null likely isn't supported, but looking at the code we should be OK as long as it doesn't try to create textures
        return loadTiledMap(tmxFile, null, null)
    }

    override fun loadTileLayer(map: TiledMap, parentLayers: MapLayers, element: XmlReader.Element) {
        // ============= THIS IS A HACK =================

        // loadTileLayer is called by loadTiledMap
        // We have to override it here because when there are no tilesets, no cells are put in the layer

        if (element.name == "layer") {
            val width = element.getIntAttribute("width", 0)
            val height = element.getIntAttribute("height", 0)
            val tileWidth = map.properties.get("tilewidth", Int::class.java)
            val tileHeight = map.properties.get("tileheight", Int::class.java)
            val layer = TiledMapTileLayer(width, height, tileWidth, tileHeight)
            loadBasicLayerInfo(layer, element)
            val ids = getTileIds(element, width, height)
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val id = ids[y * width + x]
                    val flipHorizontally = id and FLAG_FLIP_HORIZONTALLY != 0
                    val flipVertically = id and FLAG_FLIP_VERTICALLY != 0
                    val flipDiagonally = id and FLAG_FLIP_DIAGONALLY != 0

                    // we have this check here because a tile ID of 0 means nothing is there.
                    // We do this to stay consistent with the superclass implementation, but honestly we could consider handling the ID=0 case in our own code.
                    if (id != 0) {
                        val tile = StaticTiledMapTile(null as TextureRegion?) // passing null as the TextureRegion is another one of the hacky things we are doing
                        tile.id = id
                        val cell = createTileLayerCell(flipHorizontally, flipVertically, flipDiagonally)
                        cell.setTile(tile)
                        layer.setCell(x, if (flipY) height - 1 - y else y, cell)
                    }
                }
            }
            val properties = element.getChildByName("properties")
            if (properties != null) {
                loadProperties(layer.properties, properties)
            }
            parentLayers.add(layer)
        }
    }
}
