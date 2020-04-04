package com.aditshah.distributed.node

import com.aditshah.distributed.common.Coordinate
import com.aditshah.distributed.common.CoordinateArea
import kotlin.concurrent.thread
import kotlin.random.Random

abstract class IterativeDiscreteAlgorithm : NodeAlgorithm() {

    init {
        init(
            startingLocation = Coordinate(Random.nextInt(0, 10), Random.nextInt(0, 10), Random.nextInt(0, 10)),
            coordinateArea = CoordinateArea(Coordinate(0, 0, 0), Coordinate(10, 10, 10)),
            weightMap = WeightsMap("csv/map10.csv"),
            host = "babbage.local",
            port = 50051
        )
    }

    override fun start() {
        api.registerNode()
        thread {
            while (!api.isStopped()) {
                move()
            }
        }
    }

    abstract fun move()

    override fun stop() = api.shutdownNode()
}

