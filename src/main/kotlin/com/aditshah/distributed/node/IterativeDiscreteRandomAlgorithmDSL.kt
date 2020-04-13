package com.aditshah.distributed.node

import com.aditshah.distributed.common.Coordinate
import com.aditshah.distributed.common.CoordinateArea
import com.aditshah.distributed.common.random
import kotlin.time.seconds

class IterativeDiscreteRandomAlgorithmDSL

fun main() {


    val node = IterativeDiscreteAlgorithmDSL.create {

        config {
            coordinateArea = CoordinateArea(
                topLeft = Coordinate(0, 0, 0),
                bottomRight = Coordinate(10, 10, 10)
            )
            startingLocationGenerator = { coordinateArea.genRandomLocationDouble() }
            weightMap = WeightsMap("csv/map10.csv")
        }

        delayPeriod = 1.seconds

        val coverageRadius = 2.237
        val moveRadius = 1

        periodic {
            val location = Coordinate(10.random, 10.random, 10.random)
            putLocation(location)
            putWeight(location, 10.0.random)
        }
    }

    node.start()
    node.stop()
}


