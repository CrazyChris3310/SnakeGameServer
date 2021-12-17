import kotlinx.browser.document
import kotlinx.html.InputType
import kotlinx.html.id
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLSelectElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import react.*
import react.dom.*
import kotlin.jvm.Volatile

val App = fc<Props> {
    val (color, setColor) = useState(getRandomColor())
    val (map, setMap) = useState("free")
    val (panel, setPanel) = useState("mainMenu")
    var (isPlaying, setPlaying) = useState(false)
    var gameEngine: GameEngine = SingleGameEngine(color, map)
//    val (listenerInitialized, setListenerInitialized) = useState(false)

    fun updateSettings(color: String, map: String) {
        setColor(color)
        setMap(map)
    }

    val isPlayingRef = useRef(isPlaying)

    useEffect {
        isPlayingRef.current = isPlaying
    }

    fun keyActionListener(event: Event) {
        console.log(isPlayingRef.current)
        val key = event as KeyboardEvent
        if (isPlayingRef.current == true) {
            when (key.code) {
                "Escape" -> {
                    gameEngine.stopGame()
                    setPlaying(false)
                    setPanel("mainMenu")
//                document.removeEventListener("keydown", ::keyActionListener)
                }
                "ArrowUp" -> gameEngine.changeDirection("UP")
                "ArrowDown" -> gameEngine.changeDirection("DOWN")
                "ArrowLeft" -> gameEngine.changeDirection("LEFT")
                "ArrowRight" -> gameEngine.changeDirection("RIGHT")
            }

        } else {
            when (key.code) {
                "Escape" -> setPanel("mainMenu")
            }
        }
    }

    useEffectOnce{
        document.addEventListener("keydown", ::keyActionListener)
    }

    div {
        div(classes = "game-field-wrapper") {
            canvas(classes = "game-field") {
                attrs.width = "1200"
                attrs.height = "680"
            }
        }
        when (panel) {
            "mainMenu" -> child(MainMenu) {
                attrs {
                    startGame = {
                        setPanel("empty")
                        setPlaying(true)
                        gameEngine = SingleGameEngine(color, map)
                        gameEngine.startGame()
                        console.log("game started")
//                        document.addEventListener("keydown", ::keyActionListener)
                    }
                    showMultiplayerMenu = { setPanel("multiplayerMenu") }
                    showSettings = { setPanel("settings") }
                }
            }
            "settings" -> child(Settings) {
                attrs.color = color
                attrs.map = map
                attrs.save = ::updateSettings
                attrs.back = { setPanel("mainMenu") }
            }
            "multiplayerMenu" -> child(MultiplayerMenu) {
                attrs {
                    connectToGame = { roomId ->
                        setPanel("empty")
                        setPlaying(true)
                        gameEngine = NetworkGameEngine(color, map, roomId)
                        gameEngine.startGame()
                    }
                    showMultiplayerSettings = { setPanel("multiplayerSettings") }
                    back = { setPanel("mainMenu") }
                }
            }
            "multiplayerSettings" -> child(MultiplayerSettings) {
                attrs {
                    startGame = {
                        setPanel("empty")
                        setPlaying(true)
                        gameEngine = NetworkGameEngine(color, map)
                        gameEngine.startGame()
                    }
                    back = { setPanel("multiplayerMenu") }
                }
            }
            "empty" -> div {}
        }

    }
}

interface MenuProps : Props {
    var back: (Event) -> Unit
    var showSettings: (Event) -> Unit
    var showNothing: (Event) -> Unit
    var showMultiplayerMenu: (Event) -> Unit
    var showMultiplayerSettings: (Event) -> Unit
    var startGame: (Event) -> Unit
    var color: String
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
