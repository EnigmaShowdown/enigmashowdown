package com.enigmashowdown.visual

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Disposable
import com.enigmashowdown.EnigmaShowdownConstants
import com.enigmashowdown.game.conquest.state.ConquestStateView
import com.enigmashowdown.game.conquest.state.EntityState
import com.enigmashowdown.game.conquest.state.EntityType
import com.enigmashowdown.message.broadcast.LevelStateBroadcast
import com.enigmashowdown.util.getLogger
import com.enigmashowdown.util.toVector2
import com.enigmashowdown.visual.render.RenderObject
import com.enigmashowdown.visual.update.Updatable
import java.util.UUID
import kotlin.math.max

private class AnimationFrames(
    val frames: List<Drawable>,

    val frameLengthEquation: AnimationFrameLengthEquation,
)
private fun interface AnimationFrameLengthEquation {
    fun getFrameLengthSeconds(entitySpeed: Float): Float
}

private sealed interface EntityAnimation

private class Moving4WayAnimation(
    val still: AnimationFrames,

    val up: AnimationFrames,
    val down: AnimationFrames,
    val left: AnimationFrames,
    val right: AnimationFrames,
) : EntityAnimation

private class BasicAnimation(
    val frames: AnimationFrames,
) : EntityAnimation

private class EntitySprite(
    val setDrawable: (Drawable) -> Unit,
    val animation: EntityAnimation,
) : Disposable {
    val group = Group()

//    init {
//        group.addActor(Actor().apply { // debug actor
//            setSize(0.1f, 0.1f)
//        })
//    }

    var currentFrames: AnimationFrames? = null
    var currentFrameIndex: Int = 0
    var frameEndSeconds: Float = 0f // initializing to 0 is fine because its value should not be used when currentFrames is null

    override fun dispose() {
        group.remove() // remove actor from the stage it should have been added to
    }
}

