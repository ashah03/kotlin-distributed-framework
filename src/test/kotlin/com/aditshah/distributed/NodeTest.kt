package com.aditshah.distributed

import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.Test

class NodeTest {
    @Test
    fun myDrone1Test() {
        val area = CoordinateArea(Coordinate(0, 0), Coordinate(100, 100))
        val info = MapSharedInfo(coordinateArea = area)
        info.coordinateArea shouldEqual area
        val c1 = Coordinate(5, 7)
        val drone = MyDrone1(82, c1, info)
        drone.id shouldEqual 82
        c1 shouldEqual drone.location
        drone.location shouldEqual info.locationMap[drone.id]
        drone.move()
        drone.location shouldEqual info.locationMap[drone.id]
        info.locationMap.keys.size shouldEqual 1
        val c2 = Coordinate(9, 3)
        val drone2 = MyDrone1(61, c2, info)
        drone2.id shouldEqual 61
        c2 shouldEqual drone2.location
        drone2.location shouldEqual info.locationMap[drone2.id]
        drone2.move()
        drone2.location shouldEqual info.locationMap[drone2.id]
        info.locationMap.keys.size shouldEqual 2
    }
}
