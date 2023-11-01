package com.enigmashowdown.visual

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.viewport.ScalingViewport
import com.enigmashowdown.EnigmaShowdownConstants
import com.enigmashowdown.visual.render.RenderObject
import com.enigmashowdown.visual.render.Renderable
import com.enigmashowdown.visual.render.RenderableMultiplexer
import com.enigmashowdown.visual.render.StageRenderable

class LevelEndDisplay(
    renderObject: RenderObject,
    ticks: Int,
    damageTaken: Int,
    damageGiven: Int,
    enemiesDefeated: Int,
) {

    val renderable: Renderable
    // val inputProcessor: InputProcessor

    private val stage: Stage
    private val restartButton: Button
    private val returnButton: Button
    private val complete: Actor

    init {
        stage = Stage(ScalingViewport(Scaling.fit, 1000f, 800f), renderObject.batch)
        val textButtonStyle = renderObject.uiSkin.get(TextButton.TextButtonStyle::class.java).let {
            TextButton.TextButtonStyle(it).apply {
                over = down
            }
        }

        restartButton = TextButton("Restart", textButtonStyle)
        returnButton = TextButton("Return Home", textButtonStyle)

        val table = Table()
        table.setFillParent(true)
        val textFieldStyle = renderObject.uiSkin.get(TextField.TextFieldStyle::class.java)
        table.add(Label("Level Complete", renderObject.uiSkin.get("title", Label.LabelStyle::class.java)))
        table.row()
        val time = Label("Time Taken: ${ticks.toFloat() / EnigmaShowdownConstants.TICKS_PER_SECOND}s", renderObject.uiSkin)
        val damageTakenLabel = Label("Damage Taken: $damageTaken hp", renderObject.uiSkin)
        val damageGivenLabel = Label("Damage Done: $damageGiven hp", renderObject.uiSkin)
        val enemiesDefeatedLabel = Label("Enemies Defeated: $enemiesDefeated", renderObject.uiSkin)
        time.setAlignment(Align.center)
        damageGivenLabel.setAlignment(Align.center)
        damageTakenLabel.setAlignment(Align.center)
        enemiesDefeatedLabel.setAlignment(Align.center)
        table.add(time).width(400f).row()
        table.add(damageTakenLabel).width(400f).row()
        table.add(damageGivenLabel).width(400f).row()
        table.add(enemiesDefeatedLabel).width(400f).row()
        table.row()
        table.add(restartButton).width(400f).row()
        table.add(returnButton).width(400f).row()
        complete = TextField("Level Complete!", textFieldStyle)
        complete.alignment = Align.center

        stage.addActor(table)

        renderable = RenderableMultiplexer(
            listOf(
                StageRenderable(stage),
            ),
        )
    }

    fun render(delta: Float) {
        Gdx.input.inputProcessor = stage
        stage.act(delta)

        if (returnButton.isPressed) {
        }
        if (restartButton.isPressed) {
        }

        renderable.render(delta)
    }
}
