package com.enigmashowdown.visual.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.viewport.ScalingViewport
import com.enigmashowdown.util.getLogger
import com.enigmashowdown.visual.render.RenderObject
import com.enigmashowdown.visual.render.Renderable
import com.enigmashowdown.visual.render.RenderableMultiplexer
import com.enigmashowdown.visual.render.ResetRenderable
import com.enigmashowdown.visual.render.StageRenderable

class TitleScreen(
    private val screenChanger: ScreenChanger,
    private val renderObject: RenderObject,
) : ScreenAdapter() {
    private val logger = getLogger()

    private val hostButton: Button
    private val connectButton: Button
    private val connectAddressField: TextField
    private val uiStage: Stage

    private val renderable: Renderable

    init {
        val textButtonStyle = renderObject.uiSkin.get(TextButton.TextButtonStyle::class.java).let {
            TextButton.TextButtonStyle(it).apply {
                over = down
            }
        }
        hostButton = TextButton("Host", textButtonStyle)
        connectButton = TextButton("Connect", textButtonStyle)
        val textFieldStyle = renderObject.uiSkin.get(TextField.TextFieldStyle::class.java)
        connectAddressField = TextField("localhost", textFieldStyle)
        connectAddressField.alignment = Align.center

        val table = Table()
        table.setFillParent(true)
        table.add(Label("Enigma Showdown", renderObject.uiSkin.get("title", Label.LabelStyle::class.java)))
        table.row()
        table.add(hostButton).width(400f).row()
        table.add(connectButton).width(400f).row()
        table.add(connectAddressField).width(300f).row()

        uiStage = Stage(ScalingViewport(Scaling.fit, 1000f, 800f), renderObject.batch)
        uiStage.addActor(table)

        renderable = RenderableMultiplexer(
            listOf(
                ResetRenderable(Color.BLACK),
                StageRenderable(uiStage),
            ),
        )
    }

    override fun render(delta: Float) {
        Gdx.input.inputProcessor = uiStage
        uiStage.act(delta)

        if (hostButton.isPressed) {
            val newScreen = hostServer(screenChanger, renderObject)
            screenChanger.change(newScreen)
        }
        if (connectButton.isPressed) {
            logger.info("join clicked")
        }

        renderable.render(delta)
    }

    override fun resize(width: Int, height: Int) {
        renderable.resize(width, height)
    }

    override fun dispose() {
        uiStage.dispose()
    }
}
