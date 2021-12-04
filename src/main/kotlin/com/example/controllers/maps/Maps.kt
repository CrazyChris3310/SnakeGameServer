package com.example.controllers.maps

import com.example.model.Snake
import com.example.utils.ELEMENT_SIZE
import com.example.utils.FIELD_HEIGHT
import com.example.utils.FIELD_WIDTH
import com.example.utils.divideCords

interface Map {
    val edges: MutableList<Pair<Int, Int>>
    val color: String
    fun isIntersected(snake: Snake): Boolean
}

abstract class AbstractMap(val name: String) : Map {
    override val edges: MutableList<Pair<Int, Int>> = ArrayList()
    override val color = "000000"

    override fun isIntersected(snake: Snake): Boolean {
        val headCords = snake.getHead().getCords()
        for (point in edges) {
            if (point == headCords)
                return true
        }
        return false
    }
}

class FreeMap: AbstractMap("free") {
    override fun isIntersected(snake: Snake): Boolean = false
}

class EdgesMap: AbstractMap("edges") {

    init {
        for (i in 0 until FIELD_WIDTH step ELEMENT_SIZE) {
            edges.add(Pair(i, 0))
            edges.add(Pair(i, FIELD_HEIGHT - ELEMENT_SIZE))
        }
        for (i in ELEMENT_SIZE until FIELD_HEIGHT - ELEMENT_SIZE step ELEMENT_SIZE) {
            edges.add(Pair(0, i))
            edges.add(Pair(FIELD_WIDTH - ELEMENT_SIZE, i))
        }

    }
}

class TunnelMap: AbstractMap("tunnel") {
    init {
        for (i in 0 until FIELD_HEIGHT / 4 step ELEMENT_SIZE) {
            edges.add(Pair(i, 0))
            edges.add(Pair(i, FIELD_HEIGHT - ELEMENT_SIZE))
            edges.add(Pair(FIELD_WIDTH - i - ELEMENT_SIZE, 0))
            edges.add(Pair(FIELD_WIDTH - i - ELEMENT_SIZE, FIELD_HEIGHT - ELEMENT_SIZE))
        }
        for (i in ELEMENT_SIZE until FIELD_HEIGHT / 4 - ELEMENT_SIZE step ELEMENT_SIZE) {
            edges.add(Pair(0, i))
            edges.add(Pair(FIELD_WIDTH - ELEMENT_SIZE, i))
            edges.add(Pair(0, FIELD_HEIGHT - ELEMENT_SIZE - i))
            edges.add(Pair(FIELD_WIDTH - ELEMENT_SIZE, FIELD_HEIGHT - i - ELEMENT_SIZE))
        }
        val temp = divideCords(FIELD_HEIGHT, 4)
        for (i in temp until FIELD_WIDTH - temp - ELEMENT_SIZE step ELEMENT_SIZE) {
            edges.add(Pair(i, temp))
            edges.add(Pair(i, FIELD_HEIGHT - temp))
        }
    }
}

class ApartmentMap: AbstractMap("apartment") {
    init {
        for (i in 0 until FIELD_WIDTH - ELEMENT_SIZE step ELEMENT_SIZE) {
            edges.add(Pair(i, FIELD_HEIGHT - divideCords(FIELD_HEIGHT, 3)))
        }
        for (i in 0 until FIELD_WIDTH / 2 - 5 * ELEMENT_SIZE step ELEMENT_SIZE) {
            edges.add(Pair(i, divideCords(FIELD_HEIGHT, 3)))
            edges.add(Pair(FIELD_WIDTH - i -  ELEMENT_SIZE, divideCords(FIELD_HEIGHT, 3)))
        }
        for (i in 0 until 20 * ELEMENT_SIZE step ELEMENT_SIZE) {
            edges.add(Pair(i, 0))
        }
        for (i in 35 * ELEMENT_SIZE until FIELD_WIDTH - 20 * ELEMENT_SIZE step ELEMENT_SIZE) {
            edges.add(Pair(i, 0))
        }
        for (i in ELEMENT_SIZE until ELEMENT_SIZE * 15 step ELEMENT_SIZE) {
            edges.add(Pair(0, i))
        }
        for (i in ELEMENT_SIZE until FIELD_HEIGHT / 3 step ELEMENT_SIZE) {
            edges.add(Pair(divideCords(FIELD_WIDTH, 2) - 5 * ELEMENT_SIZE, i))
        }
        for (i in FIELD_HEIGHT - divideCords(FIELD_HEIGHT, 3) + ELEMENT_SIZE until FIELD_HEIGHT step ELEMENT_SIZE) {
            edges.add(Pair(FIELD_WIDTH - divideCords(FIELD_WIDTH, 2) + 4 * ELEMENT_SIZE, i))
        }
    }
}