package com.aditshah.distributed.node

import com.aditshah.distributed.common.Coordinate
import com.aditshah.distributed.common.CoordinateArea
import kotlin.concurrent.fixedRateTimer
import kotlin.concurrent.thread
import kotlin.time.seconds

class IterativeDiscreteAlgorithmDSL : NodeAlgorithm() {

    lateinit var periodicFunction: NodeAPI.() -> Unit
    var delayPeriod = 0.5.seconds

    override fun start() {
        api.registerNode()
        thread {
//            while (!api.isStopped()) {
            //delay
//                api.periodicFunction()
//            }
            val timer = fixedRateTimer(name = "move-timer", period = delayPeriod.toLongMilliseconds()) {
                api.periodicFunction()
            }
        }
    }

    inner class APIBuilder {
        lateinit var startingLocationGenerator: () -> Coordinate
        lateinit var coordinateArea: CoordinateArea
        lateinit var weightMap: WeightsMap
        var host: String = getHostname().replace(".lan", ".local")
        var port: Int = 50051

        fun makeAPI(): NodeAPI {
            val startingLocation = startingLocationGenerator()
            return GrpcNodeAPI(
                startingLocation,
                coordinateArea,
                weightMap,
                host,
                port
            )
        }
    }

    fun config(function: APIBuilder.() -> Unit) {
        val apiBuilder = APIBuilder()
        apiBuilder.function()
        api = apiBuilder.makeAPI()
    }

    fun periodic(function: NodeAPI.() -> Unit) {
        periodicFunction = function
    }


    companion object {
        fun create(function: IterativeDiscreteAlgorithmDSL.() -> Unit): IterativeDiscreteAlgorithmDSL {
            val node = IterativeDiscreteAlgorithmDSL()
            node.function()

            return node
        }
    }

}

