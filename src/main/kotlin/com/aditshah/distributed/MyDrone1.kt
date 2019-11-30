package com.aditshah.distributed

import com.github.pambrose.common.coroutine.delay
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread
import kotlin.random.Random
import kotlin.time.seconds

class MyDrone1(id: Int, location: Coordinate, info: SharedInfo) : Drone(id, location, info) {
    val countdown = CountDownLatch(1)
    override fun start() {
        thread {
            val job = GlobalScope.launch {
                while (true) {
                    move()
                    delay(Random.nextDouble(1.0).seconds)
                }
            }
            while (countdown.count == 1L) {
                if (masterCountDown.count == 0L) {
                    countdown.countDown()
                }
            }
            job.cancel()
//            print("Done" + id)
        }
    }


    override fun stop() {
        countdown.countDown()
    }

    override fun move() {
        var newLoc: Coordinate
//        do {
        newLoc = genRandomLocation(info.coordinateArea)
//        } while (info.locationMap.containsValue(newLoc))
        location = newLoc
    }

    companion object {
        val masterCountDown = CountDownLatch(1)
        @JvmStatic
        fun main(args: Array<String>) {
            val area = CoordinateArea(Coordinate(0, 0), Coordinate(100, 100))
            val info = MapSharedInfo(coordinateArea = area)
            val c1 = Coordinate(5, 7)
            val drone = MyDrone1(82, c1, info)
            println(drone.location)
            println(info.getLocation(drone.id))
            drone.move()
            println(drone.location)
            println(info.getLocation(drone.id))
        }

        fun genRandomLocation(area: CoordinateArea): Coordinate {
            return with(area) {
                val x = if (start.X == end.X) start.X else Random.nextInt(start.X, end.X)
                val y = if (start.Y == end.Y) start.Y else Random.nextInt(start.Y, end.Y)
                val z = if (start.Z == end.Z) start.Z else Random.nextInt(start.Z, end.Z)
                Coordinate(x, y, z)
            }
        }

        fun endAll() {
            masterCountDown.countDown()
        }
    }
}