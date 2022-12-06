val input = object {}.javaClass.getResource("input.txt")!!.readText()

fun main() {
    val answer1 = input
        .windowed(4)
        .first { it.toSet().size == it.length }
        .let { input.indexOf(it) + 4 }
    println(answer1)

    val answer2 = input
        .windowed(14)
        .first { it.toSet().size == it.length }
        .let { input.indexOf(it) + 14 }
    println(answer2)
}
