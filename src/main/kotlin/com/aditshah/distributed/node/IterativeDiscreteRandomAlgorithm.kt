package com.aditshah.distributed.node

import com.aditshah.distributed.common.Coordinate
import kotlin.random.Random

class IterativeDiscreteRandomAlgorithm : IterativeDiscreteAlgorithm() {

    override fun move() {
        val location = Coordinate(
            Random.nextInt(10),
            Random.nextInt(10),
            Random.nextInt(10)
        )
        val weight = Random.nextDouble(1.0, 11.0)
        api.putLocation(location)
        api.putWeight(location, weight)
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val alg = IterativeDiscreteRandomAlgorithm()
            alg.start()
        }

    }
}
