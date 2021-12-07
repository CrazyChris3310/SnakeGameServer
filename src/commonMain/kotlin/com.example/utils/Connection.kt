package com.example.utils

import com.example.model.Snake
import io.ktor.http.cio.websocket.*

class Connection(val session: DefaultWebSocketSession, var snake: Snake)