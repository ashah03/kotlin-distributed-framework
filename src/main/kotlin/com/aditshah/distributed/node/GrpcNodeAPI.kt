package com.aditshah.distributed.node;

import com.aditshah.distributed.*
import com.aditshah.distributed.common.Coordinate
import com.aditshah.distributed.common.CoordinateArea
import com.aditshah.distributed.node_old.Node
import com.google.api.kgax.grpc.ClientStreamingCall
import com.google.protobuf.Empty
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.runBlocking
import kotlinx.io.core.Closeable
import kotlin.concurrent.thread

class GrpcNodeAPI(
    private val startingLocation: Coordinate,
    private val coordinateArea: CoordinateArea,
    private val weightMap: WeightsMap,
    private val host: String,
    private val port: Int
) : NodeAPI {

    private var isRegistered: Boolean = false
    private val client = NodeClient()
    private val info = MapSharedInfo(coordinateArea, weightMap)

    //    private val logger = KotlinLogging.logger {}
    private var stopped: Boolean = false


    private var id = -1

    override fun registerNode(): Int {
        client.connect()
        id = client.registerNode()
        isRegistered = true;
        client.putLocation(startingLocation);
        thread {
            client.subscribeLocation();
        }
        thread {
            client.subscribeWeights();
        }

        return id;
    }

    override fun shutdownNode() {
        stopped = true;
        client.close();
    }

    override fun getID(): Int {
        if (id == -1) {
            if (!isRegistered) {
                val e = Node.NodeNotRegisteredException("Attempted to get ID, but node not registered")
//                logger.error(e) { "Attempted to get ID, but node not registered" }
                throw e
            } else {
                throw RuntimeException("node registered, but ID not assigned")
            }
        } else {
            return id
        }

    }

    private fun checkRegistered() {
        if (!isRegistered) throw Node.NodeNotRegisteredException("")
    }

    override fun putLocation(coord: Coordinate) {
        checkRegistered()
        require(info.coordinateArea.contains(coord)) { "Location $coord not in area ${info.coordinateArea}" }
        client.putLocation(coord)
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

    inner class NodeClient : Closeable {

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
