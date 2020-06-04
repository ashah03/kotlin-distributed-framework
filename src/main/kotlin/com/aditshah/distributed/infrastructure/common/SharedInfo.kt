//etcd --listen-client-urls=http://localhost:2379 --advertise-client-urls=http://localhost:2379

package com.aditshah.distributed.infrastructure.common

/*
 * This interface is used within the GrpcNode class to keep track of the information sent by the server locally on
 * each node.
 */
interface SharedInfo {
    val coordinateArea: CoordinateArea
    fun putLocation(id: Int, coord: Coordinate)
    fun getLocation(id: Int): Coordinate
    fun putWeight(coord: Coordinate, weight: Double)
    fun getWeight(coord: Coordinate): Double
    fun getIDs(): Set<Int>
}
