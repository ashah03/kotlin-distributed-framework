package com.aditshah.distributed;

import com.aditshah.distributed.node.IterativeDiscreteRandomAlgorithm
import com.aditshah.distributed.server.CommunicationServer
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import kotlin.concurrent.thread

class IterativeDiscreteRandomAlgorithmTest {

    @Test
    fun singleDroneUpTest() {
        val alg = IterativeDiscreteRandomAlgorithm()
        alg.start()
        Thread.sleep(5000)
        alg.stop()
    }


    companion object {
        val server = CommunicationServer()

        @JvmStatic
        @BeforeAll
        fun startServer() {

            thread {
                server.apply {
                    start()
                    server?.awaitTermination()
                }
            }
        }
    }
}


