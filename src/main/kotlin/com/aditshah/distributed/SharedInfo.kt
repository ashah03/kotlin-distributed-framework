package com.aditshah.distributed

import com.google.common.collect.Maps
import kotlinx.serialization.Serializable
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentMap

@Serializable
data class SharedInfo(val coordinateArea: CoordinateArea) {
    val locationMap: ConcurrentMap<Int, Coordinate> = Maps.newConcurrentMap()
    val weightsMap: ConcurrentMap<Coordinate, Double> = Maps.newConcurrentMap()

    fun toJson() = Json.stringify(serializer(), this)

    @UnstableDefault
    companion object {
        @JvmStatic
        fun toObject(json: String) = Json.parse(serializer(), json)
    }
}

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