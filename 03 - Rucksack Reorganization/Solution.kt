val input = object {}.javaClass.getResource("input.txt")!!.readText()

fun toPriority(c : Char) =
    if (c.isLowerCase()) {
        c.code - 'a'.code + 1
    } else {
        c.code - 'A'.code + 27
    }

fun main() {
    val answer1 = input.lines()
        .map { Pair(it.substring(0, it.length/2), it.substring(it.length/2, it.length)) }
        .flatMap { it.first.toSet().intersect(it.second.toSet()) }
        .sumOf(::toPriority)
    println(answer1)

    val answer2 = input.lines()
        .windowed(3, 3)
        .flatMap { it[0].toSet().intersect(it[1].toSet().intersect(it[2].toSet())) }
        .sumOf(::toPriority)
    println(answer2)
}
