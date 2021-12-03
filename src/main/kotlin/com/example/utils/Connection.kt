package com.example.utils

import com.example.model.Snake
import io.ktor.http.cio.websocket.*
import java.util.concurrent.atomic.AtomicInteger

class Connection(val session: DefaultWebSocketSession, var snake: Snake)