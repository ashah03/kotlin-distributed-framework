package com.aditshah.distributed;

import com.aditshah.distributed.infrastructure.server.CommunicationServer
import com.aditshah.old.node_old.IterativeDiscreteRandomSimulationOld
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


