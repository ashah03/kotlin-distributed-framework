package com.aditshah.distributed.server;

import com.aditshah.distributed.*
import com.aditshah.distributed.common.Coordinate
import com.aditshah.distributed.node.WeightsMap
import com.google.protobuf.Empty
import io.grpc.stub.StreamObserver
import java.util.concurrent.BlockingQueue
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

public class CommunicationServiceImpl : CommunicationServiceGrpc.CommunicationServiceImplBase() {

    private val currentID = AtomicInteger()
    private val nodeList = ArrayList<Int>();
//    private val locationSubscribeQueues = HashMap<Int, BlockingQueue<Pair<Int, Coordinate>>>()

    private val updateIDQueues = ConcurrentHashMap<Int, BlockingQueue<Int>>()
    private val currentLocations = ConcurrentHashMap<Int, Coordinate>()

    private val updateCoordinateQueues = ConcurrentHashMap<Int, BlockingQueue<Coordinate>>()
    private val currentWeights = WeightsMap("csv/map10.csv")

    private fun generateID() = currentID.incrementAndGet()

    override fun healthCheck(request: Empty, responseObserver: StreamObserver<Status>) {
        responseObserver.onNext(status { this.message = "Health check received" })
        responseObserver.onCompleted()
    }

    override fun registerNode(request: Empty, responseObserver: StreamObserver<NodeID>) {
        val id = generateID();
        println("Node $id registered")
        nodeList.add(id)
        updateIDQueues[id] = LinkedBlockingQueue<Int>()
        updateCoordinateQueues[id] = LinkedBlockingQueue<Coordinate>()

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
                currentLocations[id] = coordinate
                updateIDQueues
                        .filter { it.key != id }
                        .filter { !it.value.contains(id) }
                        .forEach {
                            it.value.put(id)
                        }
                println("Queue 0 size = " + updateIDQueues[1]?.size)
            }

            override fun onError(t: Throwable) {
                println("Error in putLocation()")
                t.printStackTrace()
            }

            override fun onCompleted() {
                responseObserver.apply {
                    onNext(
                        Empty.newBuilder()
                                .build()
                    ); onCompleted()
                }
            }
        }
    }

    override fun subscribeLocations(request: NodeID, responseObserver: StreamObserver<CoordinateIDMessage>) {
        val subscriberID = request.value;
        val idQueue = updateIDQueues[subscriberID]
                      ?: throw AssertionError("ID Queue for $subscriberID not in updateIDQueues, is drone registered?")
        while (nodeList.contains(subscriberID)) {
            val id = idQueue.poll(1, TimeUnit.SECONDS) ?: continue
            val coordinate = currentLocations[id] ?: throw AssertionError("No location for drone")
            println("Sending location $coordinate of $id to $subscriberID")
            responseObserver.onNext(
                coordinateIDMessage {
                    this.id = nodeID {
                        this.value = id
                    }
                    this.coordinate = coordinateMessage {
                        coordinate.also {
                            this.x = it.X;
                            this.y = it.Y;
                            this.z = it.Z;
                        }
                    }
                }
            )
        }
        responseObserver.onCompleted();
    }

    override fun putWeight(responseObserver: StreamObserver<Empty>): StreamObserver<WeightMessage> {
        return object : StreamObserver<WeightMessage> {

            override fun onNext(message: WeightMessage) {
                val coordinate = Coordinate(message.coordinate);
                val value = message.value
                println("Received weight $value for coordinate $coordinate")
                currentWeights[coordinate] = value
                updateCoordinateQueues
                        .forEach {
                            it.value.put(coordinate)
                        }

            }

            override fun onError(t: Throwable) {
                println("Error in putWeight()")
                t.printStackTrace()

            }

            override fun onCompleted() {
                responseObserver.apply {
                    onNext(
                        Empty.newBuilder()
                                .build()
                    ); onCompleted()
                }
            }
        }
    }

    override fun subscribeWeights(request: NodeID, responseObserver: StreamObserver<WeightMessage>) {
        val subscriberID = request.value;
        val coordinateQueue = updateCoordinateQueues[subscriberID]
                              ?: throw AssertionError("ID Queue for $subscriberID not in updateIDQueues, is drone registered?")
        while (nodeList.contains(subscriberID)) {
            val coordinate = coordinateQueue.poll(1, TimeUnit.SECONDS) ?: continue
            val weight = currentWeights[coordinate] ?: throw AssertionError("No location for drone")
            println("Sending weight $weight of $coordinate to $subscriberID")
            responseObserver.onNext(
                weightMessage {
                    this.coordinate = coordinateMessage {
                        coordinate.also {
                            this.x = it.X;
                            this.y = it.Y;
                            this.z = it.Z;
                        }
                    }
                    this.value = weight
                }
            )
        }
        responseObserver.onCompleted();
    }
}

data class CoordinateID(val id: Int, val coordinate: Coordinate)
