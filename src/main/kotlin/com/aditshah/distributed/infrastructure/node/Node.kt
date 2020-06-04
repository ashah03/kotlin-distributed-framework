package com.aditshah.distributed.infrastructure.node

import com.aditshah.distributed.infrastructure.common.Coordinate
import com.aditshah.distributed.infrastructure.common.CoordinateArea
import com.aditshah.distributed.infrastructure.common.MapSharedInfo

/*
 * This Node interface contains all of the methods that a user of the NodeSimulation has access to
 * This is kept as an interface so that the API calls that can be made are clear, and the backend implementation
 * can be modified in the future
 */
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

