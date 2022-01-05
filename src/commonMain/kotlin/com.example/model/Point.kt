package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class Point(val x: Int, val y: Int, val color: String) {
    constructor(cords: Pair<Int, Int>, color: String) :
            this(cords.first, cords.second, color)

}