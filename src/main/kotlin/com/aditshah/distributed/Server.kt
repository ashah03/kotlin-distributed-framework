package com.aditshah.distributed

import com.sudothought.common.util.sleep
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlin.concurrent.thread
import kotlin.time.days
import kotlin.time.seconds

class Server(port: Int, info: SharedInfo) {
    val server = embeddedServer(Netty, port = 8080) {
        routing {
            static("static") {
                resources("static")
            }
            get("/drones") {
                call.respondWith(info.toJson(), ContentType.Application.Json)
            }
        }
    }

    fun run() {
        server.start(wait = false)
    }
}

fun main() {
    val info = SharedInfo(CoordinateArea(Coordinate(0, 0), Coordinate(100, 100)))
    val server = Server(8080, info)
    server.run()
    for (i in 1..3) {
        thread {
            val drone = MyDrone1(i, Coordinate(0, 0), info)
            while (true) {
                sleep((i * 1).seconds)
                drone.move()
            }
        }
    }
    sleep(1.days)
}
