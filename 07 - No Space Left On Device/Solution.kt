val input = object {}.javaClass.getResource("input.txt")!!.readText()
val terminal = input.lines()

interface DiskItem
data class Directory(val path: List<String>) : DiskItem
data class File(val name: String, val size: Long) : DiskItem
val root = Directory(listOf())
val disk = mutableMapOf<Directory, List<DiskItem>>()
var currentDirectory = Directory(listOf())

fun main() {
    parseDisk()

    val answer1 = disk.keys
        .map { it.getSize() }
        .filter { it <= 100000 }
        .sum()
    println(answer1)

    val sizeToFree = 30000000 - (70000000 - root.getSize())

    val answer2 = disk.keys
        .map { it.getSize() }
        .filter { it >= sizeToFree }
        .min()
    println(answer2)
}

fun parseDisk() {

    disk[root] = listOf()

    var pointer = 0
    while (pointer < terminal.size) {
        var line = terminal[pointer]
        if (line.startsWith("\$ cd ")) {
            when (val gotoDirectoryName = line.substring(5)) {
                "/" -> currentDirectory = root
                ".." -> currentDirectory = Directory(currentDirectory.path.dropLast(1))
                else ->  {
                    val newDirectory = Directory(currentDirectory.path + gotoDirectoryName)
                    if (newDirectory !in disk) {
                        disk[currentDirectory] = disk[currentDirectory]!! + newDirectory
                        disk[newDirectory] = listOf()
                    }
                    currentDirectory = newDirectory
                }
            }
            pointer += 1
        }
        if (line.startsWith("\$ ls")) {
            pointer += 1
            while (pointer < terminal.size) {
                line = terminal[pointer]
                if (line.startsWith("\$")) {
                    break
                }
                if (line.startsWith("dir")) {
                    pointer++
                    continue
                } else {
                    val currentFile = File(line.split(' ')[1], line.split(' ')[0].toLong())
                    disk[currentDirectory] = disk[currentDirectory]!! + currentFile
                    pointer++
                }
            }
        }
    }
}

fun Directory.getSize(): Long =
    disk[this]!!.sumOf {
        when (it) {
            is File -> it.size
            is Directory -> it.getSize()
            else -> 0L
        }
    }
