package com.aditshah.distributed.infrastructure.server

import com.aditshah.distributed.*
import com.aditshah.distributed.infrastructure.common.Coordinate
import com.aditshah.distributed.infrastructure.common.CoordinateArea
import com.aditshah.distributed.infrastructure.common.MapSharedInfo
import io.grpc.stub.StreamObserver
import java.util.concurrent.atomic.AtomicInteger

class NodeInfoServiceImpl() : NodeInfoServiceGrpc.NodeInfoServiceImplBase() {

    private val currentID = AtomicInteger()

    private val info = MapSharedInfo(
        CoordinateArea(
            Coordinate(0, 0),
            Coordinate(100, 100)
        )
    )

    private fun generateID() = currentID.getAndIncrement()

    override fun registerNode(request: CoordinateMessage, responseObserver: StreamObserver<NodeID>) {
        val id = generateID();

        info.nodeList.addNode(id)
        responseObserver.onNext(
            nodeID {
                this.value = id
            }
        )
        responseObserver.onCompleted()
    }


    override fun putLocation(request: CoordinateIDMessage, responseObserver: StreamObserver<Status>) {
        val coordinate = Coordinate(request.coordinate)
        info.putLocation(request.id.value, coordinate)
        println("Received location $coordinate for id ${request.id.value}")
        val response = status { this.message = "Coordinate received" }
        responseObserver.apply {
            onNext(response)
            onCompleted()
        }
    }

    override fun getLocation(request: NodeID, responseObserver: StreamObserver<CoordinateMessage>) {
        val coord = info.getLocation(request.value)
        val response =
            coordinateMessage {
                this.x = coord.X
                this.y = coord.Z
                this.z = coord.Z
            }
        responseObserver.apply {
            onNext(response)
            onCompleted()
        }
    }


}