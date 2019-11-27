package com.aditshah.distributed

import com.sudothought.common.util.sleep
import org.junit.Test
import kotlin.concurrent.thread
import kotlin.time.seconds

class ServerTest {
    @Test
    fun simpleDroneMovementTest() {
        val info = SharedInfo(CoordinateArea(Coordinate(0, 0), Coordinate(100, 100)))
        val server = Server(8080, info)
        server.run()
        for (i in 1..3) {
            thread {
                val drone = MyDrone1(i, Coordinate(0, 0), info)
                while (true) {
                    sleep((i * 1).seconds)
                    drone.move()
                }
            }
        }
        val client = HttpClient()
        val content = client.get<String>("localhost:8080/drones")
        println(content)
    }
}
