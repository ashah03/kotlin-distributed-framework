package com.aditshah.distributed.node

interface NodeAlgorithm {
    val api: NodeAPI
    fun start()
    fun stop()
}
