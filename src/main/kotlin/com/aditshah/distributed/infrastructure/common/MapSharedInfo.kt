package com.aditshah.distributed.infrastructure.common

import com.google.common.collect.Maps
import io.ktor.util.InternalAPI
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.atomic.AtomicInteger

@Serializable
data class LocationMapObj(val map: Map<Int, Coordinate>) {
    fun toJson(): String = Json.stringify(serializer(), this)
}

@Serializable
data class WeightMapObj(val map: Map<Coordinate, Double>) {
    fun toJson(): String = Json(JsonConfiguration(allowStructuredMapKeys = true)).stringify(serializer(), this)
}

/*
 * This class is a specific implementation of SharedInfo that uses maps to store all of the data
 */
class MapSharedInfo(
    override val coordinateArea: CoordinateArea,
    val weightMap: WeightsMap = WeightsMap(
        "csv/map10.csv"
    )
) : SharedInfo {

    val locationMap: ConcurrentMap<Int, Coordinate> = Maps.newConcurrentMap()
    val nodeList = NodeList();
    val counter = AtomicInteger();


    fun getWeightsMap(): WeightsMap {
        return weightMap
    }

    @InternalAPI
    override fun getIDs(): MutableSet<Int> {
        return locationMap.keys
    }

    override fun putLocation(id: Int, coord: Coordinate) {
        locationMap[id] = coord
    }

    @InternalAPI
    override fun getLocation(id: Int): Coordinate {
        return locationMap[id] ?: throw AssertionError("location is null")
    }

    override fun putWeight(coord: Coordinate, weight: Double) {
        weightMap[coord] = weight
    }

    override fun getWeight(coord: Coordinate): Double {
        return weightMap[coord] ?: throw AssertionError("weight at $coord is null")
    }

    //fun toJson() = Json.stringify(serializer(), locationMap.toMap())
}

class NodeList {
    val set = HashSet<Int>();

    fun contains(id: Int): Boolean {
        return set.contains(id)
    }

    fun addNode(id: Int) {
        set.add(id);
    }

}

//class NodeList {
//    val set = HashSet<NodeRepresentation>();
//
//    fun getNode(id: Int): NodeRepresentation {
//        return set.first { it.id == id };
//    }
//
//    fun getNode(host: String, port: Int): NodeRepresentation {
//        return set.first { it.host == host && it.port == port }
//    }
//
//    fun addNode(id: Int, hostname: String, port: Int) {
//        addNode(NodeRepresentation(id, hostname, port))
//    }
//
//    fun addNode(node: NodeRepresentation) {
//        set.add(node);
//    }
//
//}
//
//data class NodeRepresentation(val id: Int, val host: String, val port: Int);

