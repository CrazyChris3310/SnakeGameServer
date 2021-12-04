class Menu {

    constructor() {
        this.menu = $("<div class='menu'>");
        this.showMenu();
        this.currentColor = getRandomColor();
        $('.game-field-wrapper').css('border-color', this.currentColor);
        this.empty = false;
        this.currentMap = new FreeMap();
    }
    
    showMenu() {
        let multiPlayerBtn = $('<input type="button" class="menu-button" value="Start multiplayer">');
        let setBtn = $('<input type="button" class="menu-button" value="Settings">');
        let singlePlayerBtn = $('<input type="button" class="menu-button" value="Start singleplayer">')
        this.menu.append(singlePlayerBtn).append(multiPlayerBtn).append(setBtn);

        multiPlayerBtn.on('click', () => {
            this.menu.empty();
            this.showMultiplayerMenu();
        });
        singlePlayerBtn.on('click', () => {
            this.menu.empty();
            this.empty = true;
            this.gameEngine = new SingleGameEngine(this.currentColor, this.currentMap);
            this.gameEngine.startGame();
        })
        setBtn.on('click', () => {
            this.menu.empty();
            this.showSettings();
        });
    }

    showMultiplayerMenu() {
        let connectPanel = $('<div class="menu-row">');
        let roomId = $('<input id="room-id" type="text" class="menu-input" placeholder="123">');
        let connectButton = $('<input type="button" class="menu-button" value="Connect">');
        connectPanel.append(roomId).append(connectButton);
        this.menu.append(connectPanel);

        let createGameBtn = $('<input type="button" class="menu-button" value="Create Game">');
        this.menu.append(createGameBtn);

        let back = $('<input type="button" class="menu-button" value="Back"/>');
        this.menu.append(back);

        connectButton.on('click', () => {
            let roomId = $('#room-id').val();
            if (roomId == null || roomId === "")
                return;
            this.menu.empty();
            this.empty = true;
            this.gameEngine = new NetworkGameEngine(this.currentColor);
            this.gameEngine.startGame(roomId);

        })

        createGameBtn.on('click', () => {
            this.menu.empty();
            this.empty = true;
            this.gameEngine = new NetworkGameEngine(this.currentColor);
            $('body').append($('<div class="temp">'))
            this.gameEngine.startGame();
        })

        back.on('click', () => {
            this.menu.empty();
            this.showMenu();
        });
    }

    showSettings() {
        let colorLabel = $('<label for="color-chooser">Snake color: </label>');
        let colorInp = $(`<input type="color" id="color-chooser" value=${this.currentColor}>`);
        let firstRow = $('<div class="menu-row">');
        firstRow.append(colorLabel).append(colorInp);

        let mapLabel = $('<label for="map-chooser">Select a map: </label>');
        let mapSelector = $('<select id="map-chooser">');
        let freeMapTag = $('<option value="free">No edges</option>');
        let edgesMap = $('<option value="edges">Edges</option>');
        let tunnelMap = $('<option value="tunnel">Tunnel</option>');
        let apartMap = $('<option value="apartament">Apartament</option>');
        mapSelector.append(freeMapTag).append(edgesMap).append(tunnelMap).append(apartMap);
        mapSelector.val(this.currentMap.name);
        let mapRow = $('<div class="menu-row">');
        mapRow.append(mapLabel).append(mapSelector);
    
        let butPanel = $('<div class="final-buttons"></div>');
        let save = $('<input type="button" class="menu-button" value="Save"/>');
        let back = $('<input type="button" class="menu-button" value="Back"/>');
        butPanel.append(save).append(back);
    
        this.menu.append(firstRow).append(mapRow).append(butPanel);

        save.on('click', (event) => {
            this.currentColor = $('#color-chooser').val();
            $('.game-field-wrapper').css('border-color', this.currentColor);

            this.currentMap = defineMap($('#map-chooser').val());
        });
        back.on('click', () => {
            this.menu.empty();
            this.showMenu();
        });
    }

    stopGame() {
        this.gameEngine.stopGame();
        if (this.empty) {
            this.showMenu();
            this.empty = false;
            $('.temp').remove();
        }
    }

    sendData(direction) {
        this.gameEngine.sendData(direction);
    }

}

function defineMap(name) {
    switch(name) {
        case 'free': return new FreeMap();
        case 'edges': return new EdgesMap();
        case 'tunnel': return new TunnelMap();
        case 'apartament': return new ApartamentMap();
    }
}

function changeDirection(event) {
    let direction = null;
    switch(event.code) {
        case "ArrowUp": direction = "UP"; break;
        case "ArrowDown": direction = "DOWN"; break;
        case "ArrowLeft": direction = "LEFT"; break;
        case "ArrowRight": direction = "RIGHT"; break;
        case "Escape": menu.stopGame(); break;
    }
    if (direction != null)
        menu.sendData(direction);
}

let menu = new Menu();
$('body').append(menu.menu);
document.addEventListener('keydown', changeDirection);
