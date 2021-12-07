class Direction {
    constructor(horiz, vert) {
        this.keys = {
            horizontal: horiz,
            vertical: vert
        }
    }

    isOpposite(direction) {
        return (
            this.keys.horizontal + direction.keys.horizontal == 0 && 
            this.keys.vertical + direction.keys.vertical == 0
        );
    }
}

class Snake {

    constructor(color, speed = DEFAUL_SPEED) {
        this.props = {
            color: color,
            speed: speed,
            count: speed,
            snake: [],
            directionQueue: [],
            direction: getRandomDirection(),
        }
        this.reborn();
    }

    createHead() {
        let cords = getRandomCoordinate();
        let head = new SnakeElement(cords, this.props.direction);
        this.props.snake.push(head);
    }

    getHead() {
        return this.props.snake[0];
    }

    reborn() {
        this.props.snake = [];
        this.props.direction = getRandomDirection();
        this.createHead();
        this.grow();
        this.grow();
        this.props.speed = DEFAUL_SPEED;
    }

    getSize() { return this.props.snake.length }

    addDirection(direction) {
        this.props.directionQueue.push(direction);
    }

    updateDirection() {
        if (this.props.count != this.props.speed) return;
        let dir = this.props.directionQueue.shift();
        if (dir == null || dir == undefined ) return;
        if (!this.props.direction.isOpposite(dir)) {
            this.props.direction = dir;
        }
    }

    grow() {
        let newCords = this.props.snake[this.props.snake.length - 1].getCords();
        let dir = this.props.snake[this.props.snake.length - 1].direction;
        newCords.x -= dir.keys.horizontal * ELEMENT_SIZE;
        newCords.y -= dir.keys.vertical * ELEMENT_SIZE;
        this.props.snake.push(new SnakeElement(newCords, dir));
    }

    updateCords() {
        this.props.count -= TICK_LENGTH;
        if (this.props.count != 0) return;
        this.props.count = this.props.speed;

        for (let i = this.props.snake.length - 1; i > 0; --i) {
            this.props.snake[i].direction = this.props.snake[i - 1].direction;
            this.props.snake[i].changeCords(this.props.snake[i-1].getCords());
        }

        let head = this.props.snake[0];
        head.direction = this.props.direction;
        head.move();

        if (head.x < 0)
            head.x  = FIELD_WIDTH - ELEMENT_SIZE;
        else if (head.x >= FIELD_WIDTH)
            head.x = 0;
        else if (head.y < 0)
            head.y = FIELD_HEIGHT - ELEMENT_SIZE;
        else if (head.y >= FIELD_HEIGHT)
            head.y = 0;
    }

    intersects(cords) {
        return equals(this.props.snake[0].getCords(), cords);
    }

    intersectsItself() {
        for (let i = 1; i < this.props.snake.length; ++i) {
            if (equals(this.props.snake[0].getCords(), this.props.snake[i].getCords()))
                return true;
        }
        return false;
    }

}

class SnakeElement {

    constructor(cords, direction) {
        this.x = cords.x;
        this.y = cords.y;
        this.direction = direction;
    }

    changeCords(cords) {
        this.x = cords.x;
        this.y = cords.y;
    }

    getCords() {
        return { x: this.x, y: this.y };
    }

    move() {
        this.x += this.direction.keys.horizontal * ELEMENT_SIZE;
        this.y += this.direction.keys.vertical * ELEMENT_SIZE;
    }

    equals(element) {
        if (this === element) return true;
        if (this.x != element.x) return false;
        if (this.y != element.y) return false;
        return true;
    }

}

class Food {
    
    constructor() {
        this.color = "black";
        this.cords = getRandomCoordinate();
    }

    newCords(mapEdges) {
        this.cords = getRandomCoordinate();
        let collision = false;
        do {
            collision = false;
            for (let edge of mapEdges) {
                if (equals(edge, this.cords)) {
                    collision = true;
                    this.cords = getRandomCoordinate();
                    break;
                }
            }
        } while (collision)
    }

}