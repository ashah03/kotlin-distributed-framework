package com.aditshah.distributed.node

import com.aditshah.distributed.common.Coordinate
import com.aditshah.distributed.common.CoordinateArea
import com.aditshah.distributed.common.random
import kotlin.time.seconds


fun main() {
    val node = IterativeDiscreteAlgorithmDSL.create {
        config {
            startingLocationGenerator = { coordinateArea.genRandomLocationInt() }
            coordinateArea = CoordinateArea(
                topLeft = Coordinate(0, 0, 0),
                bottomRight = Coordinate(10, 10, 10)
            )
            weightMap = WeightsMap("csv/map10.csv")
        }

        delayPeriod = 1.seconds

        val coverageRadius = 2.237

        periodic {
            val currentLocation = this.getLocation(this.getID())

            fun getAdjacentLocations() {
                val idList = mutableListOf<Coordinate>()
                currentLocation.apply {
                    for (x in (X.toInt() - 1)..(X.toInt() + 1)) {
                        for (y in (Y.toInt() - 1)..(Y.toInt() + 1)) {
                            val coord = Coordinate(x, y)
                            if (getCoordinateArea().contains(coord)) {
                                idList.add(coord)
                            }
                        }
                    }
                }
            }

            val location = Coordinate(10.random, 10.random, 10.random)
            putLocation(location)
            putWeight(location, 10.0.random)
        }
    }

    node.start()
    node.stop()
}
