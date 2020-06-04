package com.aditshah.distributed.simulations

import com.aditshah.distributed.infrastructure.simulation.IterativeDiscreteSimulation
import kotlin.time.seconds

fun main() {

    val simulation = IterativeDiscreteSimulation.create {
        system(numNodes = 5) {
            system50Corner()
        }

        //Setting some constants for the algorithm (defined here in case they need to be used for visualization component)
        val coverageRadius = 3.16 //2.24
        val movementRadius = 1.415
//        val tau = 50.0
//        var tau = Tau(coverageRadius.pow(2) * 1.0)
        var tau = Tau(100000.0)
        val tauDecay = 0.999

        visualization {
            addComponent("/coverageRadius", coverageRadius)
        }

        algorithm(delayPeriod = 0.01.seconds) {
            LLL2D(coverageRadius, movementRadius, tau, tauDecay)
        }
    }
    simulation.start()
}


