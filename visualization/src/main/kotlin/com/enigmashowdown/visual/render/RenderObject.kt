package com.enigmashowdown.visual.render

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Disposable

class RenderObject(
    val batch: Batch,
    val mainSkin: Skin,
    val uiSkin: Skin,
) : Disposable {
    override fun dispose() {
        batch.dispose()
        mainSkin.dispose()
        uiSkin.dispose()
    }
}
