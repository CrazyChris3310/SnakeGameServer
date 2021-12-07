package com.example.controllers

import com.example.maps.Map
import com.example.model.Direction
import com.example.model.Food
import com.example.model.Point
import com.example.model.Snake
import com.example.utils.Connection
import com.example.utils.Response
import com.example.utils.TICK_LENGTH
import com.google.gson.Gson
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock

class GameController(mapName: String?) {

    private val food = Food()
    private var timer = Timer()
    val lock = ReentrantReadWriteLock()
    private val snakes = Collections.synchronizedSet<Connection?>(HashSet())
    private val currentMap: Map = when (mapName) {
        "edges" -> com.example.maps.EdgesMap()
        "tunnel" -> com.example.maps.TunnelMap()
        "apartment" -> com.example.maps.ApartmentMap()
        else -> com.example.maps.FreeMap()
    }

    suspend fun startGame() {
        if (snakes.isNotEmpty())
            return

        food.newCords()

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
                if (it.intersects(food.cords)) {
                    food.newCords(currentMap.edges)
                    it.grow()
                    if (it.speed > 10 && it.getSize() % 10 == 0) {
                        it.speed -= 10
                    }
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
        }
        points.add(Point(food.cords, food.color))

        for (point in currentMap.edges) {
            points.add(Point(point, currentMap.color))
        }

        val response = Response(points)
        broadcast(response)
    }

    private suspend fun broadcast(response: Response) {
        val gson = Gson()
        val jsonResponse = gson.toJson(response)
        for (connection in snakes) {
            connection.session.send(jsonResponse)
        }
    }

}