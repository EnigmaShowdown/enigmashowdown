package com.enigmashowdown.visual

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input

class FullscreenHandler {

    private var width: Int? = null
    private var height: Int? = null

    fun update() {
        if (!Gdx.graphics.isFullscreen) {
            width = Gdx.graphics.width
            height = Gdx.graphics.height
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F11)) {
            if (Gdx.graphics.isFullscreen) {
                Gdx.graphics.setWindowedMode(width ?: 640, height ?: 480)
            } else {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.displayMode)
            }
        }
    }
}
