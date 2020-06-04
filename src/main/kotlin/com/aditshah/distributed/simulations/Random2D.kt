package com.aditshah.distributed.simulations

import com.aditshah.distributed.infrastructure.common.Coordinate
import com.aditshah.distributed.infrastructure.common.random
import com.aditshah.distributed.infrastructure.simulation.IterativeDiscreteSimulation
import kotlin.time.seconds


/*
 * This file is an example of how to use the IterativeDiscreteSimulation class to create a simulation.
 * It is the most simple example of this, as it just randomly generates the locations each time
 */


val random = IterativeDiscreteSimulation.create {
    system(10) {
        system10Random()
    }


    algorithm(delayPeriod = 1.seconds) {
        val coverageRadius = 2.237
        val moveRadius = 1

        val location = Coordinate(10.random, 10.random)
//        val location = Coordinate(19, 19)
        putLocation(location)
        getWeight(location)
//            putWeight(location, 10.0.random)
    }
}

fun main() {
    random.start()
}


