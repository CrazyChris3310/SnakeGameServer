import com.example.maps.*
import com.example.utils.ELEMENT_SIZE
import com.example.utils.FIELD_HEIGHT
import com.example.utils.FIELD_WIDTH
import com.example.utils.randomInt
import kotlinx.browser.document
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.Image

fun getCanvasContext(): CanvasRenderingContext2D {
    val canvas = document.querySelector(".game-field") as HTMLCanvasElement
    return canvas.getContext("2d") as CanvasRenderingContext2D
}

fun clearCanvas(ctx: CanvasRenderingContext2D) {
    ctx.fillStyle = "whitesmoke"
    ctx.fillRect(0.0, 0.0, FIELD_WIDTH.toDouble(), FIELD_HEIGHT.toDouble())
}

fun paint(x: Int, y: Int, color: String, ctx: CanvasRenderingContext2D) {
    ctx.fillStyle = color
    ctx.fillRect(x.toDouble(), y.toDouble(), ELEMENT_SIZE.toDouble(), ELEMENT_SIZE.toDouble())
}

fun paintFood(x: Int, y: Int, ctx: CanvasRenderingContext2D) {
//    ctx.fillStyle = color
    val img = Image()
    img.src = "webapp/apple.png"
    ctx.drawImage(img, x.toDouble(), y.toDouble(), ELEMENT_SIZE.toDouble(), ELEMENT_SIZE.toDouble())
//    ctx.fillRect(x.toDouble(), y.toDouble(), ELEMENT_SIZE.toDouble(), ELEMENT_SIZE.toDouble())
}

fun getRandomColor(): String {
    val letters = "0123456789ABCDEF"
    var color = "#"
    for (i in 1..6) {
        color += letters[randomInt(0, 16)]
    }
    return color
}

fun defineMap(name: String) : GameMap = when (name) {
    "free" -> FreeMap()
    "edges" -> EdgesMap()
    "tunnel" -> TunnelMap()
    "apartment" -> ApartmentMap()
    else -> FreeMap()
}
