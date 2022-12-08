import kotlin.streams.toList

val input = object {}.javaClass.getResource("input.txt")!!.readText()

val height = input.lines().size
val width = input.lines()[0].length

val grid = input.lines()
    .map { it.chars()
        .map { Character.getNumericValue(it) }
        .toList() }
    .toList()

fun main() {
    val answer1 = grid
        .mapIndexed { y, row ->
            row.mapIndexed { x, cell ->
                isVisible(x, y, cell) }.count { it == true }
        }.sum()
    println(answer1)

    val answer2 = grid
        .mapIndexed { y, row ->
            row.mapIndexed { x, cell ->
                getScenicScore(x, y, cell)
            }.max()
        }.max()
    println(answer2)
}

fun isVisible(x: Int, y: Int, z: Int) =
    isVisibleFromTheTop(x, y, z)
            || isVisibleFromTheRight(x, y, z)
            || isVisibleFromTheBottom(x, y, z)
            || isVisibleFromTheLeft(x, y, z)

fun isVisibleFromTheLeft(x: Int, y: Int, z: Int) =
    if (x == 0) true
    else (0 until x).maxOf { grid[y][it] } < z

fun isVisibleFromTheRight(x: Int, y: Int, z: Int) =
    if (x + 1 == width) true
    else (x + 1 until width).maxOf { grid[y][it] } < z

fun isVisibleFromTheTop(x: Int, y: Int, z: Int) =
    if (y == 0) true
    else (0 until y).maxOf { grid[it][x] } < z

fun isVisibleFromTheBottom(x: Int, y: Int, z: Int) =
    if (y + 1 == height) true
    else (y + 1 until height).maxOf { grid[it][x] } < z

fun getScenicScore(x: Int, y: Int, z: Int) =
    listOf(getScenicScoreTop(x, y, z).toLong(),
        getScenicScoreRight(x, y, z).toLong(),
        getScenicScoreBottom(x, y, z).toLong(),
        getScenicScoreLeft(x, y, z).toLong())
        .filter { it != 0L }
        .fold(1L) { acc, n -> acc * n }

fun getScenicScoreTop(x: Int, y: Int, z: Int) =
    (0 until y).reversed().takeWhile { grid[it][x] < z }.count() + isVisibleFromTheTop(x, y, z).not().toInt()

fun getScenicScoreBottom(x: Int, y: Int, z: Int) =
    (y + 1 until height).takeWhile { grid[it][x] < z }.count() + isVisibleFromTheBottom(x, y, z).not().toInt()

fun getScenicScoreLeft(x: Int, y: Int, z: Int) =
    (0 until x).reversed().takeWhile { grid[y][it] < z }.count() + isVisibleFromTheLeft(x, y, z).not().toInt()

fun getScenicScoreRight(x: Int, y: Int, z: Int) =
    (x + 1 until width).takeWhile { grid[y][it] < z }.count() + isVisibleFromTheRight(x, y, z).not().toInt()

fun Boolean.toInt() =
    if (this) 1 else 0
