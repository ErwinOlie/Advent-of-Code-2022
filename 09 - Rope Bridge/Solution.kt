package nl.erwinolie.`Advent-of-Code-2022`.`09 - Rope Bridge`

import kotlin.math.abs
import nl.erwinolie.extensions.input

val inputSteps = input().lines()
    .flatMap {
        val (direction, amount) = it.split(' ')
        generateSequence { direction }.take(amount.toInt())
    }
    .map { it.toMovement() }

///////

var rope = (0..9).map { Point(0, 0) }.toMutableList()
val trace = rope.map { mutableListOf(it) }

fun main() {
    inputSteps.forEach {
        moveHead(it)
        rope.indices.asSequence()
            .filter { i -> i != 0 }
            .filter { i -> !rope[i].isTouching(rope[i-1]) }
            .forEach { i -> moveTail(i) }
    }

    val answer1 = trace[1]
        .distinct()
        .size
    println(answer1)

    val answer2 = trace[9]
        .distinct()
        .size
    println(answer2)
}

fun moveHead(movement: Movement) {
    rope[0] = Point(rope[0].x + movement.dx, rope[0].y + movement.dy)
    trace[0].add(rope[0])
}

fun moveTail(index: Int) {
    val tail = rope[index]
    val head = rope[index - 1]

    val dx = abs(head.x - tail.x)
    val dy = abs(head.y - tail.y)
    if ((dx == 2 && dy == 0) || (dx == 0 && dy == 2) || (dx == 2 && dy == 2)) {
        rope[index] = Point(tail.x + (head.x - tail.x) / 2, tail.y + (head.y - tail.y) / 2)
    }
    else if ((dx == 2 && dy == 1)) {
        rope[index] = Point(tail.x + (head.x - tail.x) / 2, head.y)
    }
    else if ((dx == 1 && dy == 2)) {
        rope[index] = Point(head.x, tail.y + (head.y - tail.y) / 2)
    }
    trace[index].add(rope[index])
}

///////

enum class Movement(val abbrev: String, val dx: Int, val dy: Int) {
    UP("U", 0, -1),
    DOWN("D", 0, 1),
    LEFT("L", -1, 0),
    RIGHT("R", 1, 0);
}
fun String.toMovement() =
    Movement.values().find { it.abbrev == this }!!

///////

data class Point(val x: Int, val y: Int) {
    fun isTouching(other: Point) =
        abs(x - other.x) <= 1 && abs(y - other.y) <= 1
}