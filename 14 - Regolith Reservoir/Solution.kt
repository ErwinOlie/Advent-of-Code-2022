val input = object {}.javaClass.getResource("input.txt")!!.readText()
    .lines()
    .map { it.split(" -> ").map { Point(it.split(",")[0].toInt(), it.split(",")[1].toInt()) } }

val rocks = mutableSetOf<Point>()
var depth = 0

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
                rocks.add(Point(x, y))
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
    val sands = mutableSetOf<Point>()
    while (true) {
        var sand = Point(500, 0)
        while (true) {
            val nextPosCandidates = listOf(
                Point(sand.x, sand.y + 1),
                Point(sand.x - 1, sand.y + 1),
                Point(sand.x + 1, sand.y + 1)
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
    val sands = mutableSetOf<Point>()
    val floorDepth = depth + 2
    while (true) {
        var sand = Point(500, 0)
        while (true) {
            val nextPosCandidates = listOf(
                Point(sand.x, sand.y + 1),
                Point(sand.x - 1, sand.y + 1),
                Point(sand.x + 1, sand.y + 1)
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
        if (sand.y == 0) {
            break
        }
    }
    return sands.size
}

data class Point(val x: Int, val y: Int)
