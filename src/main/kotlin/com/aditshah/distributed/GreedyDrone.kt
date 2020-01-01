package com.aditshah.distributed

import com.github.pambrose.common.coroutine.delay
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread
import kotlin.concurrent.withLock
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.time.seconds

class GreedyDrone(
    id: Int,
    location: Coordinate,
    info: MapSharedInfo,
    coverageRadius: Double,
    moveRadius: Double
) : Drone(id, location, info, coverageRadius, moveRadius) {
    val countdown = CountDownLatch(1)

    override fun start() {
        droneArray.add(this)
        thread {
            while (countdown.count == 1L) {
                move()
                runBlocking {
                    delay(0.1.seconds)
                }
            }
//            print("Done" + id)
        }
    }


    override fun stop() {
        droneArray.remove(this)
        countdown.countDown()
    }

    override fun move() {
        with(info) {
            locationLock.withLock {
                for (droneID in getIDs()) {
                    if (id != droneID) {
                        actionInRadius2D(coverageRadius, getLocation(droneID), getWeightsMap()) { coord, weightsMap ->
                            weightsMap.map[coord] = 0.0
                        }
                    }
                }
            }
        }
    }

    private fun actionInRadius2D(
        radius: Double,
        center: Coordinate,
        weightsMap: WeightsMap,
        block: (Coordinate, WeightsMap) -> Unit
    ) {
        //Find the search square with sides = radius * 2
        val squareTopLeftX = ceil(center.X - radius)
        val squareTopLeftY = ceil(center.Y - radius)
        val squareBottomRightX = floor(center.X + radius)
        val squareBottomRightY = floor(center.Y + radius)

        with(info.coordinateArea) {
            for (x in max(topLeft.X, squareTopLeftX).toInt()..min(bottomRight.X, squareBottomRightX).toInt()) {
                for (y in max(topLeft.Y, squareTopLeftY).toInt()..min(bottomRight.Y, squareBottomRightY).toInt()) {
                    val coord = Coordinate(x.toDouble(), y.toDouble(), 0.0)
                    if (coord - center <= radius) {
                        block(coord, weightsMap)
                    }
                }
            }
        }
    }

    companion object {
        val droneArray = mutableListOf<GreedyDrone>()

        @JvmStatic
        fun main(args: Array<String>) {
            val info = MapSharedInfo(
                CoordinateArea(Coordinate(0.0, 0.0), Coordinate(100.0, 100.0)),
                WeightsMap("csv/map10.csv")
            )
            for (i in 1..6) {
                val drone = GreedyDrone(i, info.coordinateArea.genRandomLocation(), info, 2.237, 1.0)
                drone.start()
            }
//            GreedyDrone.startNum(6,info)
        }

//        fun startNum(num: Int, info: SharedInfo) {
//            for (i in 1..num) {
//                val drone = GreedyDrone(i, genRandomLocation(info.coordinateArea), info)
////                droneArray[i-1] = drone
//                drone.start()
//            }
//        }

        fun stopAll() {
            for (drone in droneArray) {
                drone.countdown.countDown()
            }
            droneArray.removeAll(droneArray)
        }
    }
}