package com.aditshah.distributed

import kotlin.random.Random

class MyDrone1(id: Int, location: Coordinate, info: SharedInfo) : Drone(id, location, info) {
    override fun move() {
        var newLoc: Coordinate
//        do {
            newLoc = genRandomLocation(info.coordinateArea)
//        } while (info.locationMap.containsValue(newLoc))
        location = newLoc
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val area = CoordinateArea(Coordinate(0, 0), Coordinate(100, 100))
            val info = MapSharedInfo(coordinateArea = area)
            val c1 = Coordinate(5, 7)
            val drone = MyDrone1(82, c1, info)
            println(drone.location)
            println(info.getLocation(drone.id))
            drone.move()
            println(drone.location)
            println(info.getLocation(drone.id))
        }

        fun genRandomLocation(area: CoordinateArea): Coordinate {
            return with(area) {
                val x = if (start.X == end.X) start.X else Random.nextInt(start.X, end.X)
                val y = if (start.Y == end.Y) start.Y else Random.nextInt(start.Y, end.Y)
                val z = if (start.Z == end.Z) start.Z else Random.nextInt(start.Z, end.Z)
                Coordinate(x, y, z)
            }
        }
    }
}