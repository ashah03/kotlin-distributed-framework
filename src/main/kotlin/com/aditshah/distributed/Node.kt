package com.aditshah.distributed

abstract class Node(
    val id: Int,
    val area: CoordinateArea,
    location: Coordinate,
    val info: SharedInfo) {

    var location: Coordinate = location
        set(value) {
            if (area.contains(value)) {
                info.locationMap[this.id] = value
                field = value
            } else {
                throw IllegalArgumentException("Location $value not in area ${this.area}")
            }
        }

    init {
        if (area.contains(location)) {
            info.locationMap[this.id] = this.location
        } else {
            throw IllegalArgumentException("Location $location not in area ${this.area}")
        }
    }

    abstract fun move()

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val info = SharedInfo();
            val area = CoordinateArea(Coordinate(0, 0), Coordinate(100, 100))
            println(area.contains(Coordinate(5,5)))
            println(area.contains(Coordinate(200,5)))
            var node1 = Drone(id = 1, info = info, location = Coordinate(5, 5), area = area)
            var node2 = Drone(id = 2, info = info, location = Coordinate(0, 0), area = area)
            println(info.locationMap[node1.id])
            println(info.locationMap[node2.id])
            node1.location = Coordinate(6, 4);
            println(info.locationMap[node1.id])
            println(info.locationMap[node2.id])
            node2.location = Coordinate(3, 4);
            println(info.locationMap[node2.id])
            println(node2.location)
//            node2.location = Coordinate(201, 4);
            println(node2.location)
        }
    }
}

data class SharedInfo(var locationMap: MutableMap<Int, Coordinate> = mutableMapOf())

data class Coordinate(var X: Int, var Y: Int, var Z: Int = 0)

data class CoordinateArea(val start: Coordinate, val end: Coordinate) {
    fun contains(coord: Coordinate): Boolean {
        return (coord.X >= start.X && coord.Y >= start.Y && coord.Z >= start.Z) &&
                (coord.X <= end.X && coord.Y <= end.Y && coord.Z <= end.Z)
    }
}