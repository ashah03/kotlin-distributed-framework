package com.aditshah.distributed

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.response.header
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

suspend fun ApplicationCall.respondWith(content: String, contentType: ContentType) {
    response.header("cache-control", "must-revalidate,no-cache,no-store")
//    response.header("Access-Control-Allow-Credentials", "true")
//    response.header("Access-Control-Allow-Headers", "accept,origin,authorization,content-type")
//    response.header("Access-Control-Allow-Methods", "*")
    response.header("Access-Control-Allow-Origin", "*")
    response.status(HttpStatusCode.OK)
    respondText(content, contentType)
}

fun main() {
    val info = SharedInfo(CoordinateArea(Coordinate(0, 0), Coordinate(100, 100)))
    val drone1 = MyDrone1(1, Coordinate(0, 0), info)
    val drone2 = MyDrone1(2, Coordinate(0, 0), info)
    val drone3 = MyDrone1(3, Coordinate(0, 0), info)
    val server = embeddedServer(Netty, port = 8080) {
        routing {
            static("static") {
                resources("static")
            }
            get("/oneDrone") {
                drone1.move()
                call.respondWith(drone1.location.toJson(), ContentType.Application.Json)
            }
            get("/drones") {
                drone1.move()
                drone2.move()
                drone3.move()
                call.respondWith(info.toJson(), ContentType.Application.Json)
            }
        }
    }
    server.start(wait = true)
}