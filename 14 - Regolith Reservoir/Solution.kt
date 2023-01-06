package nl.erwinolie.`Advent-of-Code-2022`.`14 - Regolith Reservoir`

import nl.erwinolie.extensions.Point2D
import nl.erwinolie.extensions.input

val input = input()
    .lines()
    .map { it.split(" -> ").map { Point2D(it.split(",")[0].toLong(), it.split(",")[1].toLong()) } }

val rocks = mutableSetOf<Point2D>()
var depth = 0L

fun initRocks() {
    input.forEach { rockStructure ->
        rockStructure.zipWithNext().forEach { path ->
            var x = path.first.x
            var y = path.first.y
            rocks.add(path.first)
            while (x != path.second.x || y != path.second.y) {
                if (x < path.second.x) {
                    x++
                }
                if (x > path.second.x) {
                    x--
                }
                if (y < path.second.y) {
                    y++
                }
                if (y > path.second.y) {
                    y--
                }
                rocks.add(Point2D(x, y))
                if (y > depth) {
                    depth = y
                }
            }
        }
    }
}

fun main() {
    initRocks()
    println(answer1())
    println(answer2())
}

fun answer1(): Int {
    val sands = mutableSetOf<Point2D>()
    while (true) {
        var sand = Point2D(500, 0)
        while (true) {
            val nextPosCandidates = listOf(
                Point2D(sand.x, sand.y + 1),
                Point2D(sand.x - 1, sand.y + 1),
                Point2D(sand.x + 1, sand.y + 1)
            ).filter { it !in rocks && it !in sands }
            if (nextPosCandidates.isEmpty()) {
                sands += sand
                break
            } else {
                sand = nextPosCandidates.first()
            }
            if (sand.y >= depth) {
                break
            }
        }
        if (sand.y >= depth) {
            break
        }
    }
    return sands.size
}

fun answer2(): Int {
    val sands = mutableSetOf<Point2D>()
    val floorDepth = depth + 2
    while (true) {
        var sand = Point2D(500, 0)
        while (true) {
            val nextPosCandidates = listOf(
                Point2D(sand.x, sand.y + 1),
                Point2D(sand.x - 1, sand.y + 1),
                Point2D(sand.x + 1, sand.y + 1)
            ).filter { it !in rocks && it !in sands }
            if (nextPosCandidates.isEmpty()) {
                sands += sand
                break
            } else {
                sand = nextPosCandidates.first()
            }
            if (sand.y == floorDepth - 1) {
                sands += sand
                break
            }
        }
        if (sand.y == 0L) {
            break
        }
    }
    return sands.size
}
