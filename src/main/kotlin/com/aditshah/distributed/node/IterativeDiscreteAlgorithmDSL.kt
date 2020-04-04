package com.aditshah.distributed.node

import kotlin.concurrent.thread
import kotlin.time.seconds

class IterativeDiscreteAlgorithmDSL : NodeAlgorithm() {

    lateinit var moveFunction: NodeAPI.() -> Unit
    var delayPeriod = 0.5.seconds

    override fun start() {
        api.registerNode()
        thread {
            while (!api.isStopped()) {
                api.moveFunction()
                Thread.sleep(delayPeriod.inMilliseconds.toLong())
            }
        }
    }

    fun move(function: NodeAPI.() -> Unit) {
        moveFunction = function
    }


    companion object {
        fun create(function: IterativeDiscreteAlgorithmDSL.() -> Unit): IterativeDiscreteAlgorithmDSL {
            val node = IterativeDiscreteAlgorithmDSL()
            node.function()

            return node
        }
    }

}