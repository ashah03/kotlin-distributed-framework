package com.aditshah.distributed.node

import com.aditshah.distributed.common.Coordinate

interface NodeAPI {
    fun registerNode(): Int
    fun shutdownNode()
    fun getID(): Int
    fun putLocation(coord: Coordinate)
    fun getLocation(id: Int): Coordinate
    fun getWeight(coord: Coordinate): Double
    fun putWeight(coord: Coordinate, value: Double)
    fun isStopped(): Boolean
}

