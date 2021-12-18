import com.example.maps.GameMap
import com.example.model.Direction
import com.example.model.Food
import com.example.model.Point
import com.example.model.Snake
import com.example.utils.DEFAULT_SPEED
import com.example.utils.Request
import com.example.utils.Response
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.*
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
        var url = "ws://${window.location.host}/games/snake/game";
        if (roomId != "") {
            url += "/$roomId"
        }
        url += "?colorId=$color&mapName=$mapSelected"

        socket = WebSocket(url)

        socket!!.onopen = { console.log("Connection is set!") }
        socket!!.onclose = {
            console.log("Socket closed")
        }

        socket!!.onmessage = { event: MessageEvent ->
            {
                val room = event.data.toString()
                try {
                    room.toInt()
                } catch (e: NumberFormatException) {
                    document.querySelector(".temp")?.innerHTML = "Room ID <br> $room"
                }
                val response = JSON.parse<Response>(event.data.toString())
                val points = response.points
                val ctx = getCanvasContext()
                clearCanvas(ctx)
                for (point in points) {
                    paint(point.x, point.y, "#${point.color}", ctx)
                }
            }
        }

        socket!!.onerror = { event: Event ->
            console.log("Error: $event")
        }
    }

    override fun stopGame() {
        socket!!.close()
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
        val request = JSON.stringify(Request(dir))
        socket!!.send(request)
    }
}