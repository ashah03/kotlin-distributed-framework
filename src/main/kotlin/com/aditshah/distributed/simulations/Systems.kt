package com.aditshah.distributed.simulations

import com.aditshah.distributed.infrastructure.common.Coordinate
import com.aditshah.distributed.infrastructure.common.CoordinateArea
import com.aditshah.distributed.infrastructure.common.WeightsMap
import com.aditshah.distributed.infrastructure.simulation.NodeSimulation

/*
 * This file contains a bunch of different presets for system configurations by grid size and starting position
 */
fun NodeSimulation.NodeBuilder.system10Corner() {
    startingLocationGenerator = { coordinateArea.topLeft }
    coordinateArea = CoordinateArea(topLeft = Coordinate(0, 0), bottomRight = Coordinate(9, 9))
    weightMap = WeightsMap("csv/map10norm.csv")
}

fun NodeSimulation.NodeBuilder.system10Random() {
    startingLocationGenerator = { coordinateArea.genRandomLocationInt2D() }
    coordinateArea = CoordinateArea(topLeft = Coordinate(0, 0), bottomRight = Coordinate(9, 9))
    weightMap = WeightsMap("csv/map10norm.csv")
}

fun NodeSimulation.NodeBuilder.system20Corner() {
    startingLocationGenerator = { coordinateArea.topLeft }
    coordinateArea = CoordinateArea(topLeft = Coordinate(0, 0), bottomRight = Coordinate(19, 19))
    weightMap = WeightsMap("csv/map20norm.csv")
}

fun NodeSimulation.NodeBuilder.system20Random() {
    startingLocationGenerator = { coordinateArea.genRandomLocationInt2D() }
    coordinateArea = CoordinateArea(topLeft = Coordinate(0, 0), bottomRight = Coordinate(19, 19))
    weightMap = WeightsMap("csv/map20norm.csv")
}


fun NodeSimulation.NodeBuilder.system50Corner() {
    startingLocationGenerator = { coordinateArea.topLeft }
    coordinateArea = CoordinateArea(topLeft = Coordinate(0, 0), bottomRight = Coordinate(49, 49))
    weightMap = WeightsMap("csv/map50norm.csv")
}

fun NodeSimulation.NodeBuilder.system50Random() {
    startingLocationGenerator = { coordinateArea.genRandomLocationInt2D() }
    coordinateArea = CoordinateArea(topLeft = Coordinate(0, 0), bottomRight = Coordinate(49, 49))
    weightMap = WeightsMap("csv/map50norm.csv")
}

fun NodeSimulation.NodeBuilder.system100Corner() {
    startingLocationGenerator = { coordinateArea.topLeft }
    coordinateArea = CoordinateArea(topLeft = Coordinate(0, 0), bottomRight = Coordinate(99, 99))
    weightMap = WeightsMap("csv/map100norm.csv")
}

fun NodeSimulation.NodeBuilder.system100Random() {
    startingLocationGenerator = { coordinateArea.genRandomLocationInt2D() }
    coordinateArea = CoordinateArea(topLeft = Coordinate(0, 0), bottomRight = Coordinate(99, 99))
    weightMap = WeightsMap("csv/map100norm.csv")
}

