package nl.erwinolie.`Advent-of-Code-2022`.`18 - Boiling Boulders`

import nl.erwinolie.extensions.Point3D
import nl.erwinolie.extensions.input
import nl.erwinolie.extensions.toPoint3D

val cubes = input().lines()
    .map { "^(\\d+),(\\d+),(\\d+)$".toRegex().find(it)!! }
    .map { it.toPoint3D() }

fun Point3D.adjecentPoints(): List<Point3D> =
    listOf(
        Point3D(x - 1, y, z),
        Point3D(x + 1, y, z),
        Point3D(x, y - 1, z),
        Point3D(x, y + 1, z),
        Point3D(x, y, z - 1),
        Point3D(x, y, z + 1),
    )

val minBorder = Point3D(
    cubes.minOf { it.x } - 1,
    cubes.minOf { it.y } - 1,
    cubes.minOf { it.z } - 1
)
val maxBorder = Point3D(
    cubes.maxOf { it.x } + 1,
    cubes.maxOf { it.y } + 1,
    cubes.maxOf { it.z } + 1
)

val reachablePoints = mutableListOf<Point3D>()

fun initReachablePoints() {
    val queue = mutableListOf(minBorder)
    while (queue.isNotEmpty()) {
        val point = queue.removeAt(0)
        if (point.x < minBorder.x || point.x > maxBorder.x) {
            continue
        }
        if (point.y < minBorder.y || point.y > maxBorder.y) {
            continue
        }
        if (point.z < minBorder.z || point.z > maxBorder.z) {
            continue
        }
        if (point in reachablePoints) {
            continue
        }
        reachablePoints.add(point)
        if (point !in cubes) {
            point.adjecentPoints().forEach {
                queue.add(it)
            }
        }
    }
}

fun main() {
    val answer1 = cubes
        .flatMap { it.adjecentPoints() }
        .filter{ it !in cubes }
        .count()
    println(answer1)

    initReachablePoints()

    val answer2 = cubes
        .flatMap { it.adjecentPoints() }
        .filter { it in reachablePoints }
        .filter { it !in cubes }
        .count()
    println(answer2)
}
