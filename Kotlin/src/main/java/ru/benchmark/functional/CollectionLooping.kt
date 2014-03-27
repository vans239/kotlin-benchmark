package ru.benchmark.functional

import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.logic.BlackHole
import java.util.ArrayList
import java.util.Random
import java.util.concurrent.TimeUnit

/**
 * @author evans 27.03.14.
 */

[Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)]
[Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)]
[Fork(1)]
[BenchmarkMode(Mode.AverageTime)]
[OutputTimeUnit(TimeUnit.MICROSECONDS)]
[State(Scope.Thread)]
public open class CollectionLooping() {
    private val objects = ArrayList<Double>()
    private val objectsNullable = ArrayList<Double?>()

//    [Param("1000", "10000", "100000")]
    [Param("100000")]
    private var elemCount: Int = 0
    private var random: Random = Random()

    [Setup]
    public fun setup() {
        for (i in 0..elemCount - 1) {
            val next = random.nextDouble()
            objects.add(next)
            objectsNullable.add(next)
        }
    }

    [GenerateMicroBenchmark]
    public fun passObjectsIndexed(bh: BlackHole) {
        var sum = 0.0
        //noinspection ForLoopReplaceableByForEach
        for (i in 0..objects.size() - 1) {
            sum += objects.get(i)
        }
        bh.consume(sum)
    }

    [GenerateMicroBenchmark]
    public fun passObjectsIndexedNullable(bh: BlackHole) {
        var sum = 0.0
        //noinspection ForLoopReplaceableByForEach
        for (i in 0..objectsNullable.size() - 1) {
            sum += objects.get(i)
        }
        bh.consume(sum)
    }

    [GenerateMicroBenchmark]
    public fun passObjectsForEachSimple(bh: BlackHole) {
        var sum = 0.0
        for (obj in objects) {
            sum += obj
        }
        bh.consume(sum)
    }

    [GenerateMicroBenchmark]
    public fun passObjectsForEachSimpleNullable(bh: BlackHole) {
        var sum = 0.0
        for (obj in objectsNullable) {
            sum += obj!!
        }
        bh.consume(sum)
    }

    [GenerateMicroBenchmark]
    public fun passObjectsReduce(bh: BlackHole) {
        var sum = objects.reduce { a, b -> a.plus(b) }
        bh.consume(sum)
    }

    [GenerateMicroBenchmark]
    public fun passObjectsReduceNullable(bh: BlackHole) {
        var sum = objectsNullable.reduce { a, b -> a!!.plus(b!!) }
        bh.consume(sum)
    }

    [GenerateMicroBenchmark]
    public fun passObjectsSum(bh: BlackHole) {
        var sum = objects.sum()
        bh.consume(sum)
    }

    [GenerateMicroBenchmark]
    public fun passObjectsSumNullable(bh: BlackHole) {
        var sum = objectsNullable.map { a -> a!! }.sum()
        bh.consume(sum)
    }

    [GenerateMicroBenchmark]
    public fun passObjectsReduceSafetyNullable(bh: BlackHole) {
        var sum = objectsNullable.reduce { a, b -> a?.plus(b!!) }
        bh.consume(sum)
    }
}
