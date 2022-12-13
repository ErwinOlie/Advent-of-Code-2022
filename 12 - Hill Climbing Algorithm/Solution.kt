import kotlin.streams.toList

val input = object {}.javaClass.getResource("input.txt")!!.readText()

val map = input.lines()
    .map { it.chars().toList() }
    .toList()

var start1 = Point(-1, -1)
val starts2 = mutableListOf<Point>()
var end = Point(-1, -1)

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
                'S'.code -> start1 = Point(x, y)
                'a'.code -> starts2.add(Point(x, y))
                'E'.code -> end = Point(x, y)
            }
        }
    }

    val answer1 = findLowestDistance(end, listOf(start1))
    println(answer1)

    val answer2 = findLowestDistance(end, starts2)
    println(answer2)
}

fun findLowestDistance(end: Point, starts: List<Point>): Int {
    val distances = mutableMapOf<Point, Int>()
    val toVisit = mutableListOf<Point>()
    for (y in heightMap.indices) {
        for (x in heightMap[y].indices) {
            distances[Point(x, y)] = INFINITY
            toVisit.add(Point(x, y))
        }
    }
    distances[end] = 0

    while (toVisit.size > 0) {
        val closest = toVisit.sortedBy { distances[it] }.first()
        closest.neighbourPoints().forEach {
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

data class Point(val x: Int, val y: Int) {
    fun neighbourPoints() =
        listOf(Point(x - 1, y), Point(x, y - 1), Point(x + 1, y), Point(x, y + 1))
            .filter { it.x >= 0 && it.y >= 0 }
            .filter { it.y < map.size && it.x < map[0].size }
            .filter { heightMap[it.y][it.x] >= heightMap[this.y][this.x] - 1 }
}
