package com.example.model

import junit.framework.TestCase
import kotlin.test.assertNotEquals

class SnakeElementTest : TestCase() {

    fun testTestEquals() {

        val el1 = SnakeElement(Pair(20, 120), Direction.DOWN)
        val el2 = SnakeElement(Pair(40, 180), Direction.DOWN)
        val el3 = SnakeElement(Pair(20, 120), Direction.DOWN)

        assertNotEquals(el1, el2)
        assertEquals(el1, el3)

    }

    fun testSelfIntersection() {
        val snake = Snake("Blue", 40);
        snake.grow()
        snake.grow()
        snake.grow()
    }
}