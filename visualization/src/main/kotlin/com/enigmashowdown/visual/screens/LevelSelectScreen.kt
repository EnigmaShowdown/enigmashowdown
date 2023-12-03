package com.enigmashowdown.visual.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.viewport.ScalingViewport
import com.enigmashowdown.game.conquest.map.ConquestLevelInfo
import com.enigmashowdown.util.getLogger
import com.enigmashowdown.visual.render.RenderObject
import com.enigmashowdown.visual.render.Renderable
import com.enigmashowdown.visual.render.RenderableMultiplexer
import com.enigmashowdown.visual.render.ResetRenderable
import com.enigmashowdown.visual.render.StageRenderable

class LevelSelectScreen(
    private val screenChanger: ScreenChanger,
    private val renderObject: RenderObject,
) : ScreenAdapter() {
    private val logger = getLogger()

    private val level1Button: Button
    private val level2Button: Button
    private val level3Button: Button
    private val level4Button: Button
    private val level5Button: Button
    private val level6Button: Button
    private val level7Button: Button
    private val level8Button: Button
    private val level9Button: Button
    private val level10Button: Button
    private val level11Button: Button
    private val level12Button: Button
    private val level13Button: Button
    private val level14Button: Button
    private val level15Button: Button
    private val level16Button: Button

    private val uiStage: Stage

    private val renderable: Renderable

    init {
        val textButtonStyle = renderObject.uiSkin.get(TextButton.TextButtonStyle::class.java).let {
            TextButton.TextButtonStyle(it).apply {
                over = down
            }
        }
        level1Button = TextButton("1", textButtonStyle)
        level2Button = TextButton("2", textButtonStyle)
        level3Button = TextButton("3", textButtonStyle)
        level4Button = TextButton("4", textButtonStyle)
        level5Button = TextButton("5", textButtonStyle)
        level6Button = TextButton("6", textButtonStyle)
        level7Button = TextButton("7", textButtonStyle)
        level8Button = TextButton("8", textButtonStyle)
        level9Button = TextButton("9", textButtonStyle)
        level10Button = TextButton("10", textButtonStyle)
        level11Button = TextButton("11", textButtonStyle)
        level12Button = TextButton("12", textButtonStyle)
        level13Button = TextButton("13", textButtonStyle)
        level14Button = TextButton("14", textButtonStyle)
        level15Button = TextButton("15", textButtonStyle)
        level16Button = TextButton("16", textButtonStyle)

        val table = Table()
        table.setFillParent(true)
        table.add(Label("Select Level", renderObject.uiSkin.get("title", Label.LabelStyle::class.java))).colspan(4)
        table.row()
        table.add(level1Button).width(150f).height(150f)
        table.add(level2Button).width(150f).height(150f)
        table.add(level3Button).width(150f).height(150f)
        table.add(level4Button).width(150f).height(150f)
        table.row()
        table.add(level5Button).width(150f).height(150f)
        table.add(level6Button).width(150f).height(150f)
        table.add(level7Button).width(150f).height(150f)
        table.add(level8Button).width(150f).height(150f)
        table.row()
        table.add(level9Button).width(150f).height(150f)
        table.add(level10Button).width(150f).height(150f)
        table.add(level11Button).width(150f).height(150f)
        table.add(level12Button).width(150f).height(150f)
        table.row()
        table.add(level13Button).width(150f).height(150f)
        table.add(level14Button).width(150f).height(150f)
        table.add(level15Button).width(150f).height(150f)
        table.add(level16Button).width(150f).height(150f)

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

        if (level1Button.isPressed) {
            val newScreen = hostServer(screenChanger, renderObject, ConquestLevelInfo.LEVEL_1.levelId)
            screenChanger.change(newScreen)
        }
        if (level2Button.isPressed) {
            val newScreen = hostServer(screenChanger, renderObject, ConquestLevelInfo.LEVEL_2.levelId)
            screenChanger.change(newScreen)
        }
        if (level3Button.isPressed) {
            val newScreen = hostServer(screenChanger, renderObject, ConquestLevelInfo.LEVEL_3.levelId)
            screenChanger.change(newScreen)
        }
        if (level4Button.isPressed) {
            val newScreen = hostServer(screenChanger, renderObject, ConquestLevelInfo.LEVEL_4.levelId)
            screenChanger.change(newScreen)
        }
        if (level5Button.isPressed) {
            val newScreen = hostServer(screenChanger, renderObject, ConquestLevelInfo.LEVEL_5.levelId)
            screenChanger.change(newScreen)
        }
        if (level6Button.isPressed) {
            val newScreen = hostServer(screenChanger, renderObject, ConquestLevelInfo.LEVEL_6.levelId)
            screenChanger.change(newScreen)
        }
        // TODO: Add actions for all buttons when levels exist

        renderable.render(delta)
    }

    override fun resize(width: Int, height: Int) {
        renderable.resize(width, height)
    }

    override fun dispose() {
        uiStage.dispose()
    }
}
