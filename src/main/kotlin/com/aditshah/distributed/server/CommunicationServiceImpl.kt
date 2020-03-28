package com.aditshah.distributed.server;

import com.aditshah.distributed.*
import com.aditshah.distributed.common.Coordinate
import com.google.protobuf.Empty
import io.grpc.stub.StreamObserver
import java.util.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.ArrayList

public class CommunicationServiceImpl : CommunicationServiceGrpc.CommunicationServiceImplBase() {

    private val currentID = AtomicInteger()
    private val nodeList = ArrayList<Int>();
    private val locationSubscribeQueues = HashMap<Int, BlockingQueue<Pair<Int, Coordinate>>>()
//    private val locationSubscribeQueues = HashMap<Int, HashMap<Int, Channel<Pair<Int, Coordinate>>>>()

    private fun generateID() = currentID.getAndIncrement()


    override fun registerNode(request: Empty, responseObserver: StreamObserver<NodeID>) {
        val id = generateID();
        println("Node $id registered")
        nodeList.add(id)
        locationSubscribeQueues[id] = LinkedBlockingQueue<Pair<Int, Coordinate>>()
        responseObserver.onNext(
            nodeID {
                this.value = id
            }
        )
        responseObserver.onCompleted()
    }

    override fun putLocation(responseObserver: StreamObserver<Empty>): StreamObserver<CoordinateIDMessage> {
        return object : StreamObserver<CoordinateIDMessage> {

            override fun onNext(value: CoordinateIDMessage) {
                val id = value.id.value
                val coordinate = Coordinate(value.coordinate)
                println("Location $coordinate for $id received")
                println("${value.coordinate.x} ${value.coordinate.y} ${value.coordinate.z}")
                locationSubscribeQueues
                        .filter { it.key != id }
                        .forEach {
                            it.value.put(Pair(id, coordinate))
                        }
            }

            override fun onError(t: Throwable) {
                println("Encountered error in sayHelloWithManyRequests()")
                t.printStackTrace()
            }

            override fun onCompleted() {
                responseObserver.apply { onNext(Empty.newBuilder().build()); onCompleted() }
            }
        }
    }

    override fun subscribeLocations(request: NodeID, responseObserver: StreamObserver<CoordinateIDMessage>) {
        val id = request.value;
        val locQueue = locationSubscribeQueues[id]!!
        while (nodeList.contains(id)) {
            val pair = locQueue.poll(1, TimeUnit.SECONDS)
            if (pair != null) {
                println("Sending location ${pair.second} of ${pair.first} to $id")
                responseObserver.onNext(
                    coordinateIDMessage {
                        this.id = nodeID {
                            this.value = pair.first
                        }
                        this.coordinate = coordinateMessage {
                            pair.second.also {
                                this.x = it.X;
                                this.y = it.Y;
                                this.z = it.Z;
                            }
                        }
                    }
                )
            }
        }
        responseObserver.onCompleted();
    }
}

data class CoordinateID(val id: Int, val coordinate: Coordinate)
