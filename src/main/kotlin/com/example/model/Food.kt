package com.example.model

import com.example.utils.randomCoordinate
import com.example.utils.randomDirection

class Food {
    val color = "000000"
    var cords: Pair<Int, Int>

    init {
        this.cords = randomCoordinate()
    }

    fun newCords() {
        this.cords = randomCoordinate()
    }

}