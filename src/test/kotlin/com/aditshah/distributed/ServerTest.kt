package com.aditshah.distributed


import com.aditshah.distributed_old.*
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotEqual
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.net.URL


class ServerTest {
    @Test
    fun jsonServerTest() {
        var json = URL("http://0.0.0.0:8080/drones").readText()
        val last = Json.parse(LocationMapObj.serializer(), json)
        json = URL("http://0.0.0.0:8080/drones").readText()
        val locs = Json.parse(LocationMapObj.serializer(), json)
        locs.map.size shouldEqual numDrones
        last shouldNotEqual locs
        MyDrone1.stopAll()
    }

        //println(locs)
        //println("{\"1\":{\"X\":27,\"Y\":65,\"Z\":0},\"2\":{\"X\":23,\"Y\":83,\"Z\":0},\"3\":{\"X\":92,\"Y\":75,\"Z\":0}}\")"
        //val regex = Regex("""\{("\d":\{"X":\d+,"Y":\d+,"Z":\d+},)*("\d":\{"X":\d+,"Y":\d+,"Z":\d+})+}""")
        //val regex = Regex("""\{("\d+":\{"X":\d+,"Y":\d+,"Z":\d+},){29}("\d+":\{"X":\d+,"Y":\d+,"Z":\d+})\}""")
        //val parsedMap : HashMap<String,String> = Json.parse(json)
        //working:
        //val regex = Regex("""\{("\d+":\{"X":\d+,"Y":\d+,"Z":\d+},){""" + (numDrones - 1) + """}("\d+":\{"X":\d+,"Y":\d+,"Z":\d+})\}""")
        //println(regex.matches("""{"1":{"X":38,"Y":10,"Z":0},"2":{"X":0,"Y":0,"Z":0},"3":{"X":0,"Y":0,"Z":0}}"""))
        //assert(regex.matches(json))



    companion object {
        const val numDrones = 50
        @JvmStatic
        @BeforeAll
        fun setUp() {
            val info =
                MapSharedInfo(CoordinateArea(Coordinate(0, 0), Coordinate(100, 100)), WeightsMap("csv/map10.csv"))
            val server = Server(8080, info)
            server.start()
//            val droneArray = AtomicReferenceArray<MyDrone1>(3)
            for (i in 1..numDrones) {
                val drone = MyDrone1(i, Coordinate(0, 0), info)
//                droneArray[i-1] = drone
                drone.start()
            }
        }

        @JvmStatic
        @AfterAll
        fun takeDown() {
            runBlocking {
                val client = HttpClient()
                client.get<String>("http://localhost:8080/shutdown")
            }

//            for (i in 1..numDrones) {
//                droneArray[i-1].stop()
//            }
        }
    }
}