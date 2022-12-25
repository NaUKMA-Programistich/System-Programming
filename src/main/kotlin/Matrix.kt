import kotlin.random.Random

//Start multiply thread (400 20000) (20000 500)
//General matrix multiplication thread time: 48 252 ms
//Start multiply
//General matrix multiplication time: 101 107 ms
//Is compare: true

/*
Start multiply thread (3000 3000) (3000 3000)
General matrix multiplication thread time: 150 037 ms
Start multiply
General matrix multiplication time: 546 402 ms
Is compare: true
 */


const val MAX = 1000L
typealias Matrix = Array<Array<Long>>

fun main() {
    val matrixFirst = generateMatrix(3000, 3000)
    val matrixSecond = generateMatrix(3000, 3000)

    val threadMatrix = (multiplyThread(matrixFirst, matrixSecond, 8))
    val generalMatrix = (multiply(matrixFirst, matrixSecond))

    println("Is compare: ${compareMatrix(generalMatrix, threadMatrix)}")
}

fun compareMatrix(matrixFirst: Matrix, matrixSecond: Matrix): Boolean {
    for (i in matrixFirst.indices) {
        for (y in matrixFirst[0].indices) {
            if (matrixFirst[i][y] != matrixSecond[i][y]) {
                return false
            }
        }
    }
    return true
}

fun generateMatrix(x: Int, y: Int): Array<Array<Long>> {
    val matrix = Array(x) { Array(y) { 0L } }
    for (i in 0 until x) {
        for (j in 0 until y) {
            matrix[i][j] = Random.nextLong(0, MAX)
        }
    }
    return matrix
}

fun multiplyThread(matrixFirst: Array<Array<Long>>, matrixSecond: Array<Array<Long>>, threadCount: Int): Array<Array<Long>> {
    println("Start multiply thread")
    val startGeneralMatrix = System.currentTimeMillis()

    val threads = mutableListOf<Thread>()

    val resultX = matrixFirst.size
    val resultY = matrixSecond[0].size
    val result = Array(resultX) { Array(resultY) { 0L } }

    for (i in 0 until resultX) {
        val task = MatrixCalculate(matrixFirst, matrixSecond, result, i)
        val thread = Thread(task)
        thread.start()
        threads.add(thread)

        if (threadCount == threads.size) {
            threads.forEach { it.join() }
            threads.clear()
        }
    }

    threads.forEach { it.join() }

    val endGeneralMatrix = System.currentTimeMillis()
    println("General matrix multiplication thread time: ${endGeneralMatrix - startGeneralMatrix} ms")
    return result
}

fun multiply(matrixFirst: Array<Array<Long>>, matrixSecond: Array<Array<Long>>): Array<Array<Long>> {
    println("Start multiply")
    val startGeneralMatrix = System.currentTimeMillis()
    val result = Array(matrixFirst.size) { Array(matrixSecond[0].size) { 0L } }
    for (i in matrixFirst.indices) {
        for (j in 0 until matrixSecond[0].size) {
            for (k in matrixSecond.indices) {
                result[i][j] += matrixFirst[i][k] * matrixSecond[k][j]
            }
        }
    }
    val endGeneralMatrix = System.currentTimeMillis()
    println("General matrix multiplication time: ${endGeneralMatrix - startGeneralMatrix} ms")
    return result
}

fun printMatrix(matrix: Array<Array<Long>>) {
    for (i in matrix.indices) {
        for (j in matrix[i].indices) {
            print("${matrix[i][j]} ")
        }
        println()
    }
}

class MatrixCalculate(
    private val matrixFirst: Matrix,
    private val matrixSecond: Matrix,
    private val result: Matrix,
    private val row: Int
) : Runnable {
    init{
        assert(matrixFirst[0].size == matrixSecond.size)
    }

    override fun run() {
        for (i in 0 until matrixSecond[0].size) {
            result[row][i] = 0
            for (y in 0 until matrixFirst[row].size) {
                result[row][i] += matrixFirst[row][y] * matrixSecond[y][i]
            }
        }
    }
}