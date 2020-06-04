//package com.aditshah.old.node_old
//
//import com.aditshah.distributed.infrastructure.common.Coordinate
//import com.aditshah.distributed.infrastructure.common.CoordinateArea
//import com.aditshah.distributed.infrastructure.node.GrpcNode
//import com.aditshah.distributed.infrastructure.node.Node
//import com.aditshah.distributed.infrastructure.simulation.NodeSimulation
//import com.aditshah.distributed.infrastructure.common.WeightsMap
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
