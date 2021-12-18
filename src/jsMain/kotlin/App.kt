import kotlinx.browser.document
import kotlinx.html.InputType
import kotlinx.html.div
import kotlinx.html.id
import kotlinx.html.js.onClickFunction
import kotlinx.html.style
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLSelectElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import react.*
import react.dom.*
import kotlin.jvm.Volatile

interface AppState : State {
    var color: String
    var map: String
    var panel: String
    var gameEngine: GameEngine
}

class Application : RComponent<Props, AppState>() {

    init {
        state.color = getRandomColor()
        state.panel = "mainMenu"
        state.map = "free"
    }

    override fun componentDidMount() {
        document.addEventListener("keydown", ::keyActionListener)
        (document.querySelector(".game-field-wrapper") as HTMLDivElement).style.borderColor = state.color
    }

    override fun componentWillUnmount() {
        super.componentWillUnmount()
        document.removeEventListener("keydown", ::keyActionListener)
    }

    private fun keyActionListener(event: Event) {
        val key = event as KeyboardEvent
        if (state.panel == "none") {
            when (key.code) {
                "Escape" -> {
                    state.gameEngine.stopGame()
                    setState {
                        panel = "mainMenu"
                    }
                }
                "ArrowUp" -> state.gameEngine.changeDirection("UP")
                "ArrowDown" -> state.gameEngine.changeDirection("DOWN")
                "ArrowLeft" -> state.gameEngine.changeDirection("LEFT")
                "ArrowRight" -> state.gameEngine.changeDirection("RIGHT")
            }

        } else {
            when (key.code) {
                "Escape" -> setState { panel = "mainMenu" }
            }
        }
    }

    private fun updateSettings(newColor: String, newMap: String) {
        setState {
            map = newMap
            color = newColor
            (document.querySelector(".game-field-wrapper") as HTMLDivElement).style.borderColor = color
        }
    }

    override fun RBuilder.render() {
        div {
            div(classes = "game-field-wrapper") {
                canvas(classes = "game-field") {
                    attrs.width = "1200"
                    attrs.height = "680"
                }
            }
            when (state.panel) {
                "mainMenu" -> child(MainMenu) {
                    attrs {
                        startGame = {
                            setState {
                                map = state.map
                                panel = "none"
                                gameEngine = SingleGameEngine(color, map)
                                color = state.color

                                gameEngine.startGame()
                            }
                        }
                        showMultiplayerMenu = { setState { panel = "multiplayerMenu" } }
                        showSettings = { setState { panel = "settings" } }
                    }
                }
                "settings" -> child(Settings) {
                    attrs.color = state.color
                    attrs.map = state.map
                    attrs.save = ::updateSettings
                    attrs.back = { setState { panel = "mainMenu" } }
                }
                "multiplayerMenu" -> child(MultiplayerMenu) {
                    attrs {
                        connectToGame = { roomId ->
                            setState {
                                panel = "none"
                                gameEngine = NetworkGameEngine(color, map, roomId)

                                state.gameEngine.startGame()
                            }
                        }
                        showMultiplayerSettings = { setState { panel = "multiplayerSettings" } }
                        back = { setState { panel = "mainMenu" } }
                    }
                }
                "multiplayerSettings" -> child(MultiplayerSettings) {
                    attrs {
                        startGame = {
                            setState {
                                panel = "none"
                                gameEngine = SingleGameEngine(color, map)

                                state.gameEngine.startGame()
                            }
                        }
                        back = { setState { panel = "multiplayerMenu" } }
                    }
                }
                "none" -> div {}
            }

        }
    }

}

interface MenuProps : Props {
    var back: (Event) -> Unit
    var showSettings: (Event) -> Unit
    var showMultiplayerMenu: (Event) -> Unit
    var showMultiplayerSettings: (Event) -> Unit
    var startGame: (Event) -> Unit
    var connectToGame: (String) -> Unit
}

