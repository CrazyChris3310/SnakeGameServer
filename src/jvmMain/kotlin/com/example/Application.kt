package com.example

import com.example.controllers.GameController
import com.example.model.Snake
import com.example.utils.DEFAULT_SPEED
import com.example.utils.Request
import com.google.gson.Gson
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.websocket.*
import java.util.*
import kotlin.collections.HashMap

var maxRoomId = 0

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.snakeModule() {
    install(WebSockets)
    routing {

        get("/temp") {
            call.respondText(
                this::class.java.classLoader.getResource("webapp/temp.html")!!.readText(),
                ContentType.Text.Html
            )
        }

        static("/") {
            resources("")
            resources("webapp")
        }

        static("/games") {
            resources("webapp")
        }

        static("/snakegame") {
            resources("newapp")
        }

        val rooms = Collections.synchronizedMap<String, GameController>(HashMap())

        webSocket("/games/snake/game/{roomId?}") {
            log.info("User connected")
            var roomId = call.parameters["roomId"]
            val controller: GameController?
            if (roomId == null) {
                val mapName = call.request.queryParameters["mapName"]
                controller = GameController(mapName)
                rooms.putIfAbsent(maxRoomId.toString(), controller)
                roomId = maxRoomId.toString()
                maxRoomId += 1
            } else {
                controller = rooms[roomId]
            }

            if (controller == null) {
                this.close()
                return@webSocket
            }

            val gson = Gson()

            send(roomId)

            controller.startGame()
            val color = call.request.queryParameters["colorId"] ?: "FF0000"
            val snake = Snake(color, DEFAULT_SPEED)
            controller.addSnake(this, snake)
            try {
                for(frame in incoming) {
                    frame as? Frame.Text ?: continue
                    val receivedText = frame.readText()
                    val request = gson.fromJson(receivedText, Request::class.java)
                    controller.updateDirection(this, request.direction)
                }
            } catch (e: Exception) {
                println(e.localizedMessage)
            } finally {
                controller.removeSnake(this)
                val isRoomFinished = controller.tryStopTimer()
                if (isRoomFinished)
                    rooms.remove(roomId)
            }
        }
    }
}

