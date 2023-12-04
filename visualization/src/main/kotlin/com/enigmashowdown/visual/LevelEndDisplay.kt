package com.enigmashowdown.visual

import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.enigmashowdown.EnigmaShowdownConstants
import com.enigmashowdown.game.conquest.state.LevelEndStatus
import com.enigmashowdown.message.broadcast.LevelStateBroadcast
import com.enigmashowdown.visual.render.RenderObject
import com.enigmashowdown.visual.render.Renderable
import com.enigmashowdown.visual.render.RenderableMultiplexer
import com.enigmashowdown.visual.render.StageRenderable
import com.enigmashowdown.visual.update.Updatable

class LevelEndDisplay(
    renderObject: RenderObject,
    status: LevelEndStatus,
    ticks: Int,
    damageTaken: Int,
    damageGiven: Int,
    enemiesDefeated: Int,
    private val doReturnHome: () -> Unit,
    private val doRestartLevel: () -> Unit,
) : Updatable {

    val renderable: Renderable

    private val stage: Stage
    private val restartButton: Button
    private val returnButton: Button

    val inputProcessor: InputProcessor
        get() = stage

    init {
        stage = Stage(ExtendViewport(1000f, 800f), renderObject.batch)
        val textButtonStyle = renderObject.uiSkin.get(TextButton.TextButtonStyle::class.java).let {
            TextButton.TextButtonStyle(it).apply {
                over = down
            }
        }

        restartButton = TextButton("Restart", textButtonStyle)
        returnButton = TextButton("Return Home", textButtonStyle)

        val table = Table()
        table.setFillParent(true)
        table.add(
            Label(
                when (status) {
                    LevelEndStatus.COMPLETE -> "Level Complete"
                    LevelEndStatus.FAILED -> "Level Failed"
                },
                renderObject.uiSkin.get("title", Label.LabelStyle::class.java),
            ),
        )
        table.row()
        table
            .add(Label("Time: ${ticks.toFloat() / EnigmaShowdownConstants.TICKS_PER_SECOND} seconds", renderObject.uiSkin))
            .width(400f)
            .center()
            .row()
        table
            .add(Label("Damage Taken: $damageTaken hp", renderObject.uiSkin))
            .width(400f)
            .center()
            .row()
        table
            .add(Label("Damage Done: $damageGiven hp", renderObject.uiSkin))
            .width(400f)
            .center()
            .row()
        table
            .add(Label("Enemies Defeated: $enemiesDefeated", renderObject.uiSkin))
            .width(400f)
            .center()
            .row()
        table.row()
        table.add(restartButton).width(400f).row()
        table.add(returnButton).width(400f).row()

        stage.addActor(table)

        renderable = RenderableMultiplexer(
            listOf(
                StageRenderable(stage),
            ),
        )
    }

    override fun update(delta: Float, previousState: LevelStateBroadcast, currentState: LevelStateBroadcast, percent: Float) {
        stage.act(delta)
        stage.viewport.apply(true) // make sure this is centered correctly

        if (restartButton.isPressed) {
            doRestartLevel()
        }
        if (returnButton.isPressed) {
            doReturnHome()
        }
    }
}
