package com.aditshah.distributed.infrastructure.simulation

import com.aditshah.distributed.infrastructure.common.random
import com.aditshah.distributed.infrastructure.node.Node
import com.aditshah.distributed.infrastructure.visualization.Component
import java.io.Serializable
import kotlin.concurrent.fixedRateTimer
import kotlin.time.Duration

/*
 * This is an implementation of the NodeSimulation abstract class. It implements the start function, which in this case
 *  periodically runs whatever function the user included in the algorithm. There could be many such classes which
 * describe different types of multi-agent system algorithms; an iterative periodic approach is only one of them.
 */
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

    fun visualization(addComponents: VisualizationReceiver.() -> Unit) {
        visComponents = VisualizationReceiver()
                .apply { addComponents() }.list
    }


    class VisualizationReceiver {
        val list = mutableListOf<Component>()
        fun addComponent(path: String, data: Serializable) {
            list.add(Component(path, data))
        }

    }

    fun algorithm(delayPeriod: Duration, function: Node.() -> Unit) {
        this.delayPeriod = delayPeriod
        periodicFunction = function
    }


    companion object {
        fun create(function: IterativeDiscreteSimulation.() -> Unit): IterativeDiscreteSimulation {
            val simulation =
                IterativeDiscreteSimulation()
            simulation.function()

            return simulation
        }
    }

}

