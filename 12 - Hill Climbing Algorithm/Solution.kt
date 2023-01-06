package nl.erwinolie.`Advent-of-Code-2022`.`12 - Hill Climbing Algorithm`

import nl.erwinolie.extensions.Point2D
import kotlin.streams.toList
import nl.erwinolie.extensions.input

val map = input().lines()
    .map { it.chars().toList() }
    .toList()

var start1 = Point2D(-1, -1)
val starts2 = mutableListOf<Point2D>()
var end = Point2D(-1, -1)

val heightMap = map.map { row ->
    row.map { charToHeight(it.toChar()) }.toList()
}

fun charToHeight(c: Char) = when(c) {
    'S' -> 1
    'E' -> 26
    else -> c.code - 'a'.code + 1
}

const val INFINITY = Int.MAX_VALUE

fun main() {
    for (y in map.indices) {
        for (x in map[y].indices) {
            when(map[y][x]) {
                'S'.code -> start1 = Point2D(x, y)
                'a'.code -> starts2.add(Point2D(x, y))
                'E'.code -> end = Point2D(x, y)
            }
        }
    }

    val answer1 = findLowestDistance(end, listOf(start1))
    println(answer1)

    val answer2 = findLowestDistance(end, starts2)
    println(answer2)
}

fun findLowestDistance(end: Point2D, starts: List<Point2D>): Int {
    val distances = mutableMapOf<Point2D, Int>()
    val toVisit = mutableListOf<Point2D>()
    for (y in heightMap.indices) {
        for (x in heightMap[y].indices) {
            distances[Point2D(x, y)] = INFINITY
            toVisit.add(Point2D(x, y))
        }
    }
    distances[end] = 0

    while (toVisit.size > 0) {
        val closest = toVisit.sortedBy { distances[it] }.first()
        closest.neighbours4()
            .filter { it.isInBoundedBoxInclusive(0, 0, map[0].size - 1L, map.size - 1L) }
            .filter { heightMap[it.y.toInt()][it.x.toInt()] >= heightMap[closest.y.toInt()][closest.x.toInt()] - 1 }
            .forEach {
            val newDistance = distances[closest]!! + 1
            if (newDistance < distances[it]!!) {
                distances[it] = newDistance
            }
        }
        toVisit.remove(closest)
    }

    return distances
        .filter { it.value >= 0}
        .filter { it.key in starts }
        .minOf { it.value }
}
