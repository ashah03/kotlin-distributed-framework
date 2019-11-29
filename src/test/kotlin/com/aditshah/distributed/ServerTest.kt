package com.aditshah.distributed


import com.github.pambrose.common.util.sleep
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.net.URL
import kotlin.concurrent.thread
import kotlin.time.seconds

class ServerTest {
    @Test
    fun jsonServerTest() {
//        val info = MapSharedInfo(CoordinateArea(Coordinate(0, 0), Coordinate(100, 100)))
//        val server = Server(8080, info)
//        server.run()
//        for (i in 1..3) {
//            thread {
//                val drone = MyDrone1(i, Coordinate(0, 0), info)
//                while (true) {
//                    sleep((i * 1).seconds)
//                    drone.move()
//                }
//            }
//        }
//
//        runBlocking {
//            val client = HttpClient()
//            val content = client.get<String>("localhost:8080/drones")
//            println(content)
//        }

        sleep(1.seconds)
        val json = URL("http://0.0.0.0:8080/drones").readText()
        println(json)
        //println("{\"1\":{\"X\":27,\"Y\":65,\"Z\":0},\"2\":{\"X\":23,\"Y\":83,\"Z\":0},\"3\":{\"X\":92,\"Y\":75,\"Z\":0}}\")"
        val regex = Regex("""\{["\d+":\{"X":\d+,"Y":\d+,"Z":0},\}]+\}""")
        //println(regex.matches("""{"1":{"X":38,"Y":10,"Z":0},"2":{"X":0,"Y":0,"Z":0},"3":{"X":0,"Y":0,"Z":0}}"""))
        assert(regex.matches(json))
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun setUp() {
            val info = MapSharedInfo(CoordinateArea(Coordinate(0, 0), Coordinate(100, 100)))
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
        }

        @JvmStatic
        @AfterAll
        fun takeDown() {
            runBlocking {
                val client = HttpClient()
                client.get<String>("http://localhost:8080/shutdown")
            }

        }
    }
}