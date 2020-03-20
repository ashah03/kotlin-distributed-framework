//etcd --listen-client-urls=http://localhost:2379 --advertise-client-urls=http://localhost:2379

package com.aditshah.distributed

import com.google.common.collect.Maps
import io.ktor.util.InternalAPI
import kotlinx.serialization.Serializable
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

interface SharedInfo {
    val locationLock: Lock
    val coordinateArea: CoordinateArea
    fun putLocation(id: Int, coord: Coordinate)
    fun getLocation(id: Int): Coordinate
    fun putWeight(coord: Coordinate, weight: Double)
    fun getWeight(coord: Coordinate): Double
    fun getWeightsMap(): WeightsMap
    fun getIDs(): Set<Int>

}

//class EtcdSharedInfo(override val coordinateArea: CoordinateArea) : SharedInfo {
//}

@Serializable
data class LocationMapObj(val map: Map<Int, Coordinate>) {
    fun toJson(): String = Json.stringify(serializer(), this)
}

class MapSharedInfo(
    override val coordinateArea: CoordinateArea,
    val weightMap: WeightsMap
) : SharedInfo {


    override val locationLock = ReentrantLock()
    val locationMap: ConcurrentMap<Int, Coordinate> = Maps.newConcurrentMap()

    override fun getWeightsMap(): WeightsMap {
        return weightMap
    }

    @InternalAPI
    override fun getIDs(): MutableSet<Int> {
        return locationMap.keys
    }

    override fun putLocation(id: Int, coord: Coordinate) {
        locationMap[id] = coord
    }

    @InternalAPI
    override fun getLocation(id: Int): Coordinate {
        return locationMap[id] ?: throw AssertionError("location is null")
    }

    override fun putWeight(coord: Coordinate, weight: Double) {
        weightMap[coord] = weight
    }

    override fun getWeight(coord: Coordinate): Double {
        return weightMap[coord] ?: throw AssertionError("weight is null")
    }

    //fun toJson() = Json.stringify(serializer(), locationMap.toMap())


}

@Serializable
data class Coordinate(var X: Double, var Y: Double, var Z: Double = 0.0) {
    constructor(X: Int, Y: Int, Z: Int = 0) : this(X.toDouble(), Y.toDouble(), Z.toDouble())

    @UnstableDefault
    fun toJson() = Json.stringify(serializer(), this)

    infix fun distanceTo(other: Coordinate) =
        sqrt((other.X - this.X).pow(2) + (other.Y - this.Y).pow(2) + (other.Z - this.Z).pow(2))

    operator fun minus(other: Coordinate) = distanceTo(other)

    @UnstableDefault
    companion object {
        @JvmStatic
        fun toObject(json: String) = Json.parse(serializer(), json)
    }
}


@Serializable
data class CoordinateArea(val topLeft: Coordinate, val bottomRight: Coordinate) {
    fun contains(coord: Coordinate): Boolean {
        return (coord.X >= topLeft.X && coord.Y >= topLeft.Y && coord.Z >= topLeft.Z) &&
               (coord.X <= bottomRight.X && coord.Y <= bottomRight.Y && coord.Z <= bottomRight.Z)
    }

    fun genRandomLocation(): Coordinate {
        val x = if (topLeft.X == bottomRight.X) topLeft.X else Random.nextDouble(
            topLeft.X,
            bottomRight.X
        ).toInt().toDouble()
        val y = if (topLeft.Y == bottomRight.Y) topLeft.X else Random.nextDouble(
            topLeft.Y,
            bottomRight.Y
        ).toInt().toDouble()
        val z = if (topLeft.Z == bottomRight.Z) topLeft.X else Random.nextDouble(
            topLeft.Z,
            bottomRight.Z
        ).toInt().toDouble()
        return Coordinate(x, y, z)
    }

//    fun actionInRadiusInt(radius : Double, center : Coordinate, block : ( )->Unit ){
//        //Find the search square with sides = radius * 2
//        val squareTopLeftX = ceil(center.X - radius)
//        val squareTopLeftY = ceil(center.Y - radius)
//        val squareBottomRightX = floor(center.X + radius)
//        val squareBottomRightY = floor(center.Y + radius)
//
//        for (x in max(topLeft.X, squareTopLeftX).toInt() .. min(bottomRight.X, squareBottomRightX).toInt())
//            for(y in max(topLeft.Y, squareTopLeftY).toInt() .. min(bottomRight.Y, squareBottomRightY).toInt()){
//
//        }
//    }
}

