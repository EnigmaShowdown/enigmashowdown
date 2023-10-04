package com.enigmashowdown.visual.render

class DisposeRenderable(
    private val onDispose: () -> Unit,
) : Renderable {
    override fun render(delta: Float) {
    }

    override fun resize(width: Int, height: Int) {
    }

    override fun dispose() {
        onDispose()
    }
}
