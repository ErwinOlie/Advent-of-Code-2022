val input = object {}.javaClass.getResource("input.txt")!!.readText()

fun main() {
    val calories = input
        .split("\\R\\R".toRegex())
        .map {
            it.split("\\R".toRegex())
                .map(String::toInt)
                .sum()
        }

    val answer1 = calories.max()
    val answer2 = calories.sorted().takeLast(3).sum()

    println(answer1)
    println(answer2)
}
