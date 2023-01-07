package nl.erwinolie.`Advent-of-Code-2022`.`20 - Grove Position System`

import nl.erwinolie.extensions.input
import java.math.BigInteger
import java.math.BigInteger.ZERO

class MutableCircularList<T>(private val innerList: MutableList<T>) : MutableList<T> by innerList {
    private val originalSize = innerList.size

    fun get(index: BigInteger): T =
        innerList[normalizeIndex(index)]

    fun add(index: BigInteger, element: T) =
        innerList.add(normalizeIndex(index), element)

    fun removeAt(index: BigInteger): T =
        innerList.removeAt(normalizeIndex(index))

    fun normalizeIndex(index: BigInteger) =
        if (index < ZERO) {
            (index % size.toBigInteger() + size.toBigInteger()) % size.toBigInteger()
        } else {
            index % size.toBigInteger()
        }.toInt()
}
fun <T> MutableList<T>.circular(): MutableCircularList<T> = MutableCircularList(this)

val encryptedFile = input().lines().map { it.toInt().toBigInteger() }

fun mix(encryptedFile: List<BigInteger>, key: Int, times: Int): MutableCircularList<BigInteger> {
    val result = encryptedFile.map { it * key.toBigInteger() }.withIndex().toMutableList().circular()
    repeat (times) {
        for (i in encryptedFile.indices) {
            val p = result.indexOfFirst { it.index == i }.toBigInteger()
            val resultp = result[p.toInt()]
            val oldIndex = result.normalizeIndex(p)
            result.removeAt(oldIndex)
            val newIndex = result.normalizeIndex(p + resultp.value)
            result.add(newIndex, resultp)
        }
    }
    return result.map { it.value }.toMutableList().circular()
}

fun main() {
    val mixed1 = mix(encryptedFile, 1, 1)
    val zeroIndex1 = mixed1.indexOfFirst { it == ZERO }
    val answer1 = mixed1[zeroIndex1 + 1000] + mixed1[zeroIndex1 + 2000] + mixed1[zeroIndex1 + 3000]
    println(answer1)

    val mixed2 = mix(encryptedFile, 811589153, 10)
    val zeroIndex2 = mixed2.indexOfFirst { it == ZERO }
    val answer2 = mixed2[zeroIndex2 + 1000] + mixed2[zeroIndex2 + 2000] + mixed2[zeroIndex2 + 3000]
    println(answer2)
}
