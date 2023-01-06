
typealias Rock = List<List<Char>>
class RockStuff {
    private val rocks = listOf<Rock>(
        listOf(
            listOf('#', '#', '#', '#'),
        ),
        listOf(
            listOf('.', '#', '.'),
            listOf('#', '#', '#'),
            listOf('.', '#', '.'),
        ),
        listOf(
            listOf('.', '.', '#'),
            listOf('.', '.', '#'),
            listOf('#', '#', '#'),
        ),
        listOf(
            listOf('#'),
            listOf('#'),
            listOf('#'),
            listOf('#'),
        ),
        listOf(
            listOf('#', '#'),
            listOf('#', '#'),
        ),
    )
    private var nextRockPointer = 0

    fun nextRock(): Rock {
        val nextRock = rocks[nextRockPointer]
        nextRockPointer = (nextRockPointer + 1) % rocks.size
        return nextRock.reversed()
    }
}

class GasStuff {
    private val jetPattern = object {}.javaClass.getResource("input.txt")!!.readText()
    var nextGasJetPointer = 0

    fun nextGasJet(): Char {
        val nextGasJet = jetPattern[nextGasJetPointer]
        nextGasJetPointer = (nextGasJetPointer + 1) % jetPattern.length
        return nextGasJet
    }
}

data class Point(val x: Int, val y: Int) {
    fun move(delta: Point) =
            Point(x + delta.x, y + delta.y)

    fun down() =
        Point(x, y - 1)
}

data class State(
    val depth: List<Int>,
    val instructionPointer: Int,
    val chamberHeight: Long
)
data class Cycle(
    val repeatAfterN: Int,
    val addedChamberHeight: Long
)
class CycleFinder() {
    private val cache = mutableListOf<State>()

    fun calcDepth(chamber: MutableList<MutableList<Char>>): List<Int> {
        val depth = mutableListOf<Int>()
        for (x in 0 until 7) {
            var added = false
            for (y in chamber.size - 1 downTo 0) {
                if (chamber[y][x] == '#') {
                    depth.add((chamber.size - 1) - y)
                    added = true
                    break
                }
            }
            if (!added) {
                depth.add(Int.MAX_VALUE)
            }
        }
        return depth
    }

    fun addKnownStateAndDetectCycle(state: State): Cycle? {
        val m = cache.find {
            it.depth == state.depth && it.instructionPointer == state.instructionPointer
        }
        return if (m != null) {
            val indexOld = cache.indexOfFirst { it.depth == state.depth && it.instructionPointer == state.instructionPointer }
            val indexNew = cache.size
            Cycle(indexNew - indexOld, state.chamberHeight - m.chamberHeight)
        } else {
            cache.add(state)
            null
        }
    }

    fun calcRemainingHeight(cycle: Cycle, remainingHeight: Long): Long {
        val addedChamberHeight = cycle.addedChamberHeight
        val cycleSize = cycle.repeatAfterN

        val cycles = remainingHeight / cycleSize
        val remainingAfterCycles = remainingHeight % cycleSize

        val remainingHeight = cache
            .takeLast(cycle.repeatAfterN)
            .take(remainingAfterCycles.toInt())
            .let { Pair(it.first(), it.last()) }
            .let { it.second.chamberHeight - it.first.chamberHeight }

        return cycles * addedChamberHeight + remainingHeight
    }
}

class Chamber(
    private val rockStuff: RockStuff,
    private val gasStuff: GasStuff
) {
    private val chamber = mutableListOf<MutableList<Char>>() // chamber is upside-down
    private var chamberHeightExtra: Long = 0L
    private val cycleFinder = CycleFinder()

    fun dropRocks(n: Long) {

        for (i in 0L until n) {
            dropRock()
            val state = State(
                cycleFinder.calcDepth(chamber),
                gasStuff.nextGasJetPointer,
                chamber.size.toLong()
            )
            val cycle = cycleFinder.addKnownStateAndDetectCycle(state)
            if (cycle != null) {
                chamberHeightExtra += cycleFinder.calcRemainingHeight(cycle, n - i)
                break
            }
        }
    }

    private fun dropRock() {
        val rock = rockStuff.nextRock()
        var rockPosition = Point(2, chamber.size + 3)
        while (true) {
            rockPosition = tryPushWithGas(rock, rockPosition)
            if (!canRockFallDown(rock, rockPosition)) {
                break
            }
            rockPosition = rockPosition.down()
        }
        settleRock(rock, rockPosition)
//        this.print()
    }

    private fun tryPushWithGas(rock: Rock, position: Point): Point {
        val gasJet = gasStuff.nextGasJet()
        val dx = if (gasJet == '<') -1 else 1
        if (position.x + dx < 0) {
            return position
        }
        if (position.x + dx + (rock[0].size - 1) >= 7) {
            return position
        }
        rock.forEachIndexed { rockY, rockRow ->
            rockRow.forEachIndexed { rockX, rockChar ->
                if (rockChar == '.') {
                    return@forEachIndexed
                }
                if (position.y + rockY >= chamber.size) {
                    return@forEachIndexed
                }
                if (chamber[position.y + rockY][position.x + dx + rockX] == '.') {
                    return@forEachIndexed
                }
                return@tryPushWithGas position
            }
        }
        return position.move(Point(dx, 0))
    }

    private fun canRockFallDown(rock: Rock, position: Point): Boolean {
        if (position.y <= 0) {
            return false
        }
        rock.forEachIndexed { rockY, rockRow ->
            rockRow.forEachIndexed { rockX, rockChar ->
                if (rockChar == '.') {
                    return@forEachIndexed
                }
                if (position.down().y + rockY >= chamber.size) {
                    return@forEachIndexed
                }
                if (chamber[position.down().y + rockY][position.x + rockX] == '.') {
                    return@forEachIndexed
                }
                return@canRockFallDown false
            }
        }
        return true
    }

    private fun settleRock(rock: Rock, position: Point) {
        while (position.y + (rock.size - 1) >= chamber.size) {
            chamber.add(mutableListOf('.', '.', '.', '.', '.', '.', '.'))
        }
        rock.forEachIndexed { rockY, rockRow ->
            rockRow.forEachIndexed { rockX, rockChar ->
                if (rockChar == '#') {
                    chamber[position.y + rockY][position.x + rockX] = '#'
                }
            }
        }
    }

    fun height() =
        chamber.size + chamberHeightExtra

    fun print() {
        chamber.reversed().forEach {
            println("|" + it.joinToString("") + "|")
        }
        println("+-------+")
    }
}

fun main() {
    with(Chamber(RockStuff(), GasStuff())) {
        dropRocks(2022)
        println(height())
    }
    with(Chamber(RockStuff(), GasStuff())) {
        dropRocks(1000000000000)
        println(height())
    }
}
