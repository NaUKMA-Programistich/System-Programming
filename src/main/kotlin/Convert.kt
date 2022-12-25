import kotlin.math.pow
import kotlin.math.sqrt

sealed class State {
    companion object {
        val constant = (1 + sqrt(5.0)) / 2
    }
    data class B(override val value: Double) : State() {
        override fun toString(): String = "B($value)"
    }

    data class Value(override val value: Double, val pow: Int, val next: Value? = null, val type: Type) : State() {
        override fun toString(): String = "$type($value, $pow, $next)"
    }
    data class S(val l: Value, val r: Value) : State() {
        override val value: Double = l.value + r.value
        override fun toString(): String = "S($l, $r)"
    }

    abstract val value: Double
}

enum class Type { L, R }

fun main() {
    while (true) {
        try {
            print("Enter the number of points: ")
            val read = readln()

            val states = parseStates(str = read)
            val result = parseTree(states)
            println("Result: $result")
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
    }
}

fun parseStates(str: String): Pair<List<State.B>, List<State.B>> {
    val left = str.substringBefore(".")
    val right = str.substringAfter(".")

    val leftTreeB = left.toCharArray().map { State.B(it.toString().toDouble()) }
    val rightTreeB = right.toCharArray().map { State.B(it.toString().toDouble()) }

    leftTreeB.forEach(::println)
    rightTreeB.forEach(::println)

    return leftTreeB to rightTreeB
}

fun parseTree(tree: Pair<List<State.B>, List<State.B>>): Double {
    val resultLeft: State.Value = parseToStatesValue(tree.first, Type.L)
    val resultRight: State.Value = parseToStatesValue(tree.second, Type.R)

    val resultS = parseToStateS(resultLeft to resultRight)
    println(resultS)

    return resultS.value
}

fun parseToStatesValue(states: List<State.B>, type: Type): State.Value {
    var currentIndex = 0
    var currentPow = when (type) {
        Type.L -> states.size - 1
        Type.R -> -1
    }

    fun getNextValue(prevValue: Double): Double {
        val bValue = states[currentIndex].value
        val calculateCurrentPow = State.constant.pow(currentPow.toDouble())
        val nextValue = prevValue + bValue * calculateCurrentPow

        currentIndex++
        currentPow--
        return nextValue
    }

    val firstValue = getNextValue(0.0)
    val firstState = State.Value(firstValue, currentPow, type = type)

    fun getNextState(prevState: State.Value): State.Value {
        println(prevState)
        if (currentIndex == states.size) return prevState
        val nextValue = getNextValue(prevState.value)

        val nextState = State.Value(nextValue, currentPow, prevState, type)
        println(nextState)
        return nextState
    }

    return getNextState(firstState)
}

fun parseToStateS(states: Pair<State.Value, State.Value>): State.S {
    return State.S(states.first, states.second)
}
