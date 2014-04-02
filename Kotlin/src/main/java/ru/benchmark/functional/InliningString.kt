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
public open class InliningString {

    private val objects = ArrayList<String>()
    private val diffObjects = ArrayList<String>()

//        [Param("10000", "100000", "1000000")]
    [Param("1000000")]
    private var elemCount: Int = 0


    [Setup]
    public fun setup() {
        (0..elemCount).forEach { objects.add("") }
        (0..elemCount).forEach { diffObjects.add(""); diffObjects.add("1") }
    }

//    [GenerateMicroBenchmark]
    public fun reduceSum(bh: BlackHole) {
        bh.consume(objects.reduce { a, b -> a + b })
    }

//    [GenerateMicroBenchmark]
    public fun reduceSumMy(bh: BlackHole) {
        bh.consume(reduce(objects))
    }

//    [GenerateMicroBenchmark]
    public fun mapId(bh: BlackHole) {
        bh.consume(objects.map { a -> a });
    }

//    [GenerateMicroBenchmark]
    public fun mapIdMy(bh: BlackHole) {
        bh.consume(map(objects));
    }

//    [GenerateMicroBenchmark]
    public fun filterEmpty(bh: BlackHole) {
        bh.consume(diffObjects.filter { a -> !a.isEmpty() });
    }

//    [GenerateMicroBenchmark]
    public fun filterEmptyMy1(bh: BlackHole) {
        bh.consume(filter1(diffObjects){item -> !item.isEmpty()});
    }

//    [GenerateMicroBenchmark]
    public fun filterEmptyMy2(bh: BlackHole) {
        bh.consume(filter2(diffObjects){item -> !item.isEmpty()});
    }

    public inline fun filter1(iterable : Iterable<String>, predicate : (String) -> Boolean) : Iterable<String> {
        val list = ArrayList<String>()
        for (item in iterable)
            if(predicate(item))
                list.add(item)
        return list
    }

    public fun filter2(iterable : Iterable<String>, predicate : (String) -> Boolean) : Iterable<String> {
        val list = ArrayList<String>()
        for (item in iterable)
            if(predicate(item))
                list.add(item)
        return list
    }

    [GenerateMicroBenchmark]
    [Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)]
    [Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)]
    public fun strange1(bh: BlackHole) {
        bh.consume(fun1(objects){1});
    }

    [GenerateMicroBenchmark]
    [Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)]
    [Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)]
    public fun strange2(bh: BlackHole) {
        bh.consume(fun2(objects){1});
    }

    public fun fun1(iter : Iterable<String> , provider : () -> Int) : Int {
        var sum = 0;
        for(i in iter)
            sum += provider()
        return sum
    }

    public inline fun fun2(iter : Iterable<String> , provider : () -> Int) : Int {
        var sum = 0;
        for(i in iter)
            sum += provider()
        return sum
    }

    private fun map(iterable: Iterable<String>): Iterable<String> {
        val list = ArrayList<String>()
        for (item in iterable)
            list.add(id(item))
        return list
    }

    private fun id(s: String): String = s

    //exact realization of reduce in kotlin
    private fun reduce(iterable: Iterable<String>): String {
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


    private fun sum(accumulator: String, next: String): String {
        return accumulator + next
    }

    //сравнение байткода джава и котлин
    //filter / map
}
