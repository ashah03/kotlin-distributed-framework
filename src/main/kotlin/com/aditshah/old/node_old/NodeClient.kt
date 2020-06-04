//package com.aditshah.distributed.infrastructure.node
//
//import com.aditshah.distributed.*
//import com.aditshah.distributed.infrastructure.common.Coordinate
//import com.aditshah.distributed.infrastructure.common.CoordinateArea
//import com.aditshah.distributed.infrastructure.common.WeightsMap
//import com.google.api.kgax.grpc.ClientStreamingCall
//import com.google.protobuf.Empty
//import io.grpc.ManagedChannelBuilder
//import kotlinx.coroutines.runBlocking
//import java.io.Closeable
//import kotlin.random.Random
//
//class NodeClient(private val node: Node) : Closeable {
//
//    private val client: CommunicationServiceClient;
//    val locCall: ClientStreamingCall<CoordinateIDMessage, Empty>;
//    val weightCall : ClientStreamingCall<WeightMessage, Empty>
////    private val client: NodeInfoServiceClient;
//
//    init {
//        node.apply {
//            println("Connecting to server $host:$port")
//        }
//        client = CommunicationServiceClient.create(
//            channel = ManagedChannelBuilder.forAddress(
//                node.host,
//                node.port
//            ).usePlaintext().build()
//        )
//        locCall = client.putLocation()
//        weightCall = client.putWeight()
//    }
//
//
////    @InternalAPI
////    override fun getIDs(): MutableSet<Int> {
////        //TODO
////    }
//
//    fun registerNode(): Int {
//        println("Registering node")
//        return runBlocking {
//            val coord = node.location;
//            println(coord)
//            val id = client.registerNode(
////                coordinateMessage {
////                    this.x = coord.X
////                    this.y = coord.Y
////                    this.z = coord.Z
////                }
//            )
//            println("Node registered. id = ${id.value}")
//            id.value;
//        }
//    }
//
////    fun putLocation() {
////        val coord = node.location;
////        println("Putting location $coord for id ${node.id}")
////        runBlocking {
////            val status = client.putLocation(
////                coordinateIDMessage {
////                    this.id = nodeID { this.value = node.id }
////                    this.coordinate = coordinateMessage {
////                        this.x = coord.X
////                        this.y = coord.Y
////                        this.z = coord.Z
////                    }
////                }
////            )
////            println(status)
////        }
////    }
//
//
//    fun putLocation() {
//        val coord = node.location;
//        println("Putting location $coord for id ${node.id}")
//        val call = this.locCall;
//        runBlocking {
//            call.requests.send(
//                coordinateIDMessage {
//                    this.id = nodeID { this.value = node.id };
//                    this.coordinate = coordinateMessage {
//                        this.x = coord.X;
//                        this.y = coord.Y;
//                        this.z = coord.Z
//                    }
//                }
//            )
//        }
//    }
//
//    fun subscribeLocation() {
//
//        val replies = client.subscribeLocations(nodeID { this.value = node.id })
//                .responses
//        runBlocking {
//            for (reply in replies) {
//                val id = reply.id.value
//                val coordinate = Coordinate(reply.coordinate)
//                println("Got location $coordinate from id $id")
//                node.info.putLocation(
//                    id, coordinate
//                )
//            }
//        }
//    }
//
////    fun getLocation(id: Int): Coordinate {
////        println("Getting location...")
////        assert(id != node.id)
////        return runBlocking {
////            val response = client.getLocation(
////                nodeID {
////                    this.value = id
////                }
////            )
////            val coord = Coordinate(response.x, response.y, response.z)
////            println("received $coord for id $id")
////            coord
////        }
////    }
//
//
//    fun putWeight(coord: Coordinate) {
//        val weight = node.info.getWeight(coord)
//        println("Putting weight $weight for coord $coord")
//        val call = this.weightCall
//        runBlocking {
//            call.requests.send(
//                weightMessage {
//                    this.coordinate = coordinateMessage {
//                        this.x = coord.X
//                        this.y = coord.Y
//                        this.z = coord.Z
//                    }
//                    this.value = weight
//                }
//
//            )
//        }
//    }
//
//    fun subscribeWeights(){
//        val replies = client.subscribeWeights(nodeID { this.value = node.id })
//                .responses
//        runBlocking {
//            for (reply in replies) {
//                val coordinate = Coordinate(reply.coordinate)
//                val weight = reply.value
//                println("Got location $coordinate from id $id")
//                node.info.putLocation(
//                    id, coordinate
//                )
//            }
//        }
//    }
//
////    override fun getWeight(coord: Coordinate): Double {
////        return weightMap[coord] ?: throw AssertionError("weight is null")
////    }
//
//    override fun close() {
//        client.shutdownChannel();
//    }
//
//    companion object {
//        @JvmStatic
//        fun main(args: Array<String>) {
//            val node = RandomDiscreteDrone(
//                Coordinate(Random.nextInt(0, 10), Random.nextInt(0, 10), Random.nextInt(0, 10)),
//                MapSharedInfo(
//                    CoordinateArea(Coordinate(0, 0, 0), Coordinate(10, 10, 10)),
//                    WeightsMap()
//                ),
//                "babbage.local",
//                50051
//            )
//
//            println("Starting drone")
//            node.start()
//        }
//    }
//
//
//    //fun toJson() = Json.stringify(serializer(), locationMap.toMap())
//
//}
//
//
//
//
