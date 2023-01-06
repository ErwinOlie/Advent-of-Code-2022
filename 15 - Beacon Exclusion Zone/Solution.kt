package nl.erwinolie.`Advent-of-Code-2022`.`15 - Beacon Exclusion Zone`

import nl.erwinolie.extensions.Point2D
import java.math.BigInteger
import nl.erwinolie.extensions.input

var minX = 0L
var maxX = 0L
var maxDistance = 0L
val input = input()
    .lines()
    .map {
        val (sensorX, sensorY, beaconX, beaconY) =
            "^Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)\$"
                .toRegex().find(it)!!.destructured
        SensorBeaconPair(Point2D(sensorX.toLong(), sensorY.toLong()), Point2D(beaconX.toLong(), beaconY.toLong()))
    }

fun init() {
    input.forEach {
        if (it.sensor.x < minX) {
            minX = it.sensor.x
        }
        if (it.beacon.x < minX) {
            minX = it.beacon.x
        }
        if (it.sensor.x > maxX) {
            maxX = it.sensor.x
        }
        if (it.beacon.x > maxX) {
            maxX = it.beacon.x
        }
        if (it.distance() > maxDistance) {
            maxDistance = it.distance()
        }
    }
    minX -= maxDistance
    maxX += maxDistance
}

fun main() {
    init()
    var answer1 = 0
    for (x in minX..maxX) {
        val p = Point2D(x, 2000000)
        if (input.any { it.beacon == p }) {
            continue
        }
        for (sensorBeaconPair in input) {
            if (sensorBeaconPair.distance() >= sensorBeaconPair.sensor.manhattanDistanceTo(p)) {
                answer1++
                break
            }
        }
    }
    println(answer1)

    var answer2 = BigInteger.ZERO
    for (y in 0L..4000000L) {
        var x = 0L
        while (x <= 4000000L) {
            val p = Point2D(x, y)
            val maxDiff = input.maxOf { it.distance() - it.sensor.manhattanDistanceTo(p) }
            if (maxDiff == -1L) {
                answer2 = BigInteger.valueOf(x) * BigInteger.valueOf(4000000) + BigInteger.valueOf(y)
                break
            }
            x += maxDiff + 1

        }
        if (answer2 != BigInteger.ZERO) {
            break
        }
    }
    println(answer2)
}

data class SensorBeaconPair(val sensor: Point2D, val beacon: Point2D) {
    fun distance() = sensor.manhattanDistanceTo(beacon)
}
