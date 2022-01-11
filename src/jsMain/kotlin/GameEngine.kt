import com.example.gameModes.GameMode
import com.example.maps.GameMap
import com.example.model.Direction
import com.example.model.Point
import com.example.model.Snake
import com.example.model.food.FoodWrapper
import com.example.utils.*
import kotlinx.browser.window
import kotlinx.serialization.json.Json
import org.w3c.dom.CloseEvent
import org.w3c.dom.ErrorEvent
import org.w3c.dom.WebSocket
import kotlin.js.Promise

interface GameEngine {
    fun startGame(): Promise<String?>
    fun stopGame()
    fun changeDirection(direction: String)
}

class SingleGameEngine(color: String, private val map: GameMap, private val gameMode: GameMode) : GameEngine {

    private var foods = mutableListOf(gameMode.spawnFood(map.edges))
    private val snake = Snake(color.substring(1), DEFAULT_SPEED)
    private var timer: Int? = null

    constructor(color: String, map: String, gameMode: String) : this(color, defineMap(map), defineGameMode(gameMode))

    init {
        gameMode.map = map
    }

    override fun startGame(): Promise<String?> {
       timer = window.setInterval(::step, 10)
        return Promise{ resolve, _ ->
            resolve(null)
        }
    }

    private fun step() {
        val points = ArrayList<Point>()
        if (snake.intersects(foods[0].cords)) {
            snake.eat(foods[0])
            foods[0] = gameMode.spawnFood(map.edges)
        }

        if (snake.intersectsItself())
            snake.reborn()

        if (map.isIntersected(snake))
            snake.reborn()

        snake.updateDirection()
        snake.updateCords()

        gameMode.apply(listOf(snake), foods)

        for (element in snake.snake) {
            val cords = element.getCords()
            points.add(Point(cords, snake.color))
        }

        for (point in map.edges) {
            points.add(Point(point, map.color))
        }

        val ctx = getCanvasContext()
        clearCanvas(ctx)
        for (point in points) {
            paint(point.x, point.y, "#${point.color}", ctx)
        }
        paintFood(FoodWrapper(foods[0]), ctx)
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

class NetworkGameEngine(color: String, private val mapSelected: String = "free",
                        private val roomId: String = "", private val gameMode: String) : GameEngine {

    private val color = color.substring(1)
    private var socket: WebSocket? = null

    override fun startGame(): Promise<String> {
        var path = "ws://${window.location.host}/games/snake/game"
        if (roomId != "") {
            path += "/$roomId"
        }
        path += "?colorId=$color&mapName=$mapSelected&gameMode=$gameMode"

        return Promise{ resolve, reject ->
            this.socket = WebSocket(path)

            this.socket!!.onopen = {
                console.log("Connection is set!")
            }
            this.socket!!.onclose = { e ->
                val event = e as CloseEvent
                if (event.wasClean) {
                    console.log("Clean closed")
                } else {
                    console.log("Connection reset")
                    reject(Throwable("Troubles with connection"))
                }
                console.log("Error code: " + event.code + ", reason: " + event.reason)
            }

            this.socket!!.onmessage = { event ->
                val receivedText = event.data.toString()
                try {
                    receivedText.toInt()
                    resolve(receivedText)
                } catch (e: Exception) {
                    val response = Json.decodeFromString(Response.serializer(), receivedText)
                    val points = response.points
                    val ctx = getCanvasContext()
                    clearCanvas(ctx)
                    for (point in points) {
                        paint(point.x, point.y, "#${point.color}", ctx)
                    }
                    val food = response.foodWrapper
                    paintFood(food, ctx)
                }
            }

            this.socket!!.onerror = { e ->
                val event = e as ErrorEvent
                console.log("Error" + event.message)
                reject(Throwable("Error happened. ${e.message}"))
            }
        }
    }

    override fun stopGame() {
        socket!!.close()
        console.log("socket closed")
        clearCanvas(getCanvasContext())
    }

    override fun changeDirection(direction: String) {
        val dir = defineDirection(direction) ?: return
        val request = Json.encodeToString(Request.serializer(), Request(dir))
        socket!!.send(request)
    }
}