package com.example.gameModes

import com.example.maps.FreeMap
import com.example.maps.GameMap
import com.example.model.Snake
import com.example.model.food.*
import com.example.utils.randomSpeed
import io.ktor.util.date.*
import kotlin.random.Random

interface GameMode {
    var map: GameMap
    fun spawnFood(edges: List<Pair<Int, Int>>): Food
    fun apply(snakes: List<Snake>, foods: MutableList<Food>)
}

abstract class AbstractGameMode(override var map: GameMap = FreeMap()) : GameMode {
    override fun spawnFood(edges: List<Pair<Int, Int>>): Food {
        return Apple(edges)
    }
}

class DefaultGameMode : AbstractGameMode() {
    override fun apply(snakes: List<Snake>, foods: MutableList<Food>) {}
}

class DiverseFoodGameMode : AbstractGameMode() {
    val TIME_INTERVAL_MS = 10 * 1000
    var start = 0L

    override fun apply(snakes: List<Snake>, foods: MutableList<Food>) {
        if (foods[0] is FixedLifeTimed) {
            if (getTimeMillis() >= start + TIME_INTERVAL_MS) {
                foods[0] = spawnFood(map.edges)
            }
        }
    }

    override fun spawnFood(edges: List<Pair<Int, Int>>): Food {
        val rand = Random.nextDouble()
        start = getTimeMillis()
        return when {
            rand < 0.6 -> Apple(edges)
            rand < 0.716 -> PineApple(edges)
            rand < 0.832 -> Watermelon(edges)
            rand < 0.95 -> Banana(edges)
            else -> Poison(edges)
        }
    }
}

class RandomSpeedGameMode : AbstractGameMode() {
    private val TIME_INTERVAL_MS = 20 * 1000
    var start = getTimeMillis()

    override fun apply(snakes: List<Snake>, foods: MutableList<Food>) {
        if (getTimeMillis() < start + TIME_INTERVAL_MS)
            return
        for (snake in snakes) {
            snake.speed  = randomSpeed()
        }
        start = getTimeMillis()
    }
}