package com.aditshah.distributed.infrastructure.server

import io.grpc.Server
import io.grpc.ServerBuilder
import java.io.IOException


class CommunicationServer() {

    var server: Server? = null

    @Throws(IOException::class)
    fun start() {
        server = ServerBuilder.forPort(port)
                .addService(CommunicationServiceImpl())
                .build()
                .start()
        println("Server started, listening on $port")
        Runtime.getRuntime()
                .addShutdownHook(
                    Thread {
                        // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                        System.err.println("*** shutting down gRPC server since JVM is shutting down")
                        server?.shutdown()
                        System.err.println("*** server shut down")
                    })
        server?.awaitTermination()
    }

    companion object {
        const val port = 50051

        @Throws(IOException::class, InterruptedException::class)
        @JvmStatic
        fun main(args: Array<String>) {
            CommunicationServer().start()
//                    .apply {
//                        start()
//                        server?.awaitTermination()
//                    }
        }
    }
}
