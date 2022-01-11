import GamePanel.*
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.css.Color
import kotlinx.css.borderColor
import kotlinx.html.InputType
import kotlinx.html.classes
import kotlinx.html.id
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLSelectElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import react.*
import react.dom.*
import styled.css
import styled.styledDiv

interface AppState : State {
    var color: String
    var map: String
    var panel: GamePanel
    var gameEngine: GameEngine
    var gameMode: String
    var roomId: String
    var isConnected: Boolean
}

class Application : RComponent<Props, AppState>() {

    init {
        state.color = getRandomColor()
        state.panel = MAIN_MENU
        state.map = "free"
        state.isConnected = false
        state.roomId = ""
        state.gameMode = "default"
    }

    override fun componentDidMount() {
        document.addEventListener("keydown", ::keyActionListener)
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
                        isConnected = false
                    }
                }
                "ArrowUp" -> state.gameEngine.changeDirection("UP")
                "ArrowDown" -> state.gameEngine.changeDirection("DOWN")
                "ArrowLeft" -> state.gameEngine.changeDirection("LEFT")
                "ArrowRight" -> state.gameEngine.changeDirection("RIGHT")
            }

        } else {
            when (key.code) {
                "Escape" -> setState {
                    panel = MAIN_MENU
                    isConnected = false
                }
            }
        }
    }

    private fun updateSettings(newColor: String, newMap: String, newGameMode: String) {
        setState {
            map = newMap
            color = newColor
            gameMode = newGameMode
        }
    }

    private fun beginGame(settings: GameSettings) {
        setState {
            panel = LOADER
            gameEngine = if (settings.single) {
                            SingleGameEngine(color, map, gameMode)
                        } else {
                            NetworkGameEngine(color, settings.map, settings.roomId, settings.gameMode)
                        }
            gameEngine.startGame().then { value ->
                this@Application.setState {
                    if (value == null) {
                        isConnected = false
                    } else {
                        isConnected = true
                        roomId = value
                    }
                    panel = NONE
                }
            }.catch { error ->
                console.log(error.message)
                this@Application.setState {
                    panel = MAIN_MENU
                    isConnected = false
                }
                window.alert(error.message ?: "Error happened")
            }
        }
    }

    override fun RBuilder.render() {
        div(classes = "ui-wrapper") {
            styledDiv {
                attrs.classes = setOf("game-field-wrapper")
                css {
                    borderColor = Color(state.color)
                }
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
                    attrs.gameMode = state.gameMode
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
            div {
                attrs.id = "room-id"
                if (state.isConnected) {
                    +"Room Id:"
                    br {}
                    +state.roomId
                }
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
    var save: (String, String, String) -> Unit
    var color: String
    var map: String
    var gameMode: String
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
                    id = "room-id-input"
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
    val (gameMode, setGameMode) = useState("default")
    div(classes = "menu") {
        mapSelector(map) { str -> setMap(str) }
        gameModeSelector(gameMode) { str -> setGameMode(str)}

        div(classes = "final-buttons") {
            input(type = InputType.button, classes = "menu-button") {
                attrs.value = "Start game"
                attrs.onClickFunction =  { props.startGame(GameSettings(single = false, map = map, gameMode = gameMode)) }
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
    val (gameMode, setGameMode) = useState(props.gameMode)

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
        gameModeSelector(gameMode) { str -> setGameMode(str) }

        div(classes = "final-buttons") {
            input(type = InputType.button, classes = "menu-button") {
                attrs.value = "Save"
                attrs.onClickFunction = {
                    props.save(color, map, gameMode)
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
        select(classes = "selector") {
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

fun RBuilder.gameModeSelector(mode: String, setMode: (String) -> Unit) {
    div(classes = "menu-row") {
        label{
            attrs.htmlFor = "game-mode-chooser"
            +"Select a game mode: "
        }
        select(classes = "selector") {
            attrs {
                id = "game-mode-chooser"
                value = mode
                onChange = { event ->
                    setMode((event.target as HTMLSelectElement).value)
                }
            }
            option {
                attrs.value = "default"
                +"Default"
            }
            option {
                attrs.value = "diverseFood"
                +"Diverse food"
            }
            option {
                attrs.value = "randomSpeed"
                +"Random speed"
            }
        }
    }
}