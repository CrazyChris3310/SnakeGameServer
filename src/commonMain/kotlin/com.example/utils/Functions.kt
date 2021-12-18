package com.example.utils

import com.example.model.Direction

const val FIELD_WIDTH = 1200
const val FIELD_HEIGHT = 680
const val ELEMENT_SIZE = 10
const val TICK_LENGTH = 10L
const val DEFAULT_SPEED = 50

fun randomInt(min: Int, max: Int): Int = (min until max).random()

fun randomCoordinate(): Pair<Int, Int> {
    var x = randomInt(0, FIELD_WIDTH)
    var y = randomInt(0, FIELD_HEIGHT)
    x -= x % ELEMENT_SIZE
    y -= y % ELEMENT_SIZE
    return Pair(x, y)
}

fun randomDirection(): Direction = Direction.values().random()

fun divideCords(first: Int, second: Int): Int {
    return first / ELEMENT_SIZE / second * ELEMENT_SIZE
}

fun convertDirection(direction: String): Direction? = when (direction) {
    "UP" -> Direction.UP
    "DOWN" -> Direction.DOWN
    "RIGHT" -> Direction.RIGHT
    "LEFT" -> Direction.LEFT
    else -> null
}