package com.aditshah.distributed;

import com.aditshah.distributed.node.IterativeRandomDroneAlgorithm
import com.aditshah.distributed.server.CommunicationServer
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import kotlin.concurrent.thread

class IterativeRandomDroneAlgorithmTest {

    @Test
    fun singleDroneUpTest() {
        val alg = IterativeRandomDroneAlgorithm()
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