interface SettingsProps : Props {
    var back: (Event) -> Unit
    var save: (String, String) -> Unit
    var color: String
    var map: String
}

val MainMenu = fc<MenuProps> { props ->
    div(classes = "menu") {
        input(type = InputType.button, classes = "menu-button") {
            attrs.value = "Start SinglePlayer"
            attrs.onClickFunction = props.startGame
        }
        input(type = InputType.button, classes = "menu-button") {
            attrs.value = "Start MultiPlayer"
            attrs.onClickFunction = props.showMultiplayerMenu
        }
        input(type = InputType.button, classes = "menu-button") {
            attrs.value = "Settings"
            attrs.onClickFunction = props.showSettings
        }
    }
}

val MultiplayerMenu = fc<MenuProps> { props ->
    div(classes = "menu") {
        div(classes = "menu-row") {
            input(type = InputType.text, classes = "menu-input") {
                attrs {
                    id = "room-id"
                    placeholder = "Room ID"
                }
            }
            input(type = InputType.button, classes = "menu-button") {
                attrs.value = "Connect"
                attrs.onClickFunction = props.startGame
            }
        }
        input(type = InputType.button, classes = "menu-button") {
            attrs {
                value = "Create Game"
                onClickFunction = props.showMultiplayerSettings
            }
        }
        input(type = InputType.button, classes = "menu-button") {
            attrs {
                value = "Back"
                onClickFunction = props.back
            }
        }
    }
}


val MultiplayerSettings = fc<MenuProps> { props ->
    val (map, setMap) = useState("free")

    div(classes = "menu") {
        div(classes = "menu-row") {
            label{
                attrs.htmlFor = "map-chooser"
                +"Select a map: "
            }
            select {
                attrs {
                    id = "map-chooser"
                    value = map
                    onChange = { str ->
                        setMap((str.target as HTMLSelectElement).value)
                    }
                }
                option {
                    attrs.value = "free"
                    +"No edges"
                }
                option {
                    attrs.value = "eges"
                    +"Edges"
                }
                option {
                    attrs.value = "tunnel"
                    +"Tunnel"
                }
                option {
                    attrs.value = "apartment"
                    +"Apartment"
                }
            }
        }
        div(classes = "final-buttons") {
            input(type = InputType.button, classes = "menu-button") {
                attrs.value = "Start game"
                attrs.onClickFunction = props.startGame
            }
            input(type = InputType.button, classes = "menu-button") {
                attrs.value = "back"
                attrs.onClickFunction = props.back
            }
        }
    }
}

val Settings = fc<SettingsProps> { props ->
    val (color, setColor) = useState(props.color)
    val (map, setMap) = useState(props.map)

    div(classes = "menu") {
        div(classes = "menu-row") {
            label {
                attrs.htmlFor = "color-chooser"
                +"Snake Color: "
            }
            input(type = InputType.color) {
                attrs {
                    id = "color-chooser"
                    value = color
                    onChange = { event -> setColor((event.target as HTMLInputElement).value) }
                }
            }
        }
        div(classes = "menu-row") {
            label{
                attrs.htmlFor = "map-chooser"
                +"Select a map: "
            }
            select {
                attrs {
                    id = "map-chooser"
                    value = map
                    onChange = { str ->
                        setMap((str.target as HTMLSelectElement).value)
                    }
                }
                option {
                    attrs.value = "free"
                    +"No edges"
                }
                option {
                    attrs.value = "edges"
                    +"Edges"
                }
                option {
                    attrs.value = "tunnel"
                    +"Tunnel"
                }
                option {
                    attrs.value = "apartment"
                    +"Apartment"
                }
            }
        }
        div(classes = "final-buttons") {
            input(type = InputType.button, classes = "menu-button") {
                attrs.value = "Save"
                attrs.onClickFunction = {
                    props.save(color, map)
                }
            }
            input(type = InputType.button, classes = "menu-button") {
                attrs.value = "back"
                attrs.onClickFunction = props.back
            }
        }
    }
}
