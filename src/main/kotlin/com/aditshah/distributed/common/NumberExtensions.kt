package com.aditshah.distributed.common

import kotlin.random.Random

fun Int.random() = Random.nextInt(this)

fun Double.random() = Random.nextDouble(this)

fun IntRange.random() = Random.nextInt(this.first, this.last)