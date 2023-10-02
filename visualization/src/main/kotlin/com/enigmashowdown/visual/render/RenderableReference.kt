package com.enigmashowdown.visual.render

class RenderableReference(
    private val renderableGetter: () -> Renderable?,
) : Renderable {
    override fun render(delta: Float) {
        renderableGetter()?.render(delta)
    }

    override fun resize(width: Int, height: Int) {
        renderableGetter()?.resize(width, height)
    }

    override fun dispose() {
        renderableGetter()?.dispose()
    }
}
