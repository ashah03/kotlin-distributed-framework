//package com.aditshah.distributed.node_old
//
//import com.aditshah.distributed.common.Coordinate
//import com.aditshah.distributed.common.CoordinateArea
//import com.aditshah.distributed.node.GrpcNode
//import com.aditshah.distributed.node.Node
//import com.aditshah.distributed.node.NodeSimulation
//import com.aditshah.distributed.node.WeightsMap
//import kotlin.concurrent.thread
//import kotlin.random.Random
//
//abstract class IterativeDiscreteSimulationOld : NodeSimulation() {
//
//    init {
//        initialize(
//            GrpcNode(
//            Coordinate(Random.nextInt(0, 10), Random.nextInt(0, 10), Random.nextInt(0, 10)),
//            CoordinateArea(Coordinate(0, 0, 0), Coordinate(10, 10, 10)),
//            WeightsMap("csv/map10.csv"),
//            "babbage.local",
//            50051
//        )
//        )
//    }
//
//    override fun start() {
//        node.registerNode()
//        thread {
//            while (!node.isStopped()) {
//                move()
//            }
//        }
//    }
//
//    abstract fun move()
//
//    override fun stop() = node.shutdownNode()
//}
//
