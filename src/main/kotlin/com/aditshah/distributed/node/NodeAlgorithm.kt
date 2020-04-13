package com.aditshah.distributed.node

import com.aditshah.distributed.common.Coordinate
import com.aditshah.distributed.common.CoordinateArea
import java.net.InetAddress

abstract class NodeAlgorithm {

    lateinit var api: NodeAPI
    var initialized: Boolean = false

    fun init(api: GrpcNodeAPI) {
        this.api = api
        initialized = true
    }

    fun init(
        startingLocation: Coordinate,
        coordinateArea: CoordinateArea,
        weightMap: WeightsMap,
        host: String = getHostname().replace(".lan", ".local"),
        port: Int = 50051
    ) {

        api = GrpcNodeAPI(
            startingLocation,
            coordinateArea,
            weightMap,
            host,
            port
        )
        initialized = true
    }

    fun getHostname(): String {
        return InetAddress.getLocalHost().hostName
    }

    abstract fun start()
    open fun stop() = Unit
}
