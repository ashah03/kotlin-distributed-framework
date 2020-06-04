package com.aditshah.distributed.infrastructure.visualization

import com.aditshah.distributed.infrastructure.common.LocationMapObj
import com.aditshah.distributed.infrastructure.common.MapSharedInfo
import com.aditshah.distributed.infrastructure.common.WeightMapObj
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
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.serialization.DefaultJsonConfiguration
import io.ktor.serialization.serialization
import io.ktor.server.engine.ShutDownUrl
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.serializer
import org.slf4j.event.Level
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

suspend fun ApplicationCall.respondWith(content: String, contentType: ContentType) {
    response.header("cache-control", "must-revalidate,no-cache,no-store")
//    response.header("Access-Control-Allow-Credentials", "true")
//    response.header("Access-Control-Allow-Headers", "accept,origin,authorization,content-type")
//    response.header("Access-Control-Allow-Methods", "*")
    response.header("Access-Control-Allow-Origin", "*")
    response.status(HttpStatusCode.OK)
    respondText(content, contentType)
}

class VisualizationServer(
    info: MapSharedInfo,
    val additionalComponents: MutableList<Component> = mutableListOf(),
    port: Int = 8080
) {
//    val info = node.getInfo()


    val server = embeddedServer(Netty, port = port) {
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
                if (info.getIDs()
                            .contains(id)
                ) {
                    call.respondWith(
                        info.getLocation(id)
                                .toJson(), ContentType.Application.Json
                    )
                } else {
                    call.respond(HttpStatusCode.BadRequest, "400: Invalid ID")
                }

            }


            get("/drones") {
                //call.respondWith(info.toJson(), ContentType.Application.Json)
                val json = LocationMapObj(info.locationMap.toMap())
                        .toJson()
                call.respondWith(json, ContentType.Application.Json)
//                call.respond(info.locationMap.toMap())
            }

            get("/map") {
                println("Returning map")
                val json = WeightMapObj(info.weightMap.map.toMap())
                        .toJson()
                call.respondWith(json, ContentType.Application.Json)
            }

            get("/size") {
                val x: Int = (info.coordinateArea.bottomRight.X - info.coordinateArea.topLeft.X).roundToInt()
                val y: Int = (info.coordinateArea.bottomRight.Y - info.coordinateArea.topLeft.Y).roundToInt()
                call.respondWith(XYSize(x, y).toJson(), ContentType.Application.Json)
            }

            get("/data") {
                val data = Data(
                    WeightMapObj(info.weightMap.map.toMap()),
                    LocationMapObj(info.locationMap.toMap())
                )
                call.respondWith(data.toJson(), ContentType.Application.Json)
            }

            additionalComponents.forEach { createGet(it) }

        }
    }

    fun start() {
        server.start(wait = false)
    }

    fun stop() {
        server.stop(0, 0, TimeUnit.SECONDS)
    }

}

@Serializable
data class XYSize(val x: Int, val y: Int) {
    fun toJson(): String = Json.stringify(serializer(), this)
}

@Serializable
data class Data(val weights: WeightMapObj, val drones: LocationMapObj) {
    fun toJson(): String = Json(JsonConfiguration(allowStructuredMapKeys = true)).stringify(serializer(), this)
}

class Component(val path: String, val data: Any)

fun Routing.createGet(component: Component) {
    with(component) {
        get(path) {
            if (data is Serializable) {
                call.respondWith(data.toJson(), ContentType.Application.Json)
            } else {
                call.respondWith(data.toString(), ContentType.Application.Json)
            }
        }
    }
}

fun Serializable.toJson(): String = Json(JsonConfiguration(allowStructuredMapKeys = true)).stringify(serializer(), this)

