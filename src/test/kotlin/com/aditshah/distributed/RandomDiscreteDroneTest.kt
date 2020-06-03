package com.aditshah.distributed;

import com.aditshah.distributed.common.Coordinate
import com.aditshah.distributed.common.CoordinateArea
import com.aditshah.distributed.node.WeightsMap
import com.aditshah.distributed.node_old.OldNode
import com.aditshah.distributed.node_old.RandomDiscreteDrone
import com.aditshah.distributed.server.CommunicationServer
import org.amshove.kluent.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import kotlin.concurrent.thread
import kotlin.random.Random

class RandomDiscreteDroneTest {

    @Test
    fun singleDroneUpTest() {
        val node = RandomDiscreteDrone(
            Coordinate(Random.nextInt(0, 10), Random.nextInt(0, 10), Random.nextInt(0, 10)),
            CoordinateArea(Coordinate(0, 0, 0), Coordinate(10, 10, 10)),
            WeightsMap("csv/map10.csv"),
            "babbage.local",
            50051
        )
        node.stopped shouldEqual false
        invoking { node.start() } `should not throw` AnyException
        node.stopped shouldEqual false
        node.id shouldNotEqual 0
        node.stop()
        node.stopped shouldEqual true
        println("Got here")

    }

    @Test
    fun singleDroneMoveTest() {
        val node = RandomDiscreteDrone(
            Coordinate(Random.nextInt(0, 10), Random.nextInt(0, 10), Random.nextInt(0, 10)),
            CoordinateArea(Coordinate(0, 0, 0), Coordinate(10, 10, 10)),
            WeightsMap("csv/map10.csv"),
            "babbage.local",
            50051
        )
        node.stopped shouldEqual false
        node.start()
        node.stopped shouldEqual false
        node.id shouldNotEqual 0
        Thread.sleep(10000)
        node.stop()
        node.stopped shouldEqual true
        println("Got here")

    }


    @Test
    fun multiDroneTestSmall() {
        val nodeList = mutableListOf<OldNode>()
        repeat(5) {
            nodeList.add(
                it, RandomDiscreteDrone(
                    Coordinate(Random.nextInt(0, 10), Random.nextInt(0, 10), Random.nextInt(0, 10)),
                    CoordinateArea(Coordinate(0, 0, 0), Coordinate(10, 10, 10)),
                    WeightsMap("csv/map10.csv"),
                    "babbage.local",
                    50051
                )
            )
            nodeList[it].start()
            if (it != 0) nodeList[it].id shouldNotEqual nodeList[it - 1].id
        }

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


