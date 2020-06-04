package com.aditshah.old

import com.github.pambrose.common.coroutine.delay
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread
import kotlin.random.Random
import kotlin.time.seconds

class MyDrone1(id: Int, location: Coordinate, info: SharedInfo) : Drone(id, location, info) {
    val countdown = CountDownLatch(1)

    override fun start() {
        droneArray.add(this)
        thread {
            while (countdown.count == 1L) {
                move()
                runBlocking {
                    delay(Random.nextDouble(1.0).seconds)
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
//        do {
        location = info.coordinateArea.genRandomLocation()
        println("moving to $location")
//        } while (info.locationMap.containsValue(newLoc_
    }

    companion object {
        val droneArray = mutableListOf<MyDrone1>()

        @JvmStatic
        fun main(args: Array<String>) {
            val area = CoordinateArea(
                Coordinate(0, 0),
                Coordinate(100, 100)
            )
            val info = MapSharedInfo(
                area,
                WeightsMap("csv/map10.csv")
            )
            val c1 = Coordinate(5, 7)
            val drone = MyDrone1(82, c1, info)
            println(drone.location)
            println(info.getLocation(drone.id))
            drone.move()
            println(drone.location)
            println(info.getLocation(drone.id))
        }

        fun stopAll() {
            for (drone in droneArray) {
                drone.countdown.countDown()
            }
            droneArray.removeAll(droneArray)
        }
    }
}