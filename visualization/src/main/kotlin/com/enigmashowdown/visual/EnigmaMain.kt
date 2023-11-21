package com.enigmashowdown.visual

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.enigmashowdown.visual.render.RenderObject
import com.enigmashowdown.visual.screens.TitleScreen

class EnigmaMain : Game() {
    private val fullscreenHandler = FullscreenHandler()

    private lateinit var renderObject: RenderObject

    private var nextScreen: Screen? = null

    override fun create() {
        val batch: Batch = SpriteBatch()
        val mainSkin = Skin(Gdx.files.classpath("skins/main/skin.json"))
        val uiSkin = Skin(Gdx.files.classpath("skins/star-soldier/star-soldier-ui.json"))
        renderObject = RenderObject(batch, mainSkin, uiSkin)

        setScreen(TitleScreen(::changeScreen, renderObject))
    }
    private fun changeScreen(newScreen: Screen) {
        nextScreen = newScreen
    }

    override fun render() {
        // don't call super, as it will render the screen with the delta time. Instead, let's do that here explicitly on our own terms
        nextScreen?.let {
            setScreen(it)
            nextScreen = null
        }
        val delta = Gdx.graphics.deltaTime
        screen?.render(delta)

        fullscreenHandler.update()
        Gdx.graphics.setTitle("Enigma Showdown - FPS: ${Gdx.graphics.framesPerSecond}")
    }

    override fun dispose() {
        super.dispose()
        renderObject.dispose()
    }
}
