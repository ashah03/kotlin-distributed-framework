//package com.aditshah.distributed.node_old
//
//import com.aditshah.distributed.common.Coordinate
//import kotlin.random.Random
//
//class IterativeDiscreteRandomSimulationOld : IterativeDiscreteSimulationOld() {
//
//    override fun move() {
//        val location = Coordinate(
//            Random.nextInt(10),
//            Random.nextInt(10),
//            Random.nextInt(10)
//        )
//        val weight = Random.nextDouble(1.0, 11.0)
//        node.putLocation(location)
//        node.putWeight(location, weight)
//    }
//
//    companion object {
//
//        @JvmStatic
//        fun main(args: Array<String>) {
//            val alg = IterativeDiscreteRandomSimulationOld()
//            alg.start()
//        }
//
//    }
//}
