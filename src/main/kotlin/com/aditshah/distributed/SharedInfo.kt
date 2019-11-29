//etcd --listen-client-urls=http://localhost:2379 --advertise-client-urls=http://localhost:2379

package com.aditshah.distributed

import com.google.common.collect.Maps
import kotlinx.serialization.Serializable
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.util.concurrent.ConcurrentMap
import kotlin.collections.set
import kotlin.collections.toMap

fun main() {
    val area = CoordinateArea(Coordinate(0, 0), Coordinate(100, 100))
    val info = MapSharedInfo(area)
    val drone1 = MyDrone1(1, Coordinate(0, 0), info)
    val drone2 = MyDrone1(2, Coordinate(0, 0), info)
    val drone3 = MyDrone1(3, Coordinate(0, 0), info)
    drone1.move()
    drone2.move()
    drone3.move()
    println(info.toJson())
}

interface SharedInfo {
    val coordinateArea: CoordinateArea
    fun putLocation(id: Int, coord: Coordinate)
    fun getLocation(id: Int): Coordinate
    fun putWeight(coord: Coordinate, weight: Double)
    fun getWeight(coord: Coordinate): Double

}

class MapSharedInfo(override val coordinateArea: CoordinateArea) : SharedInfo {
    val locationMap: ConcurrentMap<Int, Coordinate> = Maps.newConcurrentMap()
    val weightsMap: ConcurrentMap<Coordinate, Double> = Maps.newConcurrentMap()

    override fun putLocation(id: Int, coord: Coordinate) {
        locationMap[id] = coord
    }

    override fun getLocation(id: Int): Coordinate {
        return locationMap[id] ?: throw AssertionError("location is null")
    }

    override fun putWeight(coord: Coordinate, weight: Double) {
        weightsMap[coord] = weight
    }

    override fun getWeight(coord: Coordinate): Double {
        return weightsMap[coord] ?: throw AssertionError("location is null")
    }

    fun toJson() = Json.stringify(serializer(), locationMap.toMap())

//    @UnstableDefault
//    companion object {
//        @JvmStatic
//        fun toObject(json: String) : Map<Int, Coordinate> = Json.parse(json)
//    }
}


//class SharedInfo2(val coordinateArea: CoordinateArea) {
//    val locationMap: ConcurrentMap<Int, Coordinate> = Maps.newConcurrentMap()
//    val weightsMap: ConcurrentMap<Coordinate, Double> = Maps.newConcurrentMap()
//
//    fun toJson(): String {
//        val jsonMap = locationMap.toMap()
//        return stringify(jsonMap)
//    }
//}

@Serializable
data class Coordinate(var X: Int, var Y: Int, var Z: Int = 0) {
    @UnstableDefault
    fun toJson() = Json.stringify(serializer(), this)

    @UnstableDefault
    companion object {
        @JvmStatic
        fun toObject(json: String) = Json.parse(serializer(), json)
    }
}


@Serializable
data class CoordinateArea(val start: Coordinate, val end: Coordinate) {
    fun contains(coord: Coordinate): Boolean {
        return (coord.X >= start.X && coord.Y >= start.Y && coord.Z >= start.Z) &&
                (coord.X <= end.X && coord.Y <= end.Y && coord.Z <= end.Z)
    }
}

