package com.aditshah.distributed.node

import com.aditshah.distributed.common.random
import kotlin.concurrent.fixedRateTimer
import kotlin.time.Duration

class IterativeDiscreteSimulation : NodeSimulation() {

    lateinit var periodicFunction: Node.() -> Unit
    var delayPeriod: Duration? = null
        private set


    override fun Node.start() {
        registerNode()
        Thread.sleep(delayPeriod?.toLongMilliseconds()?.random ?: 0)
        fixedRateTimer(name = "move-timer", period = delayPeriod?.toLongMilliseconds() ?: 1000) {
            periodicFunction()
        }
    }

    fun system(numNodes: Int, configure: NodeBuilder.() -> Unit) {
        this.numNodes = numNodes
        this.configure = configure
    }

    fun algorithm(delayPeriod: Duration, function: Node.() -> Unit) {
        this.delayPeriod = delayPeriod
        periodicFunction = function
    }


    companion object {
        fun create(function: IterativeDiscreteSimulation.() -> Unit): IterativeDiscreteSimulation {
            val simulation = IterativeDiscreteSimulation()
            simulation.function()

            return simulation
        }
    }

}
