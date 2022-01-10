package com.example.model.food

import com.example.utils.randomCoordinate

interface Food {
    var cords: Pair<Int, Int>
    val speedAdd: Int
    val lengthAdd: Int
    val kills: Boolean
}

abstract class FoodAbstr(edges: List<Pair<Int, Int>>) : Food {
    final override var cords: Pair<Int, Int>
    init {
        loop@ while (true) {
            cords = randomCoordinate()
            for (point in edges) {
                if (point == cords) {
                    continue@loop
                }
            }
            break
        }
    }
}

class Apple(edges: List<Pair<Int, Int>> = ArrayList()) : FoodAbstr(edges) {
    override val speedAdd: Int = 0
    override val lengthAdd: Int = 1
    override val kills: Boolean = false
}

class PineApple(edges: List<Pair<Int, Int>> = ArrayList()) : FoodAbstr(edges) {
    override val speedAdd: Int = 0
    override val lengthAdd: Int = 3
    override val kills: Boolean = false
}

class Banana(edges: List<Pair<Int, Int>> = ArrayList()) : FoodAbstr(edges) {
    override val speedAdd: Int = 10
    override val lengthAdd: Int = 0
    override val kills: Boolean = false
}

class Poison(edges: List<Pair<Int, Int>> = ArrayList()) : FoodAbstr(edges) {
    override val speedAdd: Int = 0
    override val lengthAdd: Int = 0
    override val kills: Boolean = true
}

//class AbstractFood {
//    val color = "000000"
//    var cords: Pair<Int, Int>
//
//    init {
//        this.cords = randomCoordinate()
//    }
//
//    fun newCords(edges: List<Pair<Int, Int>> = ArrayList()) {
//        loop@ while (true) {
//            cords = randomCoordinate()
//            for (point in edges) {
//                if (point == cords) {
//                    continue@loop
//                }
//            }
//            break
//        }
//    }
//
//}