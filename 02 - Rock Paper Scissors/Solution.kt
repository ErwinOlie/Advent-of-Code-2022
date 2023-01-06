package nl.erwinolie.`Advent-of-Code-2022`.`02 - Rock Paper Scissors`

import nl.erwinolie.extensions.input

const val win = 6
const val draw = 3
const val lose = 0

const val rock = 1
const val paper = 2
const val scissors = 3

fun main() {
    val answer1 = input()
        .lines()
        .sumOf {
            mapOf(
                "B X" to lose + rock,
                "C Y" to lose + paper,
                "A Z" to lose + scissors,
                "A X" to draw + rock,
                "B Y" to draw + paper,
                "C Z" to draw + scissors,
                "C X" to win + rock,
                "A Y" to win + paper,
                "B Z" to win + scissors,
            )[it]!!
        }
    println(answer1)

    val answer2 = input()
        .lines()
        .sumOf {
            mapOf(
                "B X" to lose + rock,
                "C Y" to draw + scissors,
                "A Z" to win + paper,
                "A X" to lose + scissors,
                "B Y" to draw + paper,
                "C Z" to win + rock,
                "C X" to lose + paper,
                "A Y" to draw + rock,
                "B Z" to win + scissors,
            )[it]!!
        }
    println(answer2)
}
