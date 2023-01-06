package nl.erwinolie.`Advent-of-Code-2022`.`05 - Supply Stacks`

import nl.erwinolie.extensions.input

val input = input().lines()

val crates = input
    .takeWhile { it.isNotBlank() }
    .rotate45()
    .map { "[a-zA-Z]+".toRegex().find(it)?.value }
    .filter { !it.isNullOrBlank() }
    .map { it!!.toMutableList() }

val procedures = input.takeLastWhile { it.isNotBlank() }
    .map { "\\d+".toRegex().findAll(it).map { it.value.toInt() }.toList() }
    .map { Movement(it[0], it[1] - 1, it[2] - 1) }

fun main() {
    val crates1 = crates.deepCopy()
    procedures.forEach { movement ->
        repeat(movement.amount) {
            val crate = crates1[movement.from].pop()
            crates1[movement.to].push(crate)
        }
    }
    val answer1 = cratesOnTop(crates1)
    println(answer1)

    val crates2 = crates.deepCopy()
    procedures.forEach { movement ->
        val crates = crates2[movement.from].pop(movement.amount)
        crates2[movement.to].pushAll(crates)
    }
    val answer2 = cratesOnTop(crates2)
    println(answer2)
}

fun List<String>.rotate45() = // black magic
    (0 until this[this.lastIndex].length)
        .map { y ->
            (this.size - 1 downTo 0)
                .filter { x -> y < this[x].length }
                .map { x -> this[x][y] }
                .joinToString("")
        }

fun <T> MutableList<T>.push(item: T) = this.add(item)
fun <T> MutableList<T>.pushAll(items: List<T>) = this.addAll(items)
fun <T> MutableList<T>.pop(): T = this.removeAt(this.lastIndex)
fun <T> MutableList<T>.pop(n: Int): List<T> = (0 until n).map{ this.removeAt(this.lastIndex) }.reversed()
fun <T> MutableList<T>.peek(): T = this[this.lastIndex]

fun List<MutableList<Char>>.deepCopy(): List<MutableList<Char>> = this.map { it.toMutableList() }
fun cratesOnTop(crates: List<MutableList<Char>>) = crates.map { if (it.isNotEmpty()) it.peek() else "" }.joinToString("")

data class Movement(val amount: Int, val from: Int, val to: Int)