class EntitySpriteManager(
    private val renderObject: RenderObject,
    private val stage: Stage,
) : Updatable, Disposable {

    private val map = mutableMapOf<UUID, EntitySprite>()

    private fun findOrInitSprite(id: UUID, entityType: EntityType): EntitySprite {
        return map.computeIfAbsent(id) { _ ->
            when (entityType) {
                EntityType.PLAYER -> {
                    val image = Image().apply {
                        setSize(1.0f, 2.0f)
                        setPosition(-0.5f, -0.5f) // set the position relative to the group of this entity (remember, the group is what moves, not this image)
                    }
                    val stillFrameLength = AnimationFrameLengthEquation { 0.25f }
                    val movingFrameLength = AnimationFrameLengthEquation { entitySpeed ->
                        max(0.1f, 0.3f - entitySpeed / 10f)
                    }

                    val up = renderObject.mainSkin.atlas.findRegions("up").map { TextureRegionDrawable(it) }
                    val down = renderObject.mainSkin.atlas.findRegions("down").map { TextureRegionDrawable(it) }
                    val flippedDown = renderObject.mainSkin.atlas.findRegions("down").map { atlasRegion ->
                        val clonedRegion = AtlasRegion(atlasRegion)
                        clonedRegion.flip(true, false)
                        TextureRegionDrawable(clonedRegion)
                    }

                    EntitySprite(
                        { drawable -> image.drawable = drawable },
                        Moving4WayAnimation(
                            AnimationFrames(renderObject.mainSkin.atlas.findRegions("still").map { TextureRegionDrawable(it) }, stillFrameLength),

                            AnimationFrames(up, movingFrameLength),
                            AnimationFrames(down, movingFrameLength),
                            AnimationFrames(flippedDown, movingFrameLength), // left
                            AnimationFrames(down, movingFrameLength), // right
                        ),
                    ).also { sprite ->
                        sprite.group.addActor(image)
                    }
                }
                EntityType.CRATE -> {
                    val image = Image().apply {
                        setSize(1.0f, 1.0f)
                        setPosition(-0.5f, -0.5f)
                    }
                    EntitySprite(
                        { drawable -> image.drawable = drawable },
                        BasicAnimation(
                            AnimationFrames(
                                listOf(renderObject.mainSkin.getDrawable("wooden_crate")),
                            ) { 1.0f }, // the frame length does not matter, since there is only one frame
                        ),
                    ).also { sprite ->
                        sprite.group.addActor(image)
                    }
                }
                EntityType.FLAG -> {
                    val image = Image().apply {
                        setSize(1.0f, 2.0f)
                        setPosition(-0.5f, -0.5f) // set the position relative to the group of this entity (remember, the group is what moves, not this image)
                    }
                    val regions = renderObject.mainSkin.atlas.findRegions("flag").map { TextureRegionDrawable(it) }
                    val frames = regions + regions.subList(1, regions.size - 1).reversed() // this makes it cycle in a "bounce"
                    EntitySprite(
                        { drawable -> image.drawable = drawable },
                        BasicAnimation(
                            AnimationFrames(frames) { 0.2f },
                        ),
                    ).also { sprite ->
                        sprite.group.addActor(image)
                    }
                }
                EntityType.PRESSURE_PLATE -> {
                    val image = Image().apply {
                        setSize(1.0f, 1.0f)
                        setPosition(-0.5f, -0.5f)
                    }
                    EntitySprite(
                        { drawable -> image.drawable = drawable },
                        BasicAnimation(
                            AnimationFrames(
                                listOf(renderObject.mainSkin.getDrawable("test-plate")),
                            ) { 1.0f }, // the frame length does not matter, since there is only one frame
                        ),
                    ).also { sprite ->
                        sprite.group.addActor(image)
                    }
                }
//                else -> error("Unsupported entity type! Please add this entity type to the list of entity types that this function returns null for!")
            }
        }
    }

    private fun updateEntity(tick: Int, percent: Float, id: UUID, entity: EntityState?, nextEntity: EntityState?) {
        require(entity != null || nextEntity != null) { "Both entities are null for id: $id" }
        val anyEntity = (entity ?: nextEntity)!!
        val entityType = anyEntity.entityType
        val sprite = findOrInitSprite(id, entityType) ?: return // do nothing if sprite is null

        val position = if (entity != null && nextEntity != null) entity.position.toVector2().lerp(nextEntity.position.toVector2(), percent) else anyEntity.position.toVector2()
        stage.addActor(sprite.group)
        sprite.group.setPosition(position.x, position.y)

        val changeInPosition = if (entity != null && nextEntity != null) nextEntity.position.toVector2().sub(entity.position.toVector2()) else Vector2.Zero
        val velocity = Vector2(changeInPosition.x / EnigmaShowdownConstants.TICK_PERIOD_SECONDS, changeInPosition.y / EnigmaShowdownConstants.TICK_PERIOD_SECONDS)
        val speed = velocity.len()
        val desiredAnimationFrames = when (val animation = sprite.animation) {
            is Moving4WayAnimation -> {
                val angleDegrees = velocity.angleDeg() // in the range 0-360
                when {
                    speed < 0.5f -> animation.still
                    angleDegrees < 45 || angleDegrees > (360 - 45) -> animation.right
                    angleDegrees < (90 + 45) -> animation.up
                    angleDegrees < (180 + 45) -> animation.left
                    else -> animation.down
                }
            }
            is BasicAnimation -> {
                animation.frames
            }
        }

        val timeSeconds = (tick + percent) / EnigmaShowdownConstants.TICKS_PER_SECOND
        if (sprite.currentFrames != desiredAnimationFrames) {
            sprite.currentFrameIndex = 0
            sprite.currentFrames = desiredAnimationFrames
            sprite.frameEndSeconds = desiredAnimationFrames.frameLengthEquation.getFrameLengthSeconds(speed)
        }
        if (sprite.frameEndSeconds < timeSeconds) {
            sprite.currentFrameIndex = (sprite.currentFrameIndex + 1) % desiredAnimationFrames.frames.size
            sprite.frameEndSeconds = timeSeconds + desiredAnimationFrames.frameLengthEquation.getFrameLengthSeconds(speed)
        }
        val currentFrame = desiredAnimationFrames.frames[sprite.currentFrameIndex]
        sprite.setDrawable(currentFrame)
    }

    override fun update(delta: Float, previousState: LevelStateBroadcast, currentState: LevelStateBroadcast, percent: Float) {
        val stateView = previousState.gameStateView as ConquestStateView
        val nextStateView = currentState.gameStateView as ConquestStateView

        val entityMap = stateView.entities.associateBy { it.id }
        val nextEntityMap = nextStateView.entities.associateBy { it.id }
        val allEntityIds: Set<UUID> = entityMap.keys.toMutableSet().apply { addAll(entityMap.keys) }
        for (id in allEntityIds) {
            updateEntity(stateView.tick, percent, id, entityMap[id], nextEntityMap[id])
        }
        // Actors on the bottom should be in front
        stage.actors.asSequence().sortedByDescending { actor -> actor.y }.forEach { actor -> actor.toFront() }

        // For any entity that is removed, let's remove that entity from our map
        map.entries.iterator().let { iterator ->
            while (iterator.hasNext()) {
                val (id, sprite) = iterator.next()
                if (id !in allEntityIds) {
                    sprite.dispose()
                    iterator.remove()
                }
            }
        }
    }

    override fun dispose() {
    }

    private companion object {
        private val logger = getLogger()
    }
}
