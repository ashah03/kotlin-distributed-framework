package com.aditshah.distributed

abstract class Node(
    val id: Int,
    startingLocation: Coordinate,
    val info: SharedInfo
) {

    var location: Coordinate = startingLocation
        set(value) {
            require(info.coordinateArea.contains(location)) { "Location $location not in area ${info.coordinateArea}" }
            info.putLocation(this.id, value)
            field = value
        }

    init {
        require(info.coordinateArea.contains(startingLocation)) { "Location $startingLocation not in area ${info.coordinateArea}" }
        info.putLocation(this.id, this.location)
    }

    abstract fun move()

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val area = CoordinateArea(Coordinate(0, 0), Coordinate(100, 100))
            val info = MapSharedInfo(coordinateArea = area)
            println(area.contains(Coordinate(5, 5)))
            println(area.contains(Coordinate(200, 5)))
            val node1 = MyDrone1(id = 1, info = info, location = Coordinate(5, 5))
            val node2 = MyDrone1(id = 2, info = info, location = Coordinate(0, 0))
            println(info.getLocation(node1.id))
            println(info.getLocation(node2.id))
            node1.location = Coordinate(6, 4);
            println(info.getLocation(node1.id))
            println(info.getLocation(node2.id))
            node2.location = Coordinate(3, 4);
            println(info.getLocation(node2.id))
            println(node2.location)
//            node2.startingLocation = Coordinate(201, 4);
            println(node2.location)
        }
    }
}

