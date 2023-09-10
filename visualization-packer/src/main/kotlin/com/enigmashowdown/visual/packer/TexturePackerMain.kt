@file:JvmName("TexturePackerMain")

package com.enigmashowdown.visual.packer

import com.badlogic.gdx.tools.texturepacker.TexturePacker
import com.badlogic.gdx.tools.texturepacker.TexturePackerFileProcessor
import java.io.File

fun main(args: Array<String>) {
    val processor = TexturePackerFileProcessor(TexturePacker.Settings(), "skin", null)
    processor.process(File("raw-assets/textures"), File("output-assets/skins/main"))
}
