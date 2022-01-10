package com.example.gameModes

import com.example.model.Snake
import com.example.model.food.*
import kotlin.random.Random

interface GameMode {
    fun spawnFood(edges: List<Pair<Int, Int>>): Food
    fun apply(snakes: List<Snake>, food: Food)
}

abstract class AbstractGameMode : GameMode {
    override fun spawnFood(edges: List<Pair<Int, Int>>): Food {
        return Apple(edges)
    }
}

class DefaultGameMode : AbstractGameMode() {
    override fun apply(snakes: List<Snake>, food: Food) {}
}

class DiverseFoodGameMode : AbstractGameMode() {
    override fun apply(snakes: List<Snake>, food: Food) {
        TODO("Not yet implemented")
    }

    override fun spawnFood(edges: List<Pair<Int, Int>>): Food {
        val rand = Random.nextDouble()
        return if (rand < 0.25)
            Apple(edges)
        else if (rand < 0.5)
            PineApple(edges)
        else if (rand < 0.75)
            Banana(edges)
        else
            Poison(edges)
    }
}

class RandomSpeedGameMode : AbstractGameMode() {
    override fun apply(snakes: List<Snake>, food: Food) {
        TODO("Not yet implemented")
    }
}