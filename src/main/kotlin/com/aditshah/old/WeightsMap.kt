package com.aditshah.old

import com.google.common.collect.Maps
import org.apache.commons.csv.CSVFormat
import java.io.FileReader
import java.util.concurrent.ConcurrentMap

fun main() {
    println(WeightsMap("csv/map10.csv").map)
}

class WeightsMap() {
    var map: ConcurrentMap<Coordinate, Double> = Maps.newConcurrentMap()

    operator fun set(coord: Coordinate, value: Double) {
        map[coord] = value
    }

    operator fun get(coord: Coordinate): Double? {
        return map[coord]
    }

    constructor(filename: String) : this() {
        val records = CSVFormat.DEFAULT.parse(FileReader(filename)).toList()
        for (i in 0 until records.size) {
            val row = records[i].iterator().asSequence().toList()
            for (j in 0 until row.size) {
                map[Coordinate(i.toDouble(), j.toDouble())] = row[j]!!.toDouble()
            }
        }
    }

    fun toCSV(filename: String) {
        //TODO
    }

}

