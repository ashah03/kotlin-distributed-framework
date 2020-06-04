package com.aditshah.distributed.infrastructure.node

import com.aditshah.distributed.infrastructure.common.Coordinate
import com.aditshah.distributed.infrastructure.common.CoordinateArea
import com.aditshah.distributed.infrastructure.common.MapSharedInfo

interface Node {
    fun registerNode(): Int
    fun shutdownNode()
    fun getID(): Int
    fun getAllIDs(): MutableSet<Int>
    fun getCoordinateArea(): CoordinateArea
    fun putLocation(coord: Coordinate)
    fun getLocation(id: Int): Coordinate
    fun getWeight(coord: Coordinate): Double
    fun putWeight(coord: Coordinate, value: Double)
    fun isStopped(): Boolean
    fun getInfo(): MapSharedInfo
}

