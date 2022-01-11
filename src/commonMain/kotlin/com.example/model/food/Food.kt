package com.example.model.food
import com.example.utils.randomCoordinate

interface Food {
    var cords: Pair<Int, Int>
    val speedAdd: Int
    val lengthAdd: Int
    val kills: Boolean
    val type: FoodType
}

abstract class FoodAbstr(edges: List<Pair<Int, Int>>) : Food {
    final override var cords: Pair<Int, Int>
    override val type: FoodType = FoodType.APPLE
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
    override val speedAdd: Int = 1
    override val lengthAdd: Int = 1
    override val kills: Boolean = false
    override val type: FoodType = FoodType.APPLE
}

class PineApple(edges: List<Pair<Int, Int>> = ArrayList()) : FoodAbstr(edges) {
    override val speedAdd: Int = 0
    override val lengthAdd: Int = 3
    override val kills: Boolean = false
    override val type: FoodType = FoodType.PINEAPPLE
}

class Banana(edges: List<Pair<Int, Int>> = ArrayList()) : FoodAbstr(edges), FixedLifeTimed {
    override val speedAdd: Int = 10
    override val lengthAdd: Int = 0
    override val kills: Boolean = false
    override val type: FoodType = FoodType.BANANA
}

class Poison(edges: List<Pair<Int, Int>> = ArrayList()) : FoodAbstr(edges), FixedLifeTimed {
    override val speedAdd: Int = 0
    override val lengthAdd: Int = 0
    override val kills: Boolean = true
    override val type: FoodType = FoodType.POISON
}

class Watermelon(edges: List<Pair<Int, Int>> = ArrayList()) : FoodAbstr(edges), FixedLifeTimed {
    override val speedAdd: Int = -10
    override val lengthAdd: Int = 0
    override val kills: Boolean = false
    override val type: FoodType = FoodType.WATERMELON
}