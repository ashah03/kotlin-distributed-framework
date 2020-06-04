package com.aditshah.distributed.infrastructure.simulation

import com.aditshah.distributed.infrastructure.common.Coordinate
import com.aditshah.distributed.infrastructure.node.Node
import kotlin.math.ceil

fun Node.getLocationsByRadius(location: Coordinate, r: Double): MutableSet<Coordinate> {
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

fun Node.findCoveredLocations(coverageRadius: Double): MutableList<Coordinate> {
    val coveredLocations = mutableListOf<Coordinate>()
    for (id in getAllIDs()) {
        if (id != getID()) {
            coveredLocations.addAll(getLocationsByRadius(getLocation(id), coverageRadius))
        }
    }
    return coveredLocations
}

fun Node.getValue(coordinate: Coordinate, coverageRadius: Double): Double {
    var weight = 0.0;
    for (location in getLocationsByRadius(coordinate, coverageRadius)) {
        if (!findCoveredLocations(coverageRadius).contains(location)) {
            weight += getWeight(location)
        }
    }
    return weight
}