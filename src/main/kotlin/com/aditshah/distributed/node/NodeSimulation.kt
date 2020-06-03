package com.aditshah.distributed.node

//import kotlinx.coroutines.sync.Semaphore

import com.aditshah.distributed.common.Coordinate
import com.aditshah.distributed.common.CoordinateArea
import com.aditshah.distributed.server.CommunicationServer
import com.aditshah.distributed.visualization.Component
import com.aditshah.distributed.visualization.VisualizationServer
import java.net.InetAddress
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import kotlin.concurrent.thread

abstract class NodeSimulation {

    private val communicationServer = CommunicationServer()
    private lateinit var info: MapSharedInfo
    lateinit var visComponents: MutableList<Component>
    private val visualizationServer: VisualizationServer by lazy {
        if (::visComponents.isInitialized) {
            VisualizationServer(info, visComponents)
        } else {
            VisualizationServer(info)
        }
    }
    private val latch = CountDownLatch(1)
    var numNodes = 0
    lateinit var configure: NodeBuilder.() -> Unit
    val nodeBuilder: NodeBuilder by lazy {
        NodeBuilder().apply(configure)
    }

//    lateinit var node: Node
//        private set

//    var initialized: Boolean = false


    fun start() {
        thread { communicationServer.start() }
        val executor = Executors.newFixedThreadPool(numNodes)
        for (i in 1..numNodes) {
            executor.execute {
                val node = nodeBuilder.makeNode()
                node.start()
                if (latch.count == 1L) {
                    info = node.getInfo()
                    latch.countDown()
                }
            }
        }
        latch.await()
        thread { visualizationServer.start() }
//        this.node = node
//        initialized = true
    }


    fun getHostname(): String {
        return InetAddress.getLocalHost().hostName
    }

    abstract fun Node.start()
    open fun Node.stop() = Unit

    inner class NodeBuilder {
        lateinit var startingLocationGenerator: () -> Coordinate
        lateinit var coordinateArea: CoordinateArea
        lateinit var weightMap: WeightsMap
        var host: String = getHostname().replace(".lan", ".local")
        var port: Int = 50051

        fun makeNode(): Node {
            val startingLocation = startingLocationGenerator()
            return GrpcNode(
                startingLocation,
                coordinateArea,
                weightMap,
                host,
                port
            )
        }
    }
}
