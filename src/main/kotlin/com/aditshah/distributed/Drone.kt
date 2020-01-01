package com.aditshah.distributed


abstract class Drone(
    id: Int,
    location: Coordinate,
    info: SharedInfo,
    coverageRadius: Double = 1.0,
    minDistance: Double = 1.0,
    moveRadius: Double = 1.0
) : Node(id, location, info) {


    var coverageRadius: Double = coverageRadius
        set(value) {
            require(value > 0)
            field = value
        }

    var minDistance: Double = minDistance
        set(value) {
            require(value > 0)
            field = value
        }

    var moveRadius: Double = moveRadius
        set(value) {
            require(value > 0)
            field = value
        }

}