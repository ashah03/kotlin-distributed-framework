package com.aditshah.distributed.node

import com.aditshah.distributed.common.Coordinate
import com.aditshah.distributed.common.CoordinateArea
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class RandomDiscreteDrone(
    startingLocation: Coordinate,
    coordinateArea: CoordinateArea,
    host: String,
    port: Int
) : Node(startingLocation, coordinateArea, host, port) {


    override fun move() {
        location = Coordinate(
            Random.nextInt(0, 10),
            Random.nextInt(0, 10),
            Random.nextInt(0, 10)
        )
    }

    override fun start() {
        val executor = Executors.newFixedThreadPool(5)
        executor.execute {
            while (!stopped) {
                move();
                Thread.sleep(500);
            }
        }
        executor.awaitTermination(1000, TimeUnit.SECONDS)
    }
}