package com.aditshah.distributed.node_old

import com.aditshah.distributed.*
import com.aditshah.distributed.common.Coordinate
import com.aditshah.distributed.common.CoordinateArea
import com.aditshah.distributed.node.MapSharedInfo
import com.aditshah.distributed.node.WeightsMap
import com.google.api.kgax.grpc.ClientStreamingCall
import com.google.protobuf.Empty
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.runBlocking
import kotlinx.io.core.Closeable
import mu.KLogging
import kotlin.concurrent.thread
import kotlin.random.Random

abstract class Node(
    startingLocation: Coordinate,
    coordinateArea: CoordinateArea,
    weightMap: WeightsMap,
    val host: String,
    val port: Int
) {

    private val client = NodeClient()
    private val info = MapSharedInfo(coordinateArea, weightMap)
//    protected val logger = KotlinLogging.logger {}

    init {
        require(info.coordinateArea.contains(startingLocation)) { "Location $startingLocation not in area ${info.coordinateArea}" }
    }

    var location: Coordinate = startingLocation
        protected set(value) {
            checkRegistered()
            require(info.coordinateArea.contains(location)) { "Location $location not in area ${info.coordinateArea}" }
            client.putLocation()
            field = value
            logger.info { "Moving to $location" }
        }

    var id = -1
        private set
        get() {
            if (field == -1) {
                if (!isRegistered) {
                    val e =
                        NodeNotRegisteredException("Attempted to get ID, but node not registered")
                    logger.error(e) { "Attempted to get ID, but node not registered" }
                    throw e
                } else {
                    throw RuntimeException("node registered, but ID not assigned")
                }
            } else {
                return field
            }
        }


    var stopped: Boolean = false
        private set

    var isRegistered: Boolean = false
        private set

    class NodeNotRegisteredException(message: String) : RuntimeException()


    private fun checkRegistered() {
        if (!isRegistered) throw NodeNotRegisteredException("")
    }

//    private fun


    fun putWeight(coord: Coordinate, weight: Double) {
        checkRegistered();
        info.putWeight(coord, weight)
        client.putWeight(coord)
    }

    abstract fun move()
    protected open fun onStart() = Unit
    protected open fun onStop() = Unit

    fun start() {
        id = client.registerNode()
        isRegistered = true;
        client.putLocation();
        thread {
            client.subscribeLocation();
        }
        thread {
            client.subscribeWeights();
        }
        onStart()
    }

    fun stop() {
        stopped = true;
        client.close();
        onStop();
    }

    inner class NodeClient : Closeable {

        private val client: CommunicationServiceClient;
        val locCall: ClientStreamingCall<CoordinateIDMessage, Empty>;
        val weightCall: ClientStreamingCall<WeightMessage, Empty>
//    private val client: NodeInfoServiceClient;

        init {
            logger.info { "Connecting to server at $host:$port" }
            client = CommunicationServiceClient.create(
                channel = ManagedChannelBuilder.forAddress(
                    host,
                    port
                )
                        .usePlaintext()
                        .build()
            )
            logger.info { "Connected to server" }
            locCall = client.putLocation()
            logger.debug { "Location stream established" }
            weightCall = client.putWeight()
            logger.debug { "Weight stream established" }
        }

        fun registerNode(): Int {
            logger.info { "Registering node" }
            return runBlocking {
                val id = client.registerNode()
                logger.info { "Node registered. id = ${id.value}" }
                id.value;
            }
        }

        fun putLocation() {
            val coord = location;
            val ID = id;
            logger.debug { "Sending location $coord for id $id to server" }
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
                    logger.debug { "Received location $coordinate from id $id" }
                    info.putLocation(
                        id, coordinate
                    )
                }
            }
        }

        fun putWeight(coord: Coordinate) {
            val weight = info.getWeight(coord)
            logger.debug { "Putting weight $weight for coord $coord" }
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
                    logger.debug { "Got weight $weight for coordinate $coordinate" }
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

    companion object : KLogging() {
        @JvmStatic
        fun main(args: Array<String>) {
            val node = RandomDiscreteDrone(
                Coordinate(Random.nextInt(0, 10), Random.nextInt(0, 10), Random.nextInt(0, 10)),
                CoordinateArea(Coordinate(0, 0, 0), Coordinate(10, 10, 10)),
                WeightsMap("csv/map10.csv"),
                "babbage.local",
                50051
            )
            node.start()
        }

    }


}

