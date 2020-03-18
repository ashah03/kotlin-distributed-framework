package com.aditshah.distributed

import com.aditshah.distributed_old.*
import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.Test

class NodeTest {
    @Test
    fun myDrone1Test() {
        val area = CoordinateArea(Coordinate(0, 0), Coordinate(100, 100))
        val info: SharedInfo = MapSharedInfo(area, WeightsMap("csv/map10.csv"))
        info.coordinateArea shouldEqual area
        val c1 = Coordinate(5, 7)
        val drone = MyDrone1(82, c1, info)
        drone.id shouldEqual 82
        c1 shouldEqual drone.location
        drone.location shouldEqual info.getLocation(drone.id)
        drone.move()
        drone.location shouldEqual info.getLocation(drone.id)
        info.getIDs().size shouldEqual 1
        val c2 = Coordinate(9, 3)
        val drone2 = MyDrone1(61, c2, info)
        drone2.id shouldEqual 61
        c2 shouldEqual drone2.location
        drone2.location shouldEqual info.getLocation(drone2.id)
        drone2.move()
        drone2.location shouldEqual info.getLocation(drone2.id)
        info.getIDs().size shouldEqual 2
    }
}
