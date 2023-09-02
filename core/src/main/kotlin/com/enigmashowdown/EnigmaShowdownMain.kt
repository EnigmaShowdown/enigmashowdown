@file:JvmName("EnigmaShowdownMain")

package com.enigmashowdown

fun main(args: Array<String>) {
    println("Test")
    listOf(1, 2, 5)
        .map { it + 1 }
        .forEach { println(it) }
}
