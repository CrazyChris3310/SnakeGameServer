import kotlinx.browser.document
import org.w3c.dom.Image
import react.dom.render

val PINEAPPLE_IMG = Image()
val APPLE_IMG = Image()
val WATERMELON_IMG = Image()
val BANANA_IMG = Image()
val POISON_IMG = Image()

fun main() {
    PINEAPPLE_IMG.src = "images/pineapple.png"
    APPLE_IMG.src = "images/apple.png"
    WATERMELON_IMG.src = "images/watermelon.png"
    BANANA_IMG.src = "images/banana.png"
    POISON_IMG.src = "images/poison.png"

    render(document.getElementById("root")!!) {
        child(Application::class) {}
    }
}