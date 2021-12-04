class FreeMap {

    constructor() {
        this.name = 'free';
        this.edges = [];
        this.color = 'black';
    }

    isIntersectedBy(snake) {
        return false;
    }

}

class EdgesMap {

    constructor() {
        this.name = 'edges'
        this.edges = [];
        this.color = 'black';

        for (let i = 0; i < FIELD_WIDTH; i += ELEMENT_SIZE) {
            this.edges.push({
                x: i,
                y: 0,
            })
            this.edges.push({
                x: i,
                y: FIELD_HEIGHT - ELEMENT_SIZE
            })
        }

        for (let i = ELEMENT_SIZE; i < FIELD_HEIGHT - ELEMENT_SIZE; i += ELEMENT_SIZE) {
            this.edges.push({
                x: 0,
                y: i
            })
            this.edges.push({
                x: FIELD_WIDTH - ELEMENT_SIZE,
                y: i
            })
        }
    }



    isIntersectedBy(snake) {
        let headCords = snake.getHead().getCords();
        for (let point of this.edges) {
            if (equals(headCords, point)) 
                return true;
        }
        return false;
    }

}

class TunnelMap {
    constructor() {
        this.name = 'tunnel';
        this.edges = [];
        this.color = 'black';

        for (let i = 0; i < FIELD_HEIGHT / 4; i += ELEMENT_SIZE) {
            this.edges.push({
                x: i,
                y: 0
            });
            this.edges.push({
                x: i,
                y: FIELD_HEIGHT - ELEMENT_SIZE
            });
            this.edges.push({
                x: FIELD_WIDTH - i - ELEMENT_SIZE,
                y: 0
            });
            this.edges.push({
                x: FIELD_WIDTH - i - ELEMENT_SIZE,
                y: FIELD_HEIGHT - ELEMENT_SIZE
            });
        }

        for (let i = ELEMENT_SIZE; i < FIELD_HEIGHT / 4 - ELEMENT_SIZE; i+=ELEMENT_SIZE) {
            this.edges.push({
                x: 0,
                y: i
            });
            this.edges.push({
                x: FIELD_WIDTH - ELEMENT_SIZE,
                y: i
            });
            this.edges.push({
                x: 0,
                y: FIELD_HEIGHT - ELEMENT_SIZE - i
            });
            this.edges.push({
                x: FIELD_WIDTH - ELEMENT_SIZE,
                y: FIELD_HEIGHT - i - ELEMENT_SIZE
            })
        }

        for(let i = FIELD_HEIGHT / 4; i < FIELD_WIDTH - FIELD_HEIGHT / 4 - ELEMENT_SIZE; i += ELEMENT_SIZE ) {
            this.edges.push({
                x: i,
                y: FIELD_HEIGHT / 4
            });
            this.edges.push({
                x: i,
                y: FIELD_HEIGHT - FIELD_HEIGHT / 4
            })
        }
    }

    isIntersectedBy(snake) {
        let headCords = snake.getHead().getCords();
        for (let point of this.edges) {
            if (equals(headCords, point)) 
                return true;
        }
        return false;
    }

}

class ApartmentMap {
    constructor() {
        this.name = 'tunnel';
        this.edges = [];
        this.color = 'black';

        for (let i = 0; i < FIELD_WIDTH - ELEMENT_SIZE; i += ELEMENT_SIZE) {
            this.edges.push({
                x: i,
                y: FIELD_HEIGHT - Math.floor(FIELD_HEIGHT / 3) + Math.floor(FIELD_HEIGHT / 3) % ELEMENT_SIZE
            });
        }

        for (let i = 0; i < FIELD_WIDTH / 2 - 5 * ELEMENT_SIZE; i+= ELEMENT_SIZE) {
            this.edges.push({
                x: i,
                y: Math.floor(FIELD_HEIGHT / 3) - Math.floor(FIELD_HEIGHT / 3) % ELEMENT_SIZE
            });
            this.edges.push({
                x: FIELD_WIDTH - i - ELEMENT_SIZE,
                y: Math.floor(FIELD_HEIGHT / 3) - Math.floor(FIELD_HEIGHT / 3) % ELEMENT_SIZE
            });
        }

        for (let i = 0; i < 20 * ELEMENT_SIZE; i+= ELEMENT_SIZE) {
            this.edges.push({
                x: i,
                y: 0
            });
        }

        for (let i = 35 * ELEMENT_SIZE; i < FIELD_WIDTH - 20 * ELEMENT_SIZE; i += ELEMENT_SIZE) {
            this.edges.push({
                x: i,
                y: 0
            });
        }

        for (let i = ELEMENT_SIZE; i < ELEMENT_SIZE * 15; i += ELEMENT_SIZE) {
            this.edges.push({
                x: 0,
                y: i
            });
        }

        for (let i = ELEMENT_SIZE; i < FIELD_HEIGHT / 3; i += ELEMENT_SIZE) {
            this.edges.push({
                x: FIELD_WIDTH / 2 - 5 * ELEMENT_SIZE,
                y: i
            });
        }

        for (let i = FIELD_HEIGHT - Math.floor(FIELD_HEIGHT / 3) + Math.floor(FIELD_HEIGHT / 3) % ELEMENT_SIZE + ELEMENT_SIZE; i < FIELD_HEIGHT; i += ELEMENT_SIZE) {
            this.edges.push({
                x: FIELD_WIDTH - FIELD_WIDTH / 2 + 4 * ELEMENT_SIZE,
                y: i
            });
        }
    }

    isIntersectedBy(snake) {
        let headCords = snake.getHead().getCords();
        for (let point of this.edges) {
            if (equals(headCords, point)) 
                return true;
        }
        return false;
    }
}