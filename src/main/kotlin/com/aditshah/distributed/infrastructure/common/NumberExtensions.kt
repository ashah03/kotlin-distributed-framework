package com.aditshah.distributed.infrastructure.common

/*
 * This class provides useful number extension functions; currently it creates an easy syntax for generating random numbers
 * and sleeping regardless of the unit
 */
import kotlin.random.Random
import kotlin.time.Duration

val Int.random: Int
    get() = Random.nextInt(this)

val Long.random: Long
    get() = Random.nextLong(this)

val Double.random: Double
    get() = Random.nextDouble(this)

val IntRange.random: Int
    get() = Random.nextInt(this.first, this.last)

fun sleep(duration: Duration) = Thread.sleep(duration.toLongMilliseconds())