package com.aditshah.distributed.server

import com.aditshah.distributed.common.Coordinate
import com.aditshah.distributed.common.CoordinateArea
import com.aditshah.distributed.common.SharedInfo
import com.google.common.collect.Maps
import io.ktor.util.InternalAPI
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.atomic.AtomicInteger

class MapSharedInfo(
    override val coordinateArea: CoordinateArea,
    val weightMap: WeightsMap = WeightsMap()
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
        return weightMap[coord] ?: throw AssertionError("weight is null")
    }

    //fun toJson() = Json.stringify(serializer(), locationMap.toMap())
}

class NodeList {
    val set = HashSet<NodeRepresentation>();

    fun getNode(id: Int): NodeRepresentation {
        return set.first { it.id == id };
    }

    fun getNode(host: String, port: Int): NodeRepresentation {
        return set.first { it.host == host && it.port == port }
    }

    fun addNode(id: Int, hostname: String, port: Int) {
        addNode(NodeRepresentation(id, hostname, port))
    }

    fun addNode(node: NodeRepresentation) {
        set.add(node);
    }

}

data class NodeRepresentation(val id: Int, val host: String, val port: Int);

