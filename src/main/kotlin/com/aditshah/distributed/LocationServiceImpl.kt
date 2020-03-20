package com.aditshah.distributed

import io.grpc.stub.StreamObserver

class LocationServiceImpl() : LocationServiceGrpc.LocationServiceImplBase() {

    override fun getLocation(request: CoordinateRequest, responseObserver: StreamObserver<CoordinateMessage>) {
        val coord = Coordinate(1, 2);
        val response =
            CoordinateMessage
                    .newBuilder()
                    .run() {
                        this.id = request.id
                        this.x = coord.X
                        this.y = coord.Z
                        this.z = coord.Z
                        build()
                    }
        responseObserver.apply {
            onNext(response)
            onCompleted()
        }
    }


    override fun setLocation(request: CoordinateMessage, responseObserver: StreamObserver<Status>) {
        val coordinate = Coordinate(request.x, request.y, request.z)
        val response =
            Status.newBuilder()
                    .run {
                        this.message = "Coordinate received"
                        build()
                    }
        responseObserver.apply {
            onNext(response)
            onCompleted()
        }
    }
}