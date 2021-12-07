package com.example.model

import com.example.utils.ELEMENT_SIZE

class SnakeElement(cords: Pair<Int, Int>, var direction: Direction) {

    var x = cords.first
    var y = cords.second

    fun changeCords(cords: Pair<Int, Int>) {
        x = cords.first
        y = cords.second
    }

    fun getCords() = Pair(x, y)

    fun move() {
        x += direction.horizontalKey * ELEMENT_SIZE
        y += direction.verticalKey * ELEMENT_SIZE
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as SnakeElement

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }


}