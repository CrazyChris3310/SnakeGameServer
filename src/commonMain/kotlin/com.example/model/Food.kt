package com.example.model

import com.example.utils.randomCoordinate

class Food {
    val color = "000000"
    var cords: Pair<Int, Int>

    init {
        this.cords = randomCoordinate()
    }

    fun newCords(edges: List<Pair<Int, Int>> = ArrayList()) {
        loop@ while (true) {
            cords = randomCoordinate()
            for (point in edges) {
                if (point == cords) {
                    continue@loop;
                }
            }
            break;
        }
    }

}