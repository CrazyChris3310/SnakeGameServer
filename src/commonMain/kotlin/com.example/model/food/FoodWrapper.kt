package com.example.model.food

import kotlinx.serialization.Serializable

@Serializable
data class FoodWrapper(val x: Int, val y: Int, val type: FoodType) {
    constructor(food: Food) : this(food.cords.first, food.cords.second, food.type)
}