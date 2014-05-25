package ru.benchmark.functional

import kotlin.inline;
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
 * 12.04.14.
 */


[State(Scope.Thread)]
public open class Capacity {

    private val diffObjects = LinkedList<String>()

    [Setup]
    public fun setup() {
        (0..elemCount).forEach { diffObjects.add(""); diffObjects.add("1") }
    }

//        Param("1000000")
    Param("100", "1000", "10000", "50000", "100000", "1000000", "5000000")
    private var elemCount: Int = 0

    GenerateMicroBenchmark
    public fun mapDefaultId() : List<String> {
        return diffObjects.map { x -> id(x)}
    }

    GenerateMicroBenchmark
    public fun mapWithInitialCapacityId() : List<String> {
        return diffObjects.mapWithInitialCapacity { x -> id(x)}
    }

    GenerateMicroBenchmark
    public fun mapDefaultAppend() : List<String> {
        return diffObjects.map { x -> appendOne(x)}
    }

    GenerateMicroBenchmark
    public fun mapWithInitialCapacityAppend() : List<String> {
        return diffObjects.mapWithInitialCapacity { x -> appendOne(x)}
    }

    GenerateMicroBenchmark
    public fun mapDefaultRepeat() : List<String> {
        return diffObjects.map { x -> repeatTenTimes(x)}
    }

    GenerateMicroBenchmark
    public fun mapWithInitialCapacityRepeat() : List<String> {
        return diffObjects.mapWithInitialCapacity { x -> repeatTenTimes(x)}
    }

    public inline fun <T, R> List<T>.mapWithInitialCapacity(transform : (T) -> R) : List<R> {
        return mapTo(ArrayList<R>(size()), transform)
    }

    fun appendOne(s : String) : String = s + "1"

    fun repeatTenTimes(s : String) : String = s.repeat(10)
}
