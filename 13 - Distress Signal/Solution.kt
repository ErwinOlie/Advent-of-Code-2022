import java.lang.IllegalStateException
import java.lang.Integer.max

val input = object {}.javaClass.getResource("input.txt")!!.readText()

fun main() {
    val answer1 = input
        .split("\\R\\R".toRegex())
        .map {
            val (left, right) = it.lines()
            Pair(parse(left), parse(right))
        }
        .map { NodeComparator.compare(it.first, it.second) == -1 }
        .mapIndexed { i, b -> if (b) i + 1 else 0 }
        .sum()
    println(answer1)

    val sortedPackets = (input
        .lines() + listOf("[[2]]", "[[6]]"))
        .filter { it.isNotEmpty() }
        .map { parse(it) }
        .sortedWith(NodeComparator::compare)
    val answer2 = (sortedPackets.indexOf(parse("[[2]]")) + 1) * (sortedPackets.indexOf(parse("[[6]]")) + 1)
    println(answer2)
}

data class Node(
    val value: Any,
) {
    override fun toString(): String {
        return if (value is Int) {
            value.toString()
        } else {
            "[" + (value as List<*>).joinToString(",") + "]"
        }
    }
}

object NodeComparator : Comparator<Any> {
    override fun compare(_a: Any?, _b: Any?): Int {
        val a = if (_a is Node) _a else Node(_a!!)
        val b = if (_b is Node) _b else Node(_b!!)
        if (a.value is Int && b.value is Int) {
            return a.value.compareTo(b.value)
        }
        if (a.value is List<*> && b.value is Int) {
            return compare(a, Node(mutableListOf(b.value)))
        }
        if (a.value is Int && b.value is List<*>) {
            return compare(Node(mutableListOf(a.value)), b)
        }
        if (a.value is List<*> && b.value is List<*>) {
            for (i in 0 until max(a.value.size, b.value.size)) {
                if (i >= a.value.size) {
                    return -1
                }
                if (i >= b.value.size) {
                    return 1
                }
                val c = compare(a.value[i]!!, b.value[i]!!)

                if (c != 0) {
                    return c
                }
            }
            return 0
        }
        throw IllegalStateException()
    }
}

fun parse(packet: String): Node =
    parse(packet, 1).first

fun parse(packet: String, startIndex: Int): Pair<Node, Int> {
    var index = startIndex
    var numberAcc = ""
    val childrenAcc = mutableListOf<Node>()
    while (index < packet.length) {
        when (packet[index]) {
            '[' -> {
                val (child, newIndex) = parse(packet, index + 1)
                childrenAcc.add(child)
                index = newIndex
            }
            ']' -> {
                break
            }
            ',' -> {
                if (numberAcc.isNotEmpty()) {
                    childrenAcc.add(Node(numberAcc.toInt()))
                    numberAcc = ""
                }
                index++
            }
            else -> {
                numberAcc += packet[index]
                index++
            }
        }
    }
    if (numberAcc.isNotEmpty() && childrenAcc.isNotEmpty()) {
        childrenAcc.add(Node(numberAcc.toInt()))
        numberAcc = ""
    }
    return if (numberAcc.isNotEmpty()) {
        Pair(Node(mutableListOf(numberAcc.toInt())), index + 1)
    } else {
        Pair(Node(childrenAcc), index + 1)
    }
}
