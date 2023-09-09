package com.enigmashowdown.visual.render

import com.badlogic.gdx.utils.viewport.Viewport

class ViewportResizerRenderable(
    private val viewport: Viewport,
) : Renderable {
    override fun render(delta: Float) {
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, false)
    }

    override fun dispose() {
    }
}
