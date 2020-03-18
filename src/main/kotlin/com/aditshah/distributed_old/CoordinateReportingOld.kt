package com.aditshah.distributed_old

//@file:Suppress("UndocumentedPublicClass", "UndocumentedPublicFunction")
//
//package com.aditshah.distributed
//
//import com.github.pambrose.common.concurrent.*
//import io.etcd.jetcd.watch.WatchResponse
//import io.etcd.recipes.common.*
//import java.util.concurrent.CountDownLatch
//import java.util.concurrent.Executors
//import kotlin.random.Random
//import kotlin.time.seconds
//import kotlin.time.ExperimentalTime
//
//
//@ExperimentalTime
//fun main() {
//    val urls = listOf("http://localhost:2379")
//    val numDrones = 4;
//    val pathTemplate = "/drones/droneX/location"
//    val executor = Executors.newCachedThreadPool()
//    val numIterations = 3
//    val countdown = CountDownLatch(numDrones * numIterations)
//
//    for (droneID in 1..numDrones) {
//        val path = pathTemplate.replace("X", droneID.toString())
//        println(path)
//        executor.execute {
//            connectToEtcd(urls) { client ->
//                client.withWatchClient { watchClient ->
//                    watchClient.watcher(path) { watchResponse: WatchResponse ->
//                        watchResponse.events
//                            .forEach { watchEvent ->
//                                println("Watch event: ${watchEvent.eventType} ${watchEvent.keyValue.asString}")
//                            }
//                    }.use {
//                        countdown.await()
//                    }
//                    println("Closed watch")
//                }
//            }
//        }
//    }
//
//    countdown.countDown {
//        for (iter in 1..numIterations) {
//            for (droneID in 1..numDrones) {
//                executor.execute {
//                    val path = pathTemplate.replace("X", droneID.toString())
//                    val coord = Coordinate(
//                        Random.nextInt(1, 100),
//                        Random.nextInt(1, 100),
//                        Random.nextInt(1, 100)
//                    );
//                    connectToEtcd(urls) { client ->
//                        client.withKvClient { kvClient ->
//                            var json = coord.toJson()
//                            println("Assigning $path = $json")
//                            kvClient.putValue(path, json)
//                        }
//                    }
//                }
//            }
//            sleep(1.seconds)
//        }
//    }
//
//    countdown.await();
//}
