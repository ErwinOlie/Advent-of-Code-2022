import java.lang.IllegalStateException
import java.math.BigInteger
import kotlin.text.RegexOption.MULTILINE

val input = object {}.javaClass.getResource("input.txt")!!.readText()

val monkeys = input.split("\\R\\R".toRegex())
    .map {
        val regex = """Monkey (\d+):
  Starting items: (.+)$
  Operation: new = (.+)$
  Test: divisible by (\d+)$
    If true: throw to monkey (\d+)$
    If false: throw to monkey (\d+)$""".toRegex(MULTILINE)
        val (id, items, operation, divisibilityTest, ifTrueMonkey, ifFalseMonkey) = regex.find(it)!!.destructured
        Monkey(
            id.toInt(),
            items.split(", ").map { it.toBigInteger() }.toMutableList(),
            operation,
            divisibilityTest.toBigInteger(),
            ifTrueMonkey.toInt(),
            ifFalseMonkey.toInt()
        )
    }.associateBy { it.id }

val megadivider = monkeys.values
    .map { it.divisibilityTest }
    .reduce { acc, i -> acc * i }

fun main() {
    repeat(10000) {
        monkeys.values.sortedBy { it.id }
            .forEach { monkey ->
                monkey.items.forEach { item ->
                    monkey.inspectItem(item)
                        .let { monkey.checkAndThrow(it) }
                }
                monkey.items.clear()
            }
    }

    val answer = monkeys.values.sortedByDescending { it.inspectedItemsCount }
        .take(2)
        .map { it.inspectedItemsCount }
        .reduce { acc, inspectedItemsCount -> acc * inspectedItemsCount }
    println(answer) // see r.68 for switching between answer1 and answer2
}

class Monkey(
    val id: Int,
    val items: MutableList<BigInteger>,
    val operation: String,
    val divisibilityTest: BigInteger,
    val ifTrueMonkey: Int,
    val ifFalseMonkey: Int
) {
    var inspectedItemsCount = BigInteger.ZERO

    fun inspectItem(item: BigInteger): BigInteger {
        inspectedItemsCount++
        val (operand1, operator, operand2) = operation.split(' ')
        val a = if (operand1 == "old") item else operand1.toBigInteger()
        val b = if (operand2 == "old") item else operand2.toBigInteger()
        return when(operator) {
            "*" -> a.multiply(b)
            "+" -> a.plus(b)
            else -> throw IllegalStateException()
        }.remainder(megadivider) // / 3 for answer 1, megadivider for answer 2
    }

    fun checkAndThrow(item: BigInteger) {
        if (item.remainder(divisibilityTest) == BigInteger.ZERO) {
            monkeys[ifTrueMonkey]!!.items.add(item)
        } else {
            monkeys[ifFalseMonkey]!!.items.add(item)
        }
    }
}
