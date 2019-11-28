package com.aditshah.distributed

import com.github.pambrose.common.util.sleep
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.*
import io.ktor.http.ContentType
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.serialization.DefaultJsonConfiguration
import io.ktor.serialization.serialization
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.serialization.json.Json
import org.slf4j.event.Level
import kotlin.concurrent.thread
import kotlin.time.days
import kotlin.time.seconds

class Server(port: Int, info: SharedInfo) {

    val server = embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            serialization(
                contentType = ContentType.Application.Json,
                json = Json(DefaultJsonConfiguration.copy(prettyPrint = true))
            )
        }

        install(CallLogging) {
            level = Level.INFO
        }

        install(DefaultHeaders)

        install(Compression) {
            gzip {
                priority = 1.0
            }
            deflate {
                priority = 10.0
                minimumSize(1024) // condition
            }
        }

        routing {
            static("static") {
                resources("static")
            }
            get("/drones") {
                call.respondWith(info.toJson(), ContentType.Application.Json)
//                call.respond(info.locationMap.toMap())
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
