package com.example.utils

import com.example.model.Direction
import com.example.model.Point
import kotlinx.serialization.Serializable

@Serializable
data class Request(val direction: Direction)

@Serializable
data class Response(var points: List<Point>)