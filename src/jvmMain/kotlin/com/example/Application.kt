package com.example

import com.example.controllers.GameController
import com.example.model.Snake
import com.example.utils.DEFAULT_SPEED
import com.example.utils.Request
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.*

var maxRoomId = 0

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.snakeModule() {
    install(WebSockets)
    routing {

        get("/") {
            call.respondText(
                this::class.java.classLoader.getResource("index.html")!!.readText(),
                ContentType.Text.Html
            )
        }

        static("/") {
            resources("")
        }

        val rooms = Collections.synchronizedMap<String, GameController>(HashMap())

        webSocket("/games/snake/game/{roomId?}") {
            println("User connected")
            var roomId = call.parameters["roomId"]
            val controller: GameController?
            if (roomId == null) {
                val mapName = call.request.queryParameters["mapName"]
                val gameModeName = call.request.queryParameters["gameMode"]
                controller = GameController(mapName, gameModeName)
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

            send(roomId)

            controller.startGame()
            val color = call.request.queryParameters["colorId"] ?: "FF0000"
            val snake = Snake(color, DEFAULT_SPEED)
            controller.addSnake(this, snake)
            try {
                for(frame in incoming) {
                    frame as? Frame.Text ?: continue
                    val receivedText = frame.readText()
                    val request = Json.decodeFromString<Request>(receivedText)
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

