package nl.erwinolie.`Advent-of-Code-2022`.`04 - Camp Cleanup`

import nl.erwinolie.extensions.input

val input = input().lines()
    .map { it.split(',').map { it.split('-').map { it.toInt() } } }
    .map { Pair(it[0][0]..it[0][1], it[1][0]..it[1][1]) }

fun main() {
    val answer1 = input
        .count { containsAll(it.first, it.second) || containsAll(it.second, it.first) }
    println(answer1)

    val answer2 = input
        .map { it.first.intersect(it.second) }
        .count { it.isNotEmpty() }
    println(answer2)
}

fun containsAll(a: IntRange, b: IntRange): Boolean =
    a.all { b.contains(it) }
