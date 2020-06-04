package com.aditshah.distributed.simulations

import com.aditshah.distributed.infrastructure.common.Coordinate
import com.aditshah.distributed.infrastructure.node.Node
import com.aditshah.distributed.infrastructure.simulation.getLocationsByRadius
import com.aditshah.distributed.infrastructure.simulation.getValue
import kotlin.math.E
import kotlin.math.pow

/*
 * This file contains the logic for two algorithms: greedy and Log-Linear Learning
 */


fun Node.greedy2D(coverageRadius: Double, movementRadius: Double) {
    val currentLocation = getLocation(getID())
    println("Current weight = " + getValue(getLocation(getID()), coverageRadius))

    // Create a map between the utility of each possible location and the coordinates of the new location
    val weightLocationMap = getLocationsByRadius(currentLocation, movementRadius).map { location ->
        getValue(location, coverageRadius) to location
    }
            .toMap()

    // Determine the best location to move to greedily; i.e. always pick the one with the highest utility
    val bestLocation = weightLocationMap[weightLocationMap.keys.max()] ?: currentLocation

    // Set this location (in the backend, broadcast it to all of the other drones so they can update their knowledge of the map
    putLocation(bestLocation)
}

data class Tau(var value: Double)

fun Node.LLL2D(coverageRadius: Double, movementRadius: Double, tau: Tau, decay: Double = 0.997) {

    val currentLocation = getLocation(getID())
    println("Current weight = " + getValue(getLocation(getID()), coverageRadius))

    // Create a map between the utility of each possible location and the coordinates of the new location
    val weightLocationMap = getLocationsByRadius(currentLocation, movementRadius).map { location ->
        getValue(location, coverageRadius) to location
    }
            .toMap()

    // Create a probability distribution based on the Log Linear Learning algorithm. pLocationMapNorm contains
    // the move (key) and the probability of picking it
    val pLocations = getProbabilityDistribution(weightLocationMap, tau.value)

    //Pick a move based on the probability distribution in pLocations
    val location = selectMove(pLocations)
    putLocation(location ?: currentLocation)

    tau.value *= decay

}

fun p(tau: Double, u: Double): Double {
    val p = E.pow((1.0 / tau) * u)
    if (p.isNaN()) {
        return 1.0
    } else {
        return p
    }
}

fun getProbabilityDistribution(weightLocationMap: Map<Double, Coordinate>, tau: Double): Map<Coordinate, Double> {
    val pLocationMapNumerator = weightLocationMap.map { it.value to p(tau, it.key) }
            .toMap()
    val sumP = pLocationMapNumerator.values.sum()
    return pLocationMapNumerator.map { it.key to it.value / sumP }
            .toMap()
}

fun selectMove(pLocations: Map<Coordinate, Double>): Coordinate? {
    val rand = Math.random()
    var currentCount = 0.0
    for ((location, probability) in pLocations) {
        currentCount += probability
        if (rand < currentCount) {
            return location
        }
    }
    return null
}
