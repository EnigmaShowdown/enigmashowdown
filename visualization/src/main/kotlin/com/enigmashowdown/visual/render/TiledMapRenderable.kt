package com.enigmashowdown.visual.render

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer

class TiledMapRenderable
/**
 * @param tiledMap The [TiledMap]
 * @param cameraToFollow The camera to follow. Note that [OrthographicCamera.update] will not be called by this class, so make sure the camera is up to date
 * @param layersToRender An array of ints with the order to render certain layers, or null to render all layers in order
 */
constructor(
    tiledMap: TiledMap,
    private val cameraToFollow: OrthographicCamera,
    private val layersToRender: IntArray? = null,
) : Renderable {

    private val tiledMapRenderer = OrthogonalTiledMapRenderer(tiledMap)

    override fun render(delta: Float) {
        tiledMapRenderer.setView(cameraToFollow)
        if (layersToRender == null) {
            tiledMapRenderer.render()
        } else {
            tiledMapRenderer.render(layersToRender)
        }
    }

    override fun resize(width: Int, height: Int) {
    }

    override fun dispose() {
        tiledMapRenderer.dispose()
    }
}
