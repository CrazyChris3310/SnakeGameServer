package com.example.model

import com.example.utils.*
import java.util.*

class Snake(val color: String, speed: Int) {

    val snake: MutableList<SnakeElement> = ArrayList()
    val directionQueue: Queue<Direction> = LinkedList()
    var count = speed
    var direction: Direction = randomDirection()
    var speed: Int = speed
        set(value) {
            field = value
            count = value
        }

    init {
        reborn()
    }

    fun createHead() {
        val cords = randomCoordinate()
        val head = SnakeElement(cords, direction)
        snake.add(head)
    }

    fun reborn() {
        snake.clear()
        direction = randomDirection()
        createHead()
        grow()
        grow()
        speed = DEFAULT_SPEED
    }

    fun getSize() = snake.size

    fun addDirection(dir: Direction) = directionQueue.add(dir)

    fun updateDirection() {
        if (count != speed) return
        val dir = directionQueue.poll() ?: return
        if (!direction.isOpposite(dir)) {
            direction = dir
        }
    }

    fun grow() {
        var newCords = snake.last().getCords()
        val dir = snake.last().direction
        newCords = Pair(
            newCords.first - dir.horizontalKey * ELEMENT_SIZE,
            newCords.second - dir.verticalKey * ELEMENT_SIZE
        )
        snake.add(SnakeElement(newCords, dir))
    }

    fun updateCords() {
        count -= TICK_LENGTH.toInt()
        if (count != 0) return
        count = speed

        for (i in snake.size - 1 downTo 1) {
            snake[i].direction = snake[i-1].direction
            snake[i].changeCords(snake[i-1].getCords())
        }

        val head = snake.first()
        head.direction = direction
        head.move()

        when {
            head.x < 0 -> head.x = FIELD_WIDTH - ELEMENT_SIZE
            head.x >= FIELD_WIDTH -> head.x = 0
            head.y < 0 -> head.y = FIELD_HEIGHT - ELEMENT_SIZE
            head.y >= FIELD_HEIGHT -> head.y = 0
        }
    }

    fun intersects(cords: Pair<Int, Int>): Boolean = snake.first().getCords() == cords

    fun intersectsItself(): Boolean {
        for (i in 1 until snake.size) {
            if (snake[i] == snake.first()) {
                return true
            }
        }
        return false
    }
}