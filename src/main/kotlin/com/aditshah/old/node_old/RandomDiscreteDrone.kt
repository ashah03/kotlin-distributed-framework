package com.aditshah.old.node_old

import com.aditshah.distributed.infrastructure.common.Coordinate
import com.aditshah.distributed.infrastructure.common.CoordinateArea
import com.aditshah.distributed.infrastructure.common.WeightsMap
import kotlin.concurrent.thread
import kotlin.random.Random

class RandomDiscreteDrone(
    startingLocation: Coordinate,
    coordinateArea: CoordinateArea,
    weightMap: WeightsMap = WeightsMap(),
    host: String,
    port: Int
) : OldNode(startingLocation, coordinateArea, weightMap, host, port) {


    override fun move() {
        location = Coordinate(
            Random.nextInt(0, 10),
            Random.nextInt(0, 10),
            Random.nextInt(0, 10)
        )
        val weight = Random.nextDouble(1.0, 11.0)
        putWeight(location, weight)
        println("Moving to $location")
        println("Changing weight of $location to $weight")
    }

    override fun onStart() {
        thread {
            while (!stopped) {
                move();
                Thread.sleep(500);
            }
        }
//        println("Should have stopped")
//        executor.awaitTermination(1000, TimeUnit.DAYS)
    }
}