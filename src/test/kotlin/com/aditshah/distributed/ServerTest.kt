package com.aditshah.distributed


import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.net.URL
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread


class ServerTest {
    @Test
    fun jsonServerTest() {
        countdown.await()
        val json = URL("http://0.0.0.0:8080/drones").readText()
        //println(json)
        val locs = Json.parse(LocationMapObj.serializer(), json)
        locs.map.size shouldEqual numDrones
        //println(locs)
        //println("{\"1\":{\"X\":27,\"Y\":65,\"Z\":0},\"2\":{\"X\":23,\"Y\":83,\"Z\":0},\"3\":{\"X\":92,\"Y\":75,\"Z\":0}}\")"
        //val regex = Regex("""\{("\d":\{"X":\d+,"Y":\d+,"Z":\d+},)*("\d":\{"X":\d+,"Y":\d+,"Z":\d+})+}""")
        //val regex = Regex("""\{("\d+":\{"X":\d+,"Y":\d+,"Z":\d+},){29}("\d+":\{"X":\d+,"Y":\d+,"Z":\d+})\}""")
        //val parsedMap : HashMap<String,String> = Json.parse(json)
        //working:
        //val regex = Regex("""\{("\d+":\{"X":\d+,"Y":\d+,"Z":\d+},){""" + (numDrones - 1) + """}("\d+":\{"X":\d+,"Y":\d+,"Z":\d+})\}""")
        //println(regex.matches("""{"1":{"X":38,"Y":10,"Z":0},"2":{"X":0,"Y":0,"Z":0},"3":{"X":0,"Y":0,"Z":0}}"""))
        //assert(regex.matches(json))
    }


    companion object {
        const val numDrones = 1000
        val countdown = CountDownLatch(numDrones)
        @JvmStatic
        @BeforeAll
        fun setUp() {
            val info = MapSharedInfo(CoordinateArea(Coordinate(0, 0), Coordinate(100, 100)))
            val server = Server(8080, info)
            server.run()
            for (i in 1..numDrones) {
                thread {
                    val drone = MyDrone1(i, Coordinate(0, 0), info)
//                    while (true) {
//                        sleep((i * 1).seconds)
                        drone.move()
                    info.getLocation(i) shouldEqual drone.location
//                        println(drone.location)
                    countdown.countDown()
//                    }
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