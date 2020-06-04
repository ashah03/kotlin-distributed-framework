package com.aditshah.old

import com.github.pambrose.common.coroutine.delay
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread
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
        thread {
            droneMap[this.id] = this
            val job = GlobalScope.launch {
                while (countdown.count == 1L) {
                    move()
                    delay(0.1.seconds)
                }
//            print("Done" + id)
            }
            countdown.await()
            job.cancel()
        }
    }


    override fun stop() {
        droneMap.remove(this.id)
        countdown.countDown()
    }

    override fun move() {
        with(info) {
            //            println(getWeightsMap().map)
//            locationLock.withLock {
            println("hello")
            val currentWeightsMap = ConcurrentHashMap(getWeightsMap().map)
            for (droneID in getIDs()) {
                println("droneID=$droneID")
                println("currentLocation=${droneMap[droneID]!!.location}")
                if (id != droneID) {
                    actionInRadius2D(coverageRadius, getLocation(droneID)) { coord ->
                        try {
                            putWeight(coord, 0.0)
                        } catch (e: NullPointerException) {
                            println(coord)
                        }
                    }
                }
//                }
            }
        }
    }

    private fun actionInRadius2D(
        radius: Double,
        center: Coordinate,
        block: (Coordinate) -> Unit
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
                        println("Setting to 0: $coord")
                        block(coord)
                    }
                }
            }
        }
    }

    companion object {
        val droneMap = ConcurrentHashMap<Int, GreedyDrone>()

        @JvmStatic
        fun main(args: Array<String>) {
            val info = MapSharedInfo(
                CoordinateArea(
                    Coordinate(
                        0.0,
                        0.0
                    ), Coordinate(100.0, 100.0)
                ),
                WeightsMap("csv/map10.csv")
            )
            for (i in 1..4) {
                val drone = GreedyDrone(
                    i,
                    info.coordinateArea.genRandomLocation(),
                    info,
                    2.237,
                    1.0
                )
                drone.start()
            }
            runBlocking { delay(5.5.seconds) }
            stopAll()
            println("hi")
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
            for ((id: Int, drone: GreedyDrone) in droneMap.entries) {
                drone.countdown.countDown()
                droneMap.remove(id)
            }
        }
    }
}