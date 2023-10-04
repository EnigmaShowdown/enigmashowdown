package com.enigmashowdown.visual

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.viewport.ScalingViewport
import com.enigmashowdown.game.conquest.state.ConquestStateView
import com.enigmashowdown.message.broadcast.LevelStateBroadcast
import com.enigmashowdown.util.getLogger
import com.enigmashowdown.visual.render.RenderObject
import com.enigmashowdown.visual.render.Renderable
import com.enigmashowdown.visual.render.RenderableMultiplexer
import com.enigmashowdown.visual.render.StageRenderable
import com.enigmashowdown.visual.update.Updatable

/**
 * This class should display a 3, 2, 1, GO! on the screen right before the level is about to start
 */
class LevelCountdown(
    renderObject: RenderObject,
) : Updatable {
    // TODO implement this class

    val renderable: Renderable
    private val stage: Stage
    // maybe declare some images here

    init {
        // consider initializing a stage here
        // maybe initialize those images here
        // Notice the world width and world height, these values don't matter since this stage is only for the countdown,
        //   but some values are relative to the world width and height, such as the call to setBounds() below
        stage = Stage(ScalingViewport(Scaling.fit, 10f, 10f), renderObject.batch)
//        stage.addActor(Image(renderObject.mainSkin.getDrawable("countdown/3")).apply {
//            setBounds(-.5f, -.5f, 1f, 1f)
//        })
        renderable = RenderableMultiplexer(
            listOf(
                StageRenderable(stage),
            ),
        )
    }

    override fun update(delta: Float, previousState: LevelStateBroadcast, currentState: LevelStateBroadcast, percent: Float) {
        // consider updating a stage here
        // If you have images as member variables, you could add/remove/hide/whatever them to the stage depending on the data you are provided

        val state = previousState.gameStateView as ConquestStateView

        if (previousState.ticksUntilBegin > 0) {
            logger.info("Ticks until begin: {}", previousState.ticksUntilBegin)
        }

        previousState.ticksUntilBegin // You can access this, which tells you how many ticks until the level begins.
        state.tick // you can access state.tick, which will be 0 until the level starts. You only have to use this when displaying "GO!"
        // Remember there are 10 ticks per second.
        // However, instead of using 30, 20, 10 as constants, use 3 * EnigmaShowdownConstants.TICKS_PER_SECOND, 2 * ..., etc.

        // It would be great if the images faded away as time goes on.
        // For instance, "3" is visible at tick 30, but is mostly gone by tick 21.
    }

    private companion object {
        private val logger = getLogger()
    }
}
