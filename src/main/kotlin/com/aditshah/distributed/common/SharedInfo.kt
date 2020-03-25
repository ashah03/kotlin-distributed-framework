//etcd --listen-client-urls=http://localhost:2379 --advertise-client-urls=http://localhost:2379

package com.aditshah.distributed.common

interface SharedInfo {
    val coordinateArea: CoordinateArea
    fun putLocation(id: Int, coord: Coordinate)
    fun getLocation(id: Int): Coordinate
    fun putWeight(coord: Coordinate, weight: Double)
    fun getWeight(coord: Coordinate): Double
    fun getIDs(): Set<Int>
}
