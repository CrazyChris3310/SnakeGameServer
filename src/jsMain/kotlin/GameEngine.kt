import com.example.maps.GameMap
import com.example.model.Direction
import com.example.model.Food
import com.example.model.Point
import com.example.model.Snake
import com.example.utils.DEFAULT_SPEED
import com.example.utils.Request
import com.example.utils.Response
import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import org.w3c.dom.CloseEvent
import org.w3c.dom.ErrorEvent
import org.w3c.dom.MessageEvent
import org.w3c.dom.WebSocket
import org.w3c.dom.events.Event

interface GameEngine {
    fun startGame()
    fun stopGame()
    fun changeDirection(direction: String)
}

class SingleGameEngine(color: String, private val map: GameMap) : GameEngine {

    private val food = Food()
    private val snake = Snake(color.substring(1), DEFAULT_SPEED)
    private var timer: Int? = null

    constructor(color: String, map: String) : this(color, defineMap(map))

    override fun startGame() {
       timer = window.setInterval(::step, 10)
    }

    private fun step() {
        val points = ArrayList<Point>()
        if (snake.intersects(food.cords)) {
            food.newCords(map.edges)
            snake.grow()
            if (snake.speed > 10 && snake.getSize() % 10 == 0) {
                snake.speed -= 10
            }
        }

        if (snake.intersectsItself())
            snake.reborn()

        if (map.isIntersected(snake))
            snake.reborn()

        snake.updateDirection()
        snake.updateCords()

        for (element in snake.snake) {
            val cords = element.getCords()
            points.add(Point(cords, snake.color))
        }

        for (point in map.edges) {
            points.add(Point(point, map.color))
        }

        points.add(Point(food.cords, food.color))

        val ctx = getCanvasContext()
        clearCanvas(ctx)
        for (point in points) {
            paint(point.x, point.y, "#${point.color}", ctx)
        }
    }

    override fun stopGame() {
        if (timer != null) {
            window.clearInterval(timer!!)
            clearCanvas(getCanvasContext())
        }
    }

    override fun changeDirection(direction: String) {
        val dir = when (direction) {
            "UP" -> Direction.UP
            "DOWN" -> Direction.DOWN
            "RIGHT" -> Direction.RIGHT
            "LEFT" -> Direction.LEFT
            else -> return
        }
        snake.addDirection(dir)
    }

}

class NetworkGameEngine(color: String, private val mapSelected: String = "free", private val roomId: String = "") : GameEngine {

    private val color = color.substring(1)
    private var socket: WebSocket? = null

    override fun startGame() {
        var path = "ws://${window.location.host}/games/snake/game"
        if (roomId != "") {
            path += "/$roomId"
        }
        path += "?colorId=$color&mapName=$mapSelected"

        this.socket = WebSocket(path)
        this.socket!!.onopen = { console.log("Connection is set!") }
        this.socket!!.onclose = { e ->
            val event = e as CloseEvent
            if (event.wasClean) {
                console.log("Clean closed")
            } else {
                console.log("Connection reset")
            }
            console.log("Error code: " + event.code + ", reason: " + event.reason)
        }

        this.socket!!.onmessage = { event ->
            val receivedText = event.data.toString()
            try {
                receivedText.toInt()
            } catch (e: Exception) {
                val response = Json.decodeFromString(Response.serializer(), receivedText)
                val points = response.points
                val ctx = getCanvasContext()
                clearCanvas(ctx)
                for (point in points) {
                    paint(point.x, point.y, "#${point.color}", ctx)
                }
            }
        }

        this.socket!!.onerror = { e ->
            val event = e as ErrorEvent
            console.log("Error" + event.message)
        }
    }

    override fun stopGame() {
        socket!!.close()
        console.log("socket closed")
        clearCanvas(getCanvasContext())
    }

    override fun changeDirection(direction: String) {
        val dir = when (direction) {
            "UP" -> Direction.UP
            "DOWN" -> Direction.DOWN
            "RIGHT" -> Direction.RIGHT
            "LEFT" -> Direction.LEFT
            else -> return
        }
        val request = Json.encodeToString(Request.serializer(), Request(dir))
        socket!!.send(request)
    }
}