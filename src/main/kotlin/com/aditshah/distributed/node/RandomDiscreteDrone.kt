package com.aditshah.distributed.node

import com.aditshah.distributed.common.Coordinate
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class RandomDiscreteDrone(
    startingLocation: Coordinate,
    info: MapSharedInfo,
    host: String,
    port: Int
) : Node(startingLocation, info, host, port) {


    override fun move() {
        location = Coordinate(
            Random.nextInt(0, 10),
            Random.nextInt(0, 10),
            Random.nextInt(0, 10)
        )
        println("Moving to $location")
    }

    override fun start() {
        val executor = Executors.newFixedThreadPool(5)
        executor.execute {
            while (!stopped) {
                move();
                Thread.sleep(500);
            }
        }
        executor.awaitTermination(1000, TimeUnit.DAYS)
    }
}