package com.aditshah.distributed.simulations

import com.aditshah.distributed.infrastructure.simulation.IterativeDiscreteSimulation
import kotlin.time.seconds

fun main() {

    val simulation = IterativeDiscreteSimulation.create {

        system(5) {
            system50Corner()
        }

        val coverageRadius = 3.16 //2.24
        val movementRadius = 1.415

        //Setting some constants for the algorithm (defined here in case they need to be used for visualization component)
        visualization {
            addComponent("/coverageRadius", coverageRadius)
        }

        algorithm(delayPeriod = 0.5.seconds) {
            greedy2D(coverageRadius, movementRadius)
        }
    }
    simulation.start()
}


