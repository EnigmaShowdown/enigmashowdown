package com.enigmashowdown.visual

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.viewport.ScalingViewport
import com.enigmashowdown.EnigmaShowdownConstants
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
    private val three: Actor
    private val two: Actor
    private val one: Actor
    private val go: Actor
    // maybe declare some images here

    init {
        // consider initializing a stage here
        // maybe initialize those images here
        // Notice the world width and world height, these values don't matter since this stage is only for the countdown,
        //   but some values are relative to the world width and height, such as the call to setBounds() below
        stage = Stage(ScalingViewport(Scaling.fit, 10f, 10f), renderObject.batch)

        three = Image(renderObject.mainSkin.getDrawable("3")).apply {
            setBounds(4f, 4f, 2f, 2f)
        }
        two = Image(renderObject.mainSkin.getDrawable("2")).apply {
            setBounds(4f, 4f, 2f, 2f)
        }
        one = Image(renderObject.mainSkin.getDrawable("1")).apply {
            setBounds(4f, 4f, 2f, 2f)
        }
        go = Image(renderObject.mainSkin.getDrawable("go")).apply {
            setBounds(3f, 4f, 4f, 2f)
        }
        three.color.a = 0f
        two.color.a = 0f
        one.color.a = 0f
        go.color.a = 0f
        stage.addActor(three)
        stage.addActor(two)
        stage.addActor(one)
        stage.addActor(go)

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

        if (previousState.ticksUntilBegin > 4 * EnigmaShowdownConstants.TICKS_PER_SECOND) {
            // Resetting all countdown assets to be transparent for ticks 50-40
            three.color.a = 0f
            two.color.a = 0f
            one.color.a = 0f
            go.color.a = 0f
        } else if (previousState.ticksUntilBegin > 3 * EnigmaShowdownConstants.TICKS_PER_SECOND) {
            // Display number 3 from ticks 40-30, with the alpha channel going from 1.0-0.0 in the 10 ticks
            three.color.a = ((previousState.ticksUntilBegin - percent) % EnigmaShowdownConstants.TICKS_PER_SECOND) / EnigmaShowdownConstants.TICKS_PER_SECOND.toFloat()
        } else if (previousState.ticksUntilBegin > 2 * EnigmaShowdownConstants.TICKS_PER_SECOND) {
            // Make sure 3 is completely hidden when displaying 2
            three.color.a = 0f
            // Display number 2 from ticks 30-20, with the alpha channel going from 1.0-0.0 in the 10 ticks
            two.color.a = ((previousState.ticksUntilBegin - percent) % EnigmaShowdownConstants.TICKS_PER_SECOND).toFloat() / EnigmaShowdownConstants.TICKS_PER_SECOND.toFloat()
        } else if (previousState.ticksUntilBegin > 1 * EnigmaShowdownConstants.TICKS_PER_SECOND) {
            // Make sure 2 is completely hidden when displaying 1
            two.color.a = 0f
            // Display number 1 from ticks 20-10, with the alpha channel going from 1.0-0.0 in the 10 ticks
            one.color.a = ((previousState.ticksUntilBegin - percent) % EnigmaShowdownConstants.TICKS_PER_SECOND).toFloat() / EnigmaShowdownConstants.TICKS_PER_SECOND.toFloat()
        } else if (previousState.ticksUntilBegin > 0 * EnigmaShowdownConstants.TICKS_PER_SECOND) {
            // Make sure 1 is completely hidden when displaying GO!
            one.color.a = 0f
            // Display GO! from ticks 10-0, with the alpha channel going from 1.0-0.0 in the 10 ticks
            go.color.a = ((previousState.ticksUntilBegin - percent) % EnigmaShowdownConstants.TICKS_PER_SECOND).toFloat() / EnigmaShowdownConstants.TICKS_PER_SECOND.toFloat()
        } else {
            // Make sure GO! is hidden when countdown ticks are over
            go.color.a = 0f
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
