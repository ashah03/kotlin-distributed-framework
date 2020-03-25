package com.aditshah.distributed.client

import com.aditshah.distributed.common.Coordinate
import com.aditshah.distributed.common.CoordinateArea

abstract class Node(
    startingLocation: Coordinate,
    val coordinateArea: CoordinateArea,
    val host: String,
    val port: Int
) {

    val client = GrpcClient(this)

    init {
        require(coordinateArea.contains(startingLocation)) { "Location $startingLocation not in area $coordinateArea" }
    }

    var location: Coordinate = startingLocation
        set(value) {
            require(coordinateArea.contains(location)) { "Location $location not in area $coordinateArea" }
            client.putLocation()
            field = value
        }

    val id = client.registerNode();

    var stopped: Boolean = false;


    abstract fun move()
    abstract fun start()
    open fun stop() {
        stopped = true;
        client.close();
    }
}

