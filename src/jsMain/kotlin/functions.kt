import com.example.model.food.FoodType
import com.example.model.food.FoodWrapper
import com.example.utils.ELEMENT_SIZE
import com.example.utils.FIELD_HEIGHT
import com.example.utils.FIELD_WIDTH
import com.example.utils.randomInt
import kotlinx.browser.document
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement

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

fun paintFood(food: FoodWrapper, ctx: CanvasRenderingContext2D) {
    val img = when (food.type) {
        FoodType.APPLE -> APPLE_IMG
        FoodType.PINEAPPLE -> PINEAPPLE_IMG
        FoodType.WATERMELON -> WATERMELON_IMG
        FoodType.BANANA -> BANANA_IMG
        FoodType.POISON -> POISON_IMG
    }
    ctx.drawImage(img, food.x.toDouble(), food.y.toDouble(), ELEMENT_SIZE.toDouble(), ELEMENT_SIZE.toDouble())
}

fun getRandomColor(): String {
    val letters = "0123456789ABCDEF"
    var color = "#"
    for (i in 1..6) {
        color += letters[randomInt(0, 16)]
    }
    return color
}
