package com.aditshah.distributed.infrastructure.node;

import com.aditshah.distributed.*
import com.aditshah.distributed.infrastructure.common.Coordinate
import com.aditshah.distributed.infrastructure.common.CoordinateArea
import com.aditshah.distributed.infrastructure.common.MapSharedInfo
import com.aditshah.distributed.infrastructure.common.WeightsMap
import com.google.api.kgax.grpc.ClientStreamingCall
import com.google.protobuf.Empty
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.runBlocking
import kotlinx.io.core.Closeable
import kotlin.concurrent.thread

/**
 * This class is an implementation of Node, which uses a gRPC communications backend to simulate the communication
 * between nodes. The goal is to have the communications happen in the background as seamlessly as possible
 *
 */
class GrpcNode(
    private val startingLocation: Coordinate,
    private val coordinateArea: CoordinateArea,
    private val weightMap: WeightsMap,
    private val host: String,
    private val port: Int
) : Node {

    private var isRegistered: Boolean = false
    private val client = NodeClient()
    private val info =
        MapSharedInfo(coordinateArea, weightMap)

    //    private val logger = KotlinLogging.logger {}
    private var stopped: Boolean = false


    private var id = -1

    /*
     * This function connects the node to the server, receives an ID, sends its stating location, and subscribes to future
     * updates to the Location and Weights, each in their own thread
     */
    override fun registerNode(): Int {
        client.connect()
        id = client.registerNode()
        client.setInitialWeights()
        isRegistered = true;
        putLocation(startingLocation);
        thread { client.subscribeLocation() }
        thread { client.subscribeWeights() }

        return id;
    }

    override fun shutdownNode() {
        stopped = true;
        client.close();
    }

    class NodeNotRegisteredException(message: String = "") : RuntimeException()

    /*
     * The logic for this appears convoluted, but it exists to account for the diferrent posibilities if the id has
     * not been assigned.
     */
    override fun getID(): Int {
        if (id == -1) {
            if (!isRegistered) {
                val e = NodeNotRegisteredException("Attempted to get ID, but node not registered")
//                logger.error(e) { "Attempted to get ID, but node not registered" }
                throw e
            } else {
                throw RuntimeException("node registered, but ID not assigned")
            }
        } else {
            return id
        }

    }

    override fun getAllIDs(): MutableSet<Int> {
        return info.locationMap.keys
    }

    override fun getCoordinateArea(): CoordinateArea {
        return coordinateArea
    }

    private fun checkRegistered() {
        if (!isRegistered) throw NodeNotRegisteredException("")
    }

    override fun putLocation(coord: Coordinate) {
        checkRegistered()
        require(info.coordinateArea.contains(coord)) { "Location $coord not in area ${info.coordinateArea}" }
        client.putLocation(coord)
        info.putLocation(id, coord)
//        logger.info { "Moving to $coord" }
    }

    override fun getLocation(id: Int): Coordinate {
        return info.getLocation(id)
    }

    override fun getWeight(coord: Coordinate): Double {
        return info.getWeight(coord)
    }

    override fun putWeight(coord: Coordinate, value: Double) {
        checkRegistered();
        info.putWeight(coord, value)
        client.putWeight(coord)

    }

    override fun isStopped(): Boolean {
        return stopped
    }

    override fun getInfo(): MapSharedInfo {
        return info
    }

    /**
     * This class has the gRPC calls to the Communication Server. They are in an inner class to avoid cluttering the main
     * communication logic
     */
    private inner class NodeClient : Closeable {

        private lateinit var client: CommunicationServiceClient;
        private lateinit var locCall: ClientStreamingCall<CoordinateIDMessage, Empty>;
        private lateinit var weightCall: ClientStreamingCall<WeightMessage, Empty>
//    private val client: NodeInfoServiceClient;

        private var isConnected: Boolean = false;

        fun connect() {
//            logger.info { "Connecting to server at $host:$port" }
            client = CommunicationServiceClient.create(
                channel = ManagedChannelBuilder
                        .forAddress(host, port)
                        .usePlaintext()
                        .build()
            )
//            logger.info { "Connected to server" }
            locCall = client.putLocation()
//            logger.debug { "Location stream established" }
            weightCall = client.putWeight()
//            logger.debug { "Weight stream established" }

            isConnected = true
        }


        fun registerNode(): Int {
//            logger.info { "Registering node" }
            return runBlocking {
                val id = client.registerNode()
//                logger.info { "Node registered. id = ${id.value}" }
                id.value;
            }
        }

        fun setInitialWeights() {
            runBlocking {
                client.setInitialWeights(
                    initialWeightsMessage {
                        val weightList = mutableListOf<WeightMessage>()
                        for ((coordinate, weight) in weightMap.map) {
                            weightList.add(weightMessage {
                                this.coordinate = coordinateMessage {
                                    coordinate.also {
                                        this.x = it.X
                                        this.y = it.Y
                                        this.z = it.Z
                                    }
                                }
                                this.value = weight
                            })
                        }
                        this.weights = weightList
                    }
                )
            }
        }

        fun putLocation(coord: Coordinate) {
            val ID = id;
//            logger.debug { "Sending location $coord for id $id to server" }
            val call = this.locCall;
            runBlocking {
                call.requests.send(
                    coordinateIDMessage {
                        this.id = nodeID { this.value = ID };
                        this.coordinate = coordinateMessage {
                            this.x = coord.X;
                            this.y = coord.Y;
                            this.z = coord.Z
                        }
                    }
                )
            }
        }

        fun subscribeLocation() {
            val replies = client.subscribeLocations(nodeID { this.value = id })
                    .responses
            runBlocking {
                for (reply in replies) {
                    val id = reply.id.value
                    val coordinate = Coordinate(reply.coordinate)
//                    logger.debug { "Received location $coordinate from id $id" }
                    info.putLocation(
                        id, coordinate
                    )
                }
            }
        }

        fun putWeight(coord: Coordinate) {
            val weight = info.getWeight(coord)
//            logger.debug { "Putting weight $weight for coord $coord" }
            val call = this.weightCall
            runBlocking {
                call.requests.send(
                    weightMessage {
                        this.coordinate = coordinateMessage {
                            this.x = coord.X
                            this.y = coord.Y
                            this.z = coord.Z
                        }
                        this.value = weight
                    }

                )
            }
        }

        fun subscribeWeights() {
            val replies = client.subscribeWeights(nodeID { this.value = id })
                    .responses
            runBlocking {
                for (reply in replies) {
                    val coordinate = Coordinate(reply.coordinate)
                    val weight = reply.value
//                    logger.debug { "Got weight $weight for coordinate $coordinate" }
                    info.putWeight(
                        coordinate, weight
                    )
                }
            }
        }


        override fun close() {
            client.shutdownChannel();
        }


        //fun toJson() = Json.stringify(serializer(), locationMap.toMap())

    }
}
