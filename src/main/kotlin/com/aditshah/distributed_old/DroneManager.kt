package com.aditshah.distributed_old

import kotlin.concurrent.thread
import kotlin.math.roundToLong
import kotlin.random.Random

fun main() {
    val area = CoordinateArea(Coordinate(0, 0), Coordinate(100, 100))
    val info = MapSharedInfo(area, WeightsMap("csv/map10.csv"))
    val arr: ArrayList<Drone> = ArrayList()
    for (i in 1..200) {
        thread {
            repeat(5) {
                arr.add(MyDrone1(i, area.genRandomLocation(), info))
                Thread.sleep((1000 * Random.nextDouble()).roundToLong())
            }
        }
    }
}