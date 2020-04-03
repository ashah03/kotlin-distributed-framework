package com.aditshah.distributed.node

import com.aditshah.distributed.common.Coordinate
import kotlin.random.Random

class IterativeRandomDroneAlgorithm : IterativeDroneAlgorithm() {
    override fun move() {
        val location = Coordinate(
            Random.nextInt(0, 10),
            Random.nextInt(0, 10),
            Random.nextInt(0, 10)
        )
        val weight = Random.nextDouble(1.0, 11.0)
        api.putLocation(location)
        api.putWeight(location, weight)
    }
}
