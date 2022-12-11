import java.lang.Math.abs

val input = object {}.javaClass.getResource("input.txt")!!.readText()

val clockTicks = sequence {
    var registerX = 1
    input.lines().asSequence()
        .forEach {
            when (it.split(' ')[0]) {
                "noop" -> yield(registerX)
                "addx" -> {
                    yield(registerX)
                    yield(registerX)
                    registerX += it.split(' ')[1].toInt()
                }
            }
        }
}

fun main() {
    val answer1 = clockTicks
        .filterIndexed { index, registerX -> ((index + 1) - 20) % 40 == 0 }
        .mapIndexed { index, registerX -> (20 + index * 40) * registerX }
        .sum()
    println(answer1)

    val answer2 = clockTicks
        .mapIndexed { index, registerX -> if (abs((index % 40) - registerX) <= 1) "#" else "." }
        .chunked(40)
        .map { it.joinToString("") }
        .joinToString("\r\n")
    println(answer2)
}
