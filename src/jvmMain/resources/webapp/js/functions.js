
const ELEMENT_SIZE = 10;
const TICK_LENGTH = 10;
const DEFAUL_SPEED = 40;
const FIELD_WIDTH = 1200;
const FIELD_HEIGHT = 680;

const DIRECTION_UP = new Direction(0, -1);
const DIRECTION_DOWN = new Direction(0, 1);
const DIRECTION_RIGHT = new Direction(1, 0);
const DIRECTION_LEFT = new Direction(-1, 0);
const array = [DIRECTION_DOWN, DIRECTION_UP, DIRECTION_LEFT, DIRECTION_RIGHT];

function getCanvasContext() {
    let canvas = document.querySelector(".game-field");
    return canvas.getContext('2d');
}

function clearCanvas(ctx) {
    ctx.fillStyle = "whitesmoke";
    ctx.fillRect(0, 0, 1200, 680);
}

function paint(x, y, color, ctx) {
    ctx.fillStyle = color;
    ctx.fillRect(x, y, 10, 10);
}

function getRandomInt(min, max) {
    return Math.floor(Math.random() * (max - min) + min);
}

function getRandomColor() {
    let letters = '0123456789ABCDEF';
    let color = '#';
    for (let i = 0; i < 6; i++) {
        color += letters[Math.floor(Math.random() * 16)];
    }
    return color;
}

function getRandomCoordinate() {
    let x = getRandomInt(0, FIELD_WIDTH);
    let y = getRandomInt(0, FIELD_HEIGHT);
    x -= x % ELEMENT_SIZE;
    y -= y % ELEMENT_SIZE;
    return { 
        x: x, 
        y: y
    };
}

function getRandomDirection() {
    return array[getRandomInt(0, 3)];
}

function equals(cords, otherCords) {
    return cords.x == otherCords.x && cords.y == otherCords.y;
}