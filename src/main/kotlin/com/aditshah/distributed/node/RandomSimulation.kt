package com.aditshah.distributed.node

import com.aditshah.distributed.common.Coordinate
import com.aditshah.distributed.common.CoordinateArea
import kotlin.time.seconds

class IterativeDiscreteRandomAlgorithmDSL

fun main() {


    val simulation = IterativeDiscreteSimulation.create {
        system(10) {
            coordinateArea = CoordinateArea(
                topLeft = Coordinate(0, 0, 0),
                bottomRight = Coordinate(20, 20, 20)
            )
            startingLocationGenerator = { coordinateArea.genRandomLocationInt2D() }
            weightMap = WeightsMap("csv/map20norm.csv")
        }


        algorithm(delayPeriod = 1.seconds) {
            val coverageRadius = 2.237
            val moveRadius = 1

//            val location = Coordinate(10.random, 10.random)
            val location = Coordinate(19, 19)
            putLocation(location)
            getWeight(location)
//            putWeight(location, 10.0.random)
        }
    }

    simulation.start()
//    simulation.stop()
}


