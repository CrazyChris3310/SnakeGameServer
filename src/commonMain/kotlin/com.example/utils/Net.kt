package com.example.utils

import com.example.model.Direction
import com.example.model.Point

data class Request(val direction: Direction)

data class Response(var points: List<Point>)