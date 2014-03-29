package ru.benchmark.functional

import org.openjdk.jmh.annotations.Warmup
import java.util.concurrent.TimeUnit
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Param
import org.openjdk.jmh.annotations.Setup
import java.util.Random
import java.util.ArrayList
import java.util.LinkedList
import org.openjdk.jmh.annotations.GenerateMicroBenchmark
import org.openjdk.jmh.logic.BlackHole

/**
 * @author evans
 * 27.03.14.
 */

[Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)]
[Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)]
[Fork(1)]
[BenchmarkMode(Mode.AverageTime)]
[OutputTimeUnit(TimeUnit.MICROSECONDS)]
[State(Scope.Thread)]
public open class Inlining {

    private val objects = ArrayList<Double>()

//        [Param("10000", "100000", "1000000")]
    [Param("1000000")]
    private var elemCount: Int = 0


    [Setup]
    public fun setup() {
        var random: Random = Random()
        (0..elemCount).forEach { objects.add(random.nextDouble()) }
    }

    [GenerateMicroBenchmark]
    public fun reduceSum(bh: BlackHole) {
        bh.consume(objects.reduce { a, b -> a + b })
    }

//    [GenerateMicroBenchmark]
    public fun reduceSumMy(bh: BlackHole) {
        bh.consume(reduce(objects))
    }

    //exact realization of reduce in kotlin
    private fun reduce(iterable: Iterable<Double>): Double {
        val iterator = iterable.iterator()
        if (!iterator.hasNext()) {
            throw UnsupportedOperationException("Empty iterable can't be reduced")
        }

        var accumulator = iterator.next()
        while (iterator.hasNext()) {
            accumulator = sum(accumulator, iterator.next())
        }
        return accumulator
    }


    private fun sum(accumulator: Double, next: Double): Double {
        return accumulator + next
    }
}