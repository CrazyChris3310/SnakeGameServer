import kotlinx.browser.document
import kotlinx.browser.window
import react.dom.aria.AriaRole

fun main() {
    document.addEventListener("keydown", {
        window.alert("Clicked in kotlin!!!");
    })

}