import kotlinx.browser.document
import kotlinx.html.InputType
import kotlinx.html.id
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLSelectElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import react.*
import react.dom.*
import GamePanel.*;
import kotlinx.browser.window
import kotlinx.coroutines.await

interface AppState : State {
    var color: String
    var map: String
    var panel: GamePanel
    var gameEngine: GameEngine
}

class Application : RComponent<Props, AppState>() {

    init {
        state.color = getRandomColor()
        state.panel = MAIN_MENU
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
        if (state.panel == NONE) {
            when (key.code) {
                "Escape" -> {
                    state.gameEngine.stopGame()
                    setState {
                        panel = MAIN_MENU
                    }
                }
                "ArrowUp" -> state.gameEngine.changeDirection("UP")
                "ArrowDown" -> state.gameEngine.changeDirection("DOWN")
                "ArrowLeft" -> state.gameEngine.changeDirection("LEFT")
                "ArrowRight" -> state.gameEngine.changeDirection("RIGHT")
            }

        } else {
            when (key.code) {
                "Escape" -> setState { panel = MAIN_MENU }
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

    private fun beginGame(settings: GameSettings) {
        setState {
            panel = LOADER
            gameEngine = if (settings.single) {
                            SingleGameEngine(color, map)
                        } else {
                            NetworkGameEngine(color, settings.map, settings.roomId)
                        }
            gameEngine.startGame().then {
                this@Application.setState {
                    panel = NONE
                }
            }.catch { error ->
                console.log(error.message)
                this@Application.setState {
                    panel = MAIN_MENU
                }
            }
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
                MAIN_MENU -> child(MainMenu) {
                    attrs {
                        startGame = ::beginGame
                        showMultiplayerMenu = { setState { panel = MULTIPLAYER_MENU } }
                        showSettings = { setState { panel = SETTINGS } }
                    }
                }
                SETTINGS -> child(Settings) {
                    attrs.color = state.color
                    attrs.map = state.map
                    attrs.save = ::updateSettings
                    attrs.back = { setState { panel = MAIN_MENU } }
                }
                MULTIPLAYER_MENU -> child(MultiplayerMenu) {
                    attrs {
                        startGame = ::beginGame
                        showMultiplayerSettings = { setState { panel = MULTIPLAYER_SETTINGS } }
                        back = { setState { panel = MAIN_MENU } }
                    }
                }
                MULTIPLAYER_SETTINGS -> child(MultiplayerSettings) {
                    attrs {
                        startGame = ::beginGame
                        back = { setState { panel = MULTIPLAYER_MENU } }
                    }
                }
                NONE -> div {}
                LOADER -> div(classes = "loader") {}
            }

        }
    }

}

interface MenuProps : Props {
    var back: (Event) -> Unit
    var showSettings: (Event) -> Unit
    var showMultiplayerMenu: (Event) -> Unit
    var showMultiplayerSettings: (Event) -> Unit
    var startGame: (GameSettings) -> Unit
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
            attrs.onClickFunction = { props.startGame(GameSettings(single = true)) }
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
    val (roomId, setRoomId) = useState("")
    div(classes = "menu") {
        div(classes = "menu-row") {
            input(type = InputType.text, classes = "menu-input") {
                attrs {
                    id = "room-id"
                    placeholder = "Room ID"
                    onChange = { event -> setRoomId((event.target as HTMLInputElement).value) }
                }
            }
            input(type = InputType.button, classes = "menu-button") {
                attrs.value = "Connect"
                attrs.onClickFunction = { props.startGame(GameSettings(single = false, roomId = roomId)) }
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
        mapSelector(map) { str -> setMap(str) }

        div(classes = "final-buttons") {
            input(type = InputType.button, classes = "menu-button") {
                attrs.value = "Start game"
                attrs.onClickFunction =  { props.startGame(GameSettings(single = false, map = map)) }
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
        mapSelector(map) { str -> setMap(str) }

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

fun RBuilder.mapSelector(map: String, setMap: (String) -> Unit) {
    div(classes = "menu-row") {
        label{
            attrs.htmlFor = "map-chooser"
            +"Select a map: "
        }
        select {
            attrs {
                id = "map-chooser"
                value = map
                onChange = { event ->
                    setMap((event.target as HTMLSelectElement).value)
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
}