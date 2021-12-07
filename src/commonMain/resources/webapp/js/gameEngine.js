class NetworkGameEngine {

    constructor(color, mapSelected = "free") {
        this.color = color.substring(1);
        this.map = mapSelected;
    }

    startGame(roomId = "") {
        let url = "ws://" + window.location.host + "/games/snake/game";
        if (roomId !== "")
            url += "/" + roomId;
        url += "?colorId=" + this.color + "&mapName=" + this.map;
        this.socket = new WebSocket(url);

        this.socket.onopen = () => (console.log("Connection is set!"));
        this.socket.onclose = (event) => {
            if (event.wasClean) {
                console.log("Clean closed");
            } else {
                console.log("Connection reset");
            }
            console.log("Error code: " + event.code + ", reason: " + event.reason);
        }
    
        this.socket.onmessage = (event) => {
            let roomId = Number.parseInt(event.data);
            if  (!isNaN(roomId)) {
                $('.temp').html('Room ID <br>' + roomId);
                return;
            }

            let response = JSON.parse(event.data);
            let points = response.points;
            let ctx = getCanvasContext();
            clearCanvas(ctx);
            for (let point of points) {
                paint(point.x, point.y, '#' + point.color, ctx);
            }
        }
    
        this.socket.onerror = (event) => {
            console.log("Error: " + event.message);
        }
    }

    stopGame() {
        this.socket.close();
        clearCanvas(getCanvasContext());
    }

    sendData(direction) {
        let request = {
            "direction": direction
        }
        request = JSON.stringify(request);
        this.socket.send(request);
    }

}

class SingleGameEngine {

    constructor(color, map) {
        this.food = new Food();
        this.snake = new Snake(color);
        this.map = map;
    }

    startGame() {
        this.timer = setInterval(() => { this.step(); }, 10);
    }

    step() {
        let points = [];
        if (this.snake.intersects(this.food.cords)) {
            this.food.newCords(this.map.edges);
            this.snake.grow();
            if (this.snake.props.speed > 10 && this.snake.getSize() % 10 == 0) {
                this.snake.props.speed -= 10;
            }
        }

        if (this.snake.intersectsItself())
            this.snake.reborn();

        if (this.map.isIntersectedBy(this.snake))
            this.snake.reborn();

        this.snake.updateDirection();
        this.snake.updateCords();

        for (let element of this.snake.props.snake) {
            let {x, y} = element.getCords();
            points.push({ x: x, y: y, color: this.snake.props.color })
        }

        let edges = this.map.edges;
        for (let point of edges) {
            points.push({
                x: point.x,
                y: point.y,
                color: this.map.color
            })
        }

        points.push({x: this.food.cords.x, y: this.food.cords.y, color: this.food.color});

        let ctx = getCanvasContext();
        clearCanvas(ctx);
        for (let point of points) {
            paint(point.x, point.y, point.color, ctx);
        }
        
    }

    stopGame() {
        clearInterval(this.timer);
        clearCanvas(getCanvasContext());
    }

    sendData(direction) {
        let dir;
        switch(direction) {
            case "UP": dir = DIRECTION_UP; break;
            case "DOWN": dir = DIRECTION_DOWN; break;
            case "RIGHT": dir = DIRECTION_RIGHT; break;
            case "LEFT": dir = DIRECTION_LEFT; break;
        }
        this.snake.addDirection(dir);
    }

}
