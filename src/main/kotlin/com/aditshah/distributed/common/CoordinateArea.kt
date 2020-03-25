package com.aditshah.distributed.common

import kotlinx.serialization.Serializable
import kotlin.random.Random

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
}