package com.aditshah.distributed.simulations

import com.aditshah.distributed.infrastructure.common.Coordinate
import com.aditshah.distributed.infrastructure.common.CoordinateArea
import com.aditshah.distributed.infrastructure.common.WeightsMap
import com.aditshah.distributed.infrastructure.simulation.IterativeDiscreteSimulation
import com.aditshah.distributed.infrastructure.simulation.getLocationsByRadius
import com.aditshah.distributed.infrastructure.simulation.getValue
import kotlin.time.seconds

fun main() {

    val simulation = IterativeDiscreteSimulation.create {
        system(5) {
            startingLocationGenerator = { coordinateArea.genRandomLocationInt2D() }
//            startingLocationGenerator = { coordinateArea.topLeft }
            coordinateArea = CoordinateArea(topLeft = Coordinate(0, 0), bottomRight = Coordinate(49, 49))
            weightMap = WeightsMap("csv/map50norm.csv")
        }

        val coverageRadius = 3.16 //2.24
        val movementRadius = 1.415

        visualization {
            addComponent("/coverageRadius", coverageRadius)
        }

        algorithm(delayPeriod = 0.seconds) {
            val currentLocation = getLocation(getID())
            println("Current weight = " + getValue(getLocation(getID()), coverageRadius))
            val weightLocationMap = getLocationsByRadius(currentLocation, movementRadius).map { location ->
                getValue(location, coverageRadius) to location
            }
                    .toMap()
            val bestLocation = weightLocationMap[weightLocationMap.keys.max()] ?: currentLocation
            putLocation(bestLocation)
        }
    }
    simulation.start()
}


