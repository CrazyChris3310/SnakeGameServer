package com.example.controllers

import com.example.gameModes.GameMode
import com.example.maps.GameMap
import com.example.model.Direction
import com.example.model.Point
import com.example.model.Snake
import com.example.model.food.Food
import com.example.model.food.FoodWrapper
import com.example.utils.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock

class GameController(mapName: String?, gameMode: String?) {

    private var timer = Timer()
    val lock = ReentrantReadWriteLock()
    private val snakes = Collections.synchronizedSet<Connection?>(HashSet())
    private val currentMap: GameMap = defineMap(mapName)
    private val gameMode: GameMode = defineGameMode(gameMode)
    private var foods = mutableListOf<Food>()

    suspend fun startGame() {
        if (snakes.isNotEmpty())
            return

        foods.add(gameMode.spawnFood(currentMap.edges))

        println("Scheduling timer")
        timer = Timer()
        timer.schedule( object : TimerTask() {
            override fun run() {
                lock.writeLock().lock()
                try {
                    runBlocking {
                        step()
                    }
                } finally {
                    lock.writeLock().unlock()
                }
            }
                                             } , 0, TICK_LENGTH)

        println("After coroutine scope")
    }

    fun tryStopTimer() : Boolean {
        if (snakes.isEmpty()) {
            timer.cancel()
            return true
        }
        return false
    }

    fun addSnake(session: DefaultWebSocketSession, snake: Snake) {
        lock.writeLock().lock()
        try {
            println("adding snake")
            snakes.add(Connection(session, snake))
        } finally {
            lock.writeLock().unlock()
        }
    }

    fun removeSnake(session: DefaultWebSocketSession) {
        lock.writeLock().lock()
        println("removing snake")
        try {
            snakes.removeIf { it.session == session }
        } finally {
            lock.writeLock().unlock()
        }
    }

    fun updateDirection(session: DefaultWebSocketSession, direction: Direction) {
        lock.writeLock().lock()
        try {
            snakes.find { it.session == session }?.snake?.addDirection(direction)
        } finally {
            lock.writeLock().unlock()
        }
    }

    private suspend fun step() {
        val points = LinkedList<Point>()
        if (snakes.isNotEmpty()) {
            snakes.map{ it.snake }.forEach { it ->
                if (it.intersects(foods[0].cords)) {
                    it.eat(foods[0])
                    foods[0] = gameMode.spawnFood(currentMap.edges)
                }

                var fail = false
                loop@ for (otherSnake in snakes.map { it.snake }) {
                    if (it === otherSnake)
                        continue
                    for (element in otherSnake.snake) {
                        if (it.intersects(element.getCords())) {
                            it.reborn()
                            fail = true
                            break@loop
                        }
                    }
                }

                if (!fail && it.intersectsItself()) {
                    it.reborn()
                    fail = true
                }

                if (!fail && currentMap.isIntersected(it)) {
                    it.reborn()
                }

                it.updateDirection()
                it.updateCords()

                for (element in it.snake) {
                    points.add(Point(element.getCords(), it.color))
                }
            }
            gameMode.apply(snakes.map {it.snake}, foods)
        }

        for (point in currentMap.edges) {
            points.add(Point(point, currentMap.color))
        }

        val response = Response(points, FoodWrapper(foods[0]))
        broadcast(response)
    }

    private suspend fun broadcast(response: Response) {
        val jsonResponse = Json.encodeToString(Response.serializer(), response)
        for (connection in snakes) {
            connection.session.send(jsonResponse)
        }
    }

}