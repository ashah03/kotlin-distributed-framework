package com.aditshah.distributed.client

import com.aditshah.distributed.*
import com.aditshah.distributed.common.Coordinate
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.runBlocking
import java.io.Closeable

class GrpcClient(private val node: Node) : Closeable {


    private val client: NodeInfoServiceClient =
        NodeInfoServiceClient.create(
            channel = ManagedChannelBuilder.forAddress(
                node.host,
                node.port
            ).usePlaintext().build()
        );


//    @InternalAPI
//    override fun getIDs(): MutableSet<Int> {
//        //TODO
//    }

    fun registerNode(): Int {
        println("Registering node")
        return runBlocking {
            val coord = node.location;
            println(coord)
            val id = client.registerNode(
                registrationMessage {
                    this.hostname = node.host;
                    this.port = node.port;
                    coordinateMessage {
                        this.x = coord.X
                        this.y = coord.Y
                        this.z = coord.Z
                    }
                }
            )
            println("Node registered. id = ${id.value}")
            id.value;
        }
    }

    fun putLocation() {
        val coord = node.location;
        println("Putting location $coord for id ${node.id}")
        runBlocking {
            val status = client.putLocation(
                coordinateIDMessage {
                    this.id = nodeID { this.value = node.id }
                    this.coordinate = coordinateMessage {
                        this.x = coord.X
                        this.y = coord.Y
                        this.z = coord.Z
                    }
                }
            )
            println(status)
        }
    }

    fun getLocation(id: Int): Coordinate {
        println("Getting location...")
        assert(id != node.id)
        return runBlocking {
            val response = client.getLocation(
                nodeID {
                    this.value = id
                }
            )
            val coord = Coordinate(response.x, response.y, response.z)
            println("received $coord for id $id")
            coord
        }
    }

//    override fun putWeight(coord: Coordinate, weight: Double) {
//        runBlocking {
//            val status = client.putWeight(
//                coordinateMessage {
//                    this.x = coord.X
//                    this.y = coord.Y
//                    this.z = coord.Z
//                }
//            )
//            println(status)
//        }
//    }

//    override fun getWeight(coord: Coordinate): Double {
//        return weightMap[coord] ?: throw AssertionError("weight is null")
//    }

    override fun close() {
        client.shutdownChannel();
    }

    //fun toJson() = Json.stringify(serializer(), locationMap.toMap())

}




