package com.enigmashowdown.visual

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.enigmashowdown.visual.render.RenderObject
import com.enigmashowdown.visual.render.Renderable
import com.enigmashowdown.visual.render.RenderableMultiplexer
import com.enigmashowdown.visual.render.StageRenderable

class HealthClassManager(
    renderObject: RenderObject,
) {
    private val barWidth = 100f
    private val barHeight = 10f
    private val healthLabel: Label
    val renderable: Renderable
    private val stage: Stage

    init {
        stage = Stage(ExtendViewport(1000f, 800f), renderObject.batch)
        healthLabel = Label("100/100 hp", renderObject.uiSkin)
        healthLabel.setAlignment(Align.center)
        val x = 50
        val y = 50
        healthLabel.setPosition(x.toFloat(), y.toFloat())

        stage.addActor(healthLabel)

        renderable = RenderableMultiplexer(
            listOf(
                StageRenderable(stage),
            ),
        )
    }

    fun update(currentHealth: Int, totalHealth: Int) {
        if (currentHealth >= 0) {
            healthLabel.setText("$currentHealth/$totalHealth hp")
        }

        // setPosition(playerposition.x - barWidth / 2, playerposition.y + 2.0f)
        // healthLabel.setPosition(playerposition.x - barWidth / 2, playerposition.y - barHeight)
    }
}
