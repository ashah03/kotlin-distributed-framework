package com.aditshah.distributed

import com.github.pambrose.common.util.sleep
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.*
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.response.header
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.serialization.DefaultJsonConfiguration
import io.ktor.serialization.serialization
import io.ktor.server.engine.ShutDownUrl
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.serialization.json.Json
import org.slf4j.event.Level
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.time.seconds

suspend fun ApplicationCall.respondWith(content: String, contentType: ContentType) {
    response.header("cache-control", "must-revalidate,no-cache,no-store")
//    response.header("Access-Control-Allow-Credentials", "true")
//    response.header("Access-Control-Allow-Headers", "accept,origin,authorization,content-type")
//    response.header("Access-Control-Allow-Methods", "*")
    response.header("Access-Control-Allow-Origin", "*")
    response.status(HttpStatusCode.OK)
    respondText(content, contentType)
}

class Server(port: Int, info: MapSharedInfo) {

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

        install(ShutDownUrl.ApplicationCallFeature) {
            // The URL that will be intercepted
            shutDownUrl = "/shutdown"
            // A function that will be executed to get the exit code of the process
            exitCodeSupplier = { 0 } // ApplicationCall.() -> Int
        }

        routing {
            static("static") {
                resources("static")
            }

            get("/drone") {
                val strID = call.parameters["id"]
                if (strID == null) call.respond(HttpStatusCode.BadRequest, "No ID specified")
                val id: Int = Integer.parseInt(strID)
                if (info.getIDs().contains(id)) {
                    call.respondWith(info.getLocation(id).toJson(), ContentType.Application.Json)
                } else {
                    call.respond(HttpStatusCode.BadRequest, "400: Invalid ID")
                }

            }

            get("/drones") {
                //call.respondWith(info.toJson(), ContentType.Application.Json)
                val json = LocationMapObj(info.locationMap.toMap()).toJson()
                call.respondWith(json, ContentType.Application.Json)
//                call.respond(info.locationMap.toMap())
            }
        }
    }

    fun run() {
        server.start(wait = false)
    }

    fun stop() {
        server.stop(0, 0, TimeUnit.SECONDS)
    }
}

fun main() {
    val info = MapSharedInfo(CoordinateArea(Coordinate(0, 0), Coordinate(100, 100)))
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

}
