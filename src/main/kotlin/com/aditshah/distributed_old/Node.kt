package com.aditshah.distributed_old

abstract class Node(
    val id: Int,
    startingLocation: Coordinate,
    val info: SharedInfo
) {

    var location: Coordinate = startingLocation
        set(value) {
            require(info.coordinateArea.contains(location)) { "Location $location not in area ${info.coordinateArea}" }
            info.putLocation(this.id, value)
            field = value
        }

    init {
        require(info.coordinateArea.contains(startingLocation)) { "Location $startingLocation not in area ${info.coordinateArea}" }
        info.putLocation(this.id, this.location)
    }

    abstract fun move()
    abstract fun start()
    abstract fun stop()
}

