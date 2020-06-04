package com.aditshah.distributed.infrastructure.common

import kotlinx.serialization.Serializable
import kotlin.random.Random

/*
 * This class represents a cubic coordinate area, defined as the cube between 2 corners in 3 dimensions (or in 2 dimensions
 * represents a square area). It also provides utility functions for generating random locations
 */
@Serializable
data class CoordinateArea(val topLeft: Coordinate, val bottomRight: Coordinate) {
    fun contains(coord: Coordinate): Boolean {
        return (coord.X >= topLeft.X && coord.Y >= topLeft.Y && coord.Z >= topLeft.Z) &&
               (coord.X <= bottomRight.X && coord.Y <= bottomRight.Y && coord.Z <= bottomRight.Z)
    }

    // Generate random locations as Double in 3D
    fun genRandomLocationDouble(): Coordinate {
        val x = if (topLeft.X == bottomRight.X) topLeft.X else Random.nextDouble(
            topLeft.X,
            bottomRight.X
        )
                .toInt()
                .toDouble()
        val y = if (topLeft.Y == bottomRight.Y) topLeft.X else Random.nextDouble(
            topLeft.Y,
            bottomRight.Y
        )
                .toInt()
                .toDouble()
        val z = if (topLeft.Z == bottomRight.Z) topLeft.X else Random.nextDouble(
            topLeft.Z,
            bottomRight.Z
        )
                .toInt()
                .toDouble()
        return Coordinate(x, y, z)
    }

    //Generate random locations as int in 3D
    fun genRandomLocationInt(): Coordinate {
        val x = if (topLeft.X == bottomRight.X) topLeft.X else Random.nextInt(
            topLeft.X.toInt(),
            bottomRight.X.toInt()
        )
                .toInt()
                .toDouble()
        val y = if (topLeft.Y == bottomRight.Y) topLeft.X else Random.nextInt(
            topLeft.Y.toInt(),
            bottomRight.Y.toInt()
        )
                .toInt()
                .toDouble()
        val z = if (topLeft.Z == bottomRight.Z) topLeft.X else Random.nextInt(
            topLeft.Z.toInt(),
            bottomRight.Z.toInt()
        )
                .toInt()
                .toDouble()
        return Coordinate(x, y, z)
    }

    // Generate random locations as Int in 2D
    fun genRandomLocationInt2D(): Coordinate {
        return genRandomLocationInt().apply { Z = 0.0 }
    }
}