package com.aditshah.distributed.node

import com.aditshah.distributed.common.Coordinate
import com.aditshah.distributed.common.CoordinateArea
import kotlin.math.ceil
import kotlin.time.seconds


fun main() {
    val simulation = IterativeDiscreteSimulation.create {
        system(3) {
            startingLocationGenerator = { coordinateArea.genRandomLocationInt2D() }
            coordinateArea = CoordinateArea(
                topLeft = Coordinate(0, 0, 0),
                bottomRight = Coordinate(19, 19, 0)
            )
            weightMap = WeightsMap("csv/map20norm.csv")
        }

        val coverageRadius = 2.237
        val movementRadius = 1.415

        algorithm(delayPeriod = 1.seconds) {
            val currentLocation = getLocation(getID())

//            fun getAdjacentLocations(): MutableList<Coordinate> {
//                val adjList = mutableListOf<Coordinate>()
//                currentLocation.apply {
//                    for (x in (X.toInt() - 1)..(X.toInt() + 1)) {
//                        for (y in (Y.toInt() - 1)..(Y.toInt() + 1)) {
//                            val coord = Coordinate(x, y)
//                            if (getCoordinateArea().contains(coord)) {
//                                adjList.add(coord)
//                            }
//                        }
//                    }
//                }
//                return adjList
//            }

            fun getLocationsByRadius(location: Coordinate = currentLocation, r: Double): MutableSet<Coordinate> {
                val adjSet = mutableSetOf<Coordinate>()
                location.apply {
                    for (x in (X.toInt() - ceil(r).toInt())..(X.toInt() + ceil(r).toInt())) {
                        for (y in (Y.toInt() - ceil(r).toInt())..(Y.toInt() + ceil(r).toInt())) {
                            val coord = Coordinate(x, y)
                            if (getCoordinateArea().contains(coord) && coord - location <= r) {
                                adjSet.add(coord)
                            }
                        }
                    }
                }
                return adjSet
            }

            fun findCoveredLocations(): MutableList<Coordinate> {
                val coveredLocations = mutableListOf<Coordinate>()
                for (id in getAllIDs()) {
                    if (id != getID()) {
                        coveredLocations.addAll(getLocationsByRadius(getLocation(id), coverageRadius))
                    }
                }
                return coveredLocations
            }

            fun getValue(coordinate: Coordinate): Double {
                var weight = 0.0;
                for (location in getLocationsByRadius(coordinate, coverageRadius)) {
                    if (!findCoveredLocations().contains(location)) {
                        weight += getWeight(location)
                    }
                }
                return weight
            }
            println("Current weight = " + getValue(getLocation(getID())))

            val weightToLocation = getLocationsByRadius(currentLocation, movementRadius).map { getValue(it) to it }
                    .toMap()
            val bestLocation = weightToLocation[weightToLocation.keys.max()] ?: currentLocation

            putLocation(bestLocation)
//            putLocation(Coordinate(19.0, 19.0))
//            putWeight(location, 10.0.random)
        }
    }

    simulation.start()
//    simulation.stop()
}

