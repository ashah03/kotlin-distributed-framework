package com.aditshah.distributed.infrastructure.common

import com.aditshah.distributed.CoordinateMessage
import kotlinx.serialization.Serializable
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlin.math.pow
import kotlin.math.sqrt

@Serializable
data class Coordinate(var X: Double, var Y: Double, var Z: Double = 0.0) {
    constructor(X: Int, Y: Int, Z: Int = 0) : this(X.toDouble(), Y.toDouble(), Z.toDouble())

    constructor(coord: CoordinateMessage) : this(coord.x, coord.y, coord.z)

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