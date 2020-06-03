package com.aditshah.distributed;

import com.aditshah.distributed.node_old.IterativeDiscreteRandomSimulationOld
import com.aditshah.distributed.server.CommunicationServer
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import kotlin.concurrent.thread

class IterativeDiscreteRandomAlgorithmTest {

    @Test
    fun singleDroneUpTest() {
        val alg = IterativeDiscreteRandomSimulationOld()
        alg.start()
        Thread.sleep(1000)
        alg.stop()
    }


    companion object {
        val server = CommunicationServer()

        @JvmStatic
        @BeforeAll
        fun startServer() {
            thread {
                server.start()
            }
        }
    }
}


