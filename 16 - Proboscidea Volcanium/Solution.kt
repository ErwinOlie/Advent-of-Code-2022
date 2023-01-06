package nl.erwinolie.`Advent-of-Code-2022`.`16 - Proboscidea Volcanium`

import nl.erwinolie.extensions.input

import kotlin.math.max
import kotlin.math.min

val input = input()
val inputRegex = "^Valve (.+) has flow rate=(\\d+); tunnels? leads? to valves? (.+)$".toRegex()

lateinit var valves: Map<String, Valve>
lateinit var tunnels: List<Tunnel>

data class Valve(
    val label: String,
    val flowRate: Int
) {
    fun getNeighbours() =
        tunnels.filter { it.from == this }
}

data class Tunnel(
    val from: Valve,
    val to: Valve,
    val weight: Int
)

fun initValves() {
    valves = input.lines()
        .map {
            val (label, flowrate, _) = inputRegex.find(it)!!.destructured
            Valve(label, flowrate.toInt())
        }
        .associateBy(Valve::label)
}

fun initTunnels() {
    tunnels = input.lines()
        .map {
            val (valve, _, tunnels) = inputRegex.find(it)!!.destructured
            Pair(valve, tunnels.split(", "))
        }
        .flatMap { valveTunnelsPair ->
            valveTunnelsPair.second.map { otherValve ->
                Tunnel(valves[valveTunnelsPair.first]!!, valves[otherValve]!!, 1)
            }
        }
        .toList()
}

fun removeValvesWithNoFlow() {
    val INFINITY = 9999
    val dist = Array(valves.size) { Array(valves.size) { INFINITY } }

    tunnels.forEach { tunnel ->
        dist[valves.values.indexOf(tunnel.from)][valves.values.indexOf(tunnel.to)] = tunnel.weight
    }
    valves.values.forEachIndexed { index, _ ->
        dist[index][index] = 0
    }

    valves.values.forEachIndexed { k, _ ->
        valves.values.forEachIndexed { i, _ ->
            valves.values.forEachIndexed { j, _ ->
                dist[i][j] = min(dist[i][j], dist[i][k] + dist[k][j])
            }
        }
    }

    val newValves = valves.filter { (_, v) -> v.label == "AA" || v.flowRate != 0 }
    val newTunnels = mutableListOf<Tunnel>()
    valves.values.forEachIndexed { i, from ->
        valves.values.forEachIndexed { j, to ->
            if (i != j && dist[i][j] != 0 && dist[i][j] != INFINITY && from in newValves.values && to in newValves.values) {
                newTunnels.add(Tunnel(from, to, dist[i][j]))
            }
        }
    }
    println(newTunnels)

    valves = newValves
    tunnels = newTunnels
}

fun findSubsets(output: MutableList<List<Valve>>, input: List<Valve>, builder: MutableList<Valve>, index: Int) {
    if (builder.size == index) {
        output.add(builder)
        return
    }

    findSubsets(output, input, builder.toList().toMutableList(), index + 1)
    builder.add(input[index])
    findSubsets(output, input, builder.toList().toMutableList(), index + 1)
}

fun diffWithAllValves(curr: List<Valve>) =
    valves.values
        .filter { it !in curr }
        .filter { it.label != "AA" }
        .toList()

fun main() {
    initValves()
    initTunnels()
    removeValvesWithNoFlow()

    val answer1 = search(valves["AA"]!!, 0, listOf())
    println(answer1)


    val output = mutableListOf<List<Valve>>()
    findSubsets(output, valves.values.toList(), mutableListOf(), 0)

    val pairs = output
        .map { subset -> Pair(subset.filter { it.label != "AA" }, diffWithAllValves(subset)) }
        .filter { pair -> pair.first.isNotEmpty() && pair.second.isNotEmpty() }
        .filter { pair -> !pair.first.all { it in pair.second } }
        .filter { pair -> !pair.second.all { it in pair.first } }
        .fold(mutableListOf<Pair<List<Valve>, List<Valve>>>()) { acc, pair ->
            if (Pair(pair.second, pair.first) !in acc) {
                acc += pair
            }
            acc
        }

    val answer2 = pairs
        .maxOf { search(valves["AA"]!!, 4, it.first.map { it.label }) + search(valves["AA"]!!, 4, it.second.map { it.label }) }
    println(answer2)
}

fun search(currentValve: Valve, minutesPassed: Int, openedValves: List<String>): Int {

    val currentPressureReleased = currentValve.flowRate * (30 - minutesPassed - 1)
    var maxPressureReleased = 0

    if (currentValve.label == "AA") {
        maxPressureReleased = currentValve.getNeighbours()
            .filter { it.to.label !in openedValves }
            .filter { minutesPassed + it.weight < 30 }
            .maxOf { search(it.to, minutesPassed + it.weight, openedValves + currentValve.label) }
    } else {
        val maxWithOpeningCurrentValve = currentValve.getNeighbours()
            .filter { it.to.label !in openedValves }
            .filter { minutesPassed + 1 + it.weight < 30 }
            .maxOfOrNull { search(it.to, minutesPassed + it.weight + 1, openedValves + currentValve.label) } ?: 0
        maxPressureReleased = max(maxWithOpeningCurrentValve, maxPressureReleased)
    }

    return maxPressureReleased + currentPressureReleased
}
