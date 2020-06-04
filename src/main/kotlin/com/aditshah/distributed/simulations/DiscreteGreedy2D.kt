/*
 * This file is an example of how to use the IterativeDiscreteSimulation class to create a simulation.
 * It uses one of the system declarations from Systems.kt and the greedy2D() algorithm from Algorithms.Kt
 */
package com.aditshah.distributed.simulations

import com.aditshah.distributed.infrastructure.simulation.IterativeDiscreteSimulation
import kotlin.time.seconds

fun main() {

    val simulation = IterativeDiscreteSimulation.create {

        system(5) {
            system50Corner()
        }

        //Setting some constants for the algorithm (defined here in case they need to be used for visualization component)
        val coverageRadius = 3.16 //2.24
        val movementRadius = 1.415

        visualization {
            addComponent("/coverageRadius", coverageRadius)
        }

        algorithm(delayPeriod = 0.3.seconds) {
            greedy2D(coverageRadius, movementRadius)
        }
    }
    simulation.start()
}


