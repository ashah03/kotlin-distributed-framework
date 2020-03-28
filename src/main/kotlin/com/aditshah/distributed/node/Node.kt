package com.aditshah.distributed.node

import com.aditshah.distributed.common.Coordinate
import kotlin.concurrent.thread

abstract class Node(
    startingLocation: Coordinate,
    val info: MapSharedInfo,
    val host: String,
    val port: Int
) {

    val client = NodeClient(this)

    init {
        require(info.coordinateArea.contains(startingLocation)) { "Location $startingLocation not in area ${info.coordinateArea}" }
    }

    var location: Coordinate = startingLocation
        set(value) {
            require(info.coordinateArea.contains(location)) { "Location $location not in area ${info.coordinateArea}" }
            client.putLocation()
            field = value
        }


    val id = client.registerNode();
    var stopped: Boolean = false;

    init {
        client.putLocation();
        thread {
            client.subscribeLocation();
        }
    }

    abstract fun move()
    abstract fun start()
    open fun stop() {
        stopped = true;
        client.close();
    }
}

