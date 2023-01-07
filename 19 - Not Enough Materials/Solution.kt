package nl.erwinolie.`Advent-of-Code-2022`.`19 - Not Enough Materials`

import nl.erwinolie.extensions.factorial
import nl.erwinolie.extensions.input
import kotlin.math.max

data class Blueprint(
    val id: Int,
    val oreRobotOres: Int,
    val clayRobotOres: Int,
    val obsidianRobotOres: Int,
    val obsidianRobotClay: Int,
    val geodeRobotOres: Int,
    val geodeRobotObsidian: Int
)

val blueprints = input().lines()
    .map { "^Blueprint (\\d+): Each ore robot costs (\\d+) ore. Each clay robot costs (\\d+) ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian.$".toRegex().find(it)!! }
    .map {
        val (id, oreRobotOres, clayRobotOres, obsidianRobotOres, obsidianRobotClay, geodeRobotOres, geodeRobotObsidian) = it.destructured
        Blueprint(id.toInt(), oreRobotOres.toInt(), clayRobotOres.toInt(), obsidianRobotOres.toInt(), obsidianRobotClay.toInt(), geodeRobotOres.toInt(), geodeRobotObsidian.toInt())
    }

fun main() {
    val answer1 = blueprints.asSequence()
        .map { it.id * largestNumberOfGeodes(State(it, 24, 0, 0, 0, 0, 1, 0, 0, 0)) }
        .sum()
    println(answer1)

    val answer2 = blueprints.take(3).asSequence()
        .map { largestNumberOfGeodes(State(it, 32, 0, 0, 0, 0, 1, 0, 0, 0)) }
        .reduce { acc, i -> acc * i }
    println(answer2)
}

data class State(
    val blueprint: Blueprint,
    val minutesRemaining: Int,
    val ores: Int,
    val clay: Int,
    val obsidian: Int,
    val geodes: Int,
    val oreRobots: Int,
    val clayRobots:Int,
    val obsidianRobots: Int,
    val geodeRobots: Int
) {
    private fun doActions() = copy(
        minutesRemaining = minutesRemaining - 1,
        ores = ores + oreRobots,
        clay = clay + clayRobots,
        obsidian =  obsidian + obsidianRobots,
        geodes = geodes + geodeRobots
    )

    fun geodesLimit(): Int {
        var geodes = geodes
        var geodeRobots = geodeRobots
        for (i in 1..minutesRemaining) {
            geodes += geodeRobots
            geodeRobots += 1
        }
        return geodes
    }

    fun maxOresNeeded() =
        listOf(blueprint.oreRobotOres, blueprint.clayRobotOres, blueprint.obsidianRobotOres, blueprint.geodeRobotOres).max()

    fun maxClayNeeded() =
        blueprint.obsidianRobotClay

    fun maxObsidianNeeded() =
        blueprint.geodeRobotObsidian

    fun neighbours(): List<State> {
        val neighbours = mutableListOf<State>()
        if (oreRobots < maxOresNeeded()) {
            var nextState = doActions()
            while (nextState.ores < blueprint.oreRobotOres) {
                nextState = nextState.doActions()
            }
            if (nextState.minutesRemaining >= 2) {
                neighbours += nextState.copy(
                    ores = nextState.ores - blueprint.oreRobotOres - 1,
                    oreRobots = nextState.oreRobots + 1
                )
            }
        }
        if (clayRobots < maxClayNeeded()) {
            var nextState = doActions()
            while (nextState.ores < blueprint.clayRobotOres) {
                nextState = nextState.doActions()
            }
            if (nextState.minutesRemaining >= 3) {
                neighbours += nextState.copy(
                    ores = nextState.ores - blueprint.clayRobotOres,
                    clay = nextState.clay - 1,
                    clayRobots = nextState.clayRobots + 1
                )
            }
        }
        if (clayRobots >= 1 && obsidianRobots < maxObsidianNeeded()) {
            var nextState = doActions()
            while (nextState.ores < blueprint.obsidianRobotOres || nextState.clay < blueprint.obsidianRobotClay) {
                nextState = nextState.doActions()
            }
            if (nextState.minutesRemaining >= 2) {
                neighbours += nextState.copy(
                    ores = nextState.ores - blueprint.obsidianRobotOres,
                    clay = nextState.clay - blueprint.obsidianRobotClay,
                    obsidian = nextState.obsidian - 1,
                    obsidianRobots = nextState.obsidianRobots + 1
                )
            }
        }
        if (obsidianRobots >= 1) {
            var nextState = doActions()
            while (nextState.ores < blueprint.geodeRobotOres || nextState.obsidian < blueprint.geodeRobotObsidian) {
                nextState = nextState.doActions()
            }
            if (nextState.minutesRemaining >= 1) {
                neighbours += nextState.copy(
                    ores = nextState.ores - blueprint.geodeRobotOres,
                    obsidian = nextState.obsidian - blueprint.geodeRobotObsidian,
                    geodes = nextState.geodes - 1,
                    geodeRobots = nextState.geodeRobots + 1
                )
            }
        }
        if (neighbours.size == 0) {
            neighbours.add(doActions())
        }
        return neighbours
    }
}

fun largestNumberOfGeodes(beginState: State): Int {
    val queue = mutableListOf(beginState)
    val visited = mutableSetOf<State>()

    var maxGeodes = -1

    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        if (current in visited) {
            continue
        }
        visited += current
        if (current.minutesRemaining <= 0) {
            maxGeodes = max(maxGeodes, current.geodes)
            continue
        }
        if (current.geodesLimit() <= maxGeodes) {
            continue
        }
        current.neighbours()
            .forEach { queue.add(it) }
    }

    return maxGeodes
}
