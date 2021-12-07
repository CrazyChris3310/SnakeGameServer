package com.example.model

enum class Direction(val verticalKey: Int, val horizontalKey: Int) {
    UP(-1, 0),
    RIGHT(0, 1),
    LEFT(0, -1),
    DOWN(1, 0);

    fun isOpposite(dir: Direction): Boolean {
        return dir.horizontalKey + horizontalKey == 0 && dir.verticalKey + verticalKey == 0
    }


}