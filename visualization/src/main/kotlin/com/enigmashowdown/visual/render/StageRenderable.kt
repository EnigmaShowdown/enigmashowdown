package com.enigmashowdown.visual.render

import com.badlogic.gdx.scenes.scene2d.Stage

class StageRenderable(
    private val stage: Stage,
) : Renderable {

    override fun render(delta: Float) {
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, false)
    }

    override fun dispose() {
        stage.dispose()
    }
}
