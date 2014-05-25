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
import java.util.HashSet

/**
 * @author evans
 * 12.04.14.
 */


[State(Scope.Thread)]
public open class InliningWins {


    private val objects = ArrayList<String>()
    private val diffObjects = ArrayList<String>()

    //    Param("100", "1000", "10000", "50000", "100000", "1000000", "5000000")
//            [Param("100000","200000","300000","400000", "500000", "1000000")]
    [Param("10000000")]
    private var elemCount: Int = 0

    [Setup]
    public fun setup() {
        (0..elemCount).forEach { objects.add("") }
        (0..elemCount).forEach { i -> diffObjects.add(i.toString()) }
    }

//            [GenerateMicroBenchmark]
    public fun batchFilterInline(bh: BlackHole) {
        val r1 = diffObjects.inlineFilter { item -> !item.isEmpty() }
        bh.consume(r1);
        val r2 = diffObjects.inlineFilter { item -> item == "" }
        bh.consume(r2);
        val r3 = diffObjects.inlineFilter { item -> item == "1" }
        bh.consume(r3);
        val r4 = diffObjects.inlineFilter { item -> !item.isEmpty() }
        bh.consume(r4);
        val r5 = diffObjects.inlineFilter { item -> item == "" }
        bh.consume(r5);
        val r6 = diffObjects.inlineFilter { item -> item == "1" }
        bh.consume(r6);
    }

//        [GenerateMicroBenchmark]
    public fun batchFilterNonInline(bh: BlackHole) {
        val r1 = diffObjects.nonInlineFilter { item -> !item.isEmpty() }
        bh.consume(r1);
        val r2 = diffObjects.nonInlineFilter { item -> item == "" }
        bh.consume(r2);
        val r3 = diffObjects.nonInlineFilter { item -> item == "1" }
        bh.consume(r3);
        val r4 = diffObjects.nonInlineFilter { item -> !item.isEmpty() }
        bh.consume(r4);
        val r5 = diffObjects.nonInlineFilter { item -> item == "" }
        bh.consume(r5);
        val r6 = diffObjects.nonInlineFilter { item -> item == "1" }
        bh.consume(r6);
    }

//        [GenerateMicroBenchmark]
    public fun batchFilterNonInlineWithoutClassMethod(bh: BlackHole) {
        val r1 = nonInlineFilter(diffObjects) { item -> !item.isEmpty() }
        bh.consume(r1);
        val r2 = nonInlineFilter(diffObjects) { item -> item == "" }
        bh.consume(r2);
        val r3 = nonInlineFilter(diffObjects) { item -> item == "1" }
        bh.consume(r3);
        val r4 = nonInlineFilter(diffObjects) { item -> !item.isEmpty() }
        bh.consume(r4);
        val r5 = nonInlineFilter(diffObjects) { item -> item == "" }
        bh.consume(r5);
        val r6 = nonInlineFilter(diffObjects) { item -> item == "1" }
        bh.consume(r6);
    }


    /**
     * This method works slower with inlining for 25%
     */
        [GenerateMicroBenchmark]
    public fun twoFiltersInline(bh1: BlackHole, bh2: BlackHole) {
        val r2 = diffObjects.inlineFilter{ (item : String) -> item == "" }
        bh1.consume(r2);
        val r3 = diffObjects.inlineFilter{ (item : String) -> item == "" }
        bh2.consume(r3);
    }

//    [GenerateMicroBenchmark]
    public fun twoFiltersInlineCached(bh1: BlackHole, bh2: BlackHole) {
        val func = { (item : String) -> item == "" }
        val r2 = diffObjects.inlineFilter(func)
        bh1.consume(r2);
        val r3 = diffObjects.inlineFilter(func)
        bh2.consume(r3);
    }



    //        [GenerateMicroBenchmark]
    public fun twoFiltersInline1(bh1: BlackHole, bh2: BlackHole) {
        val r3 = diffObjects.inlineFilter { item -> item == "" }
        bh2.consume(r3);
    }

    public inline fun <T> Iterable<T>.inlineFilter2(): Unit {
        for (item in this) {
        }
    }

    public inline fun <T> Iterable<T>.inlineFilter(predicate: (T) -> Boolean): List<T> {
        val list = ArrayList<T>()
        for (item in this)
            if (predicate(item))
                list.add(item)
        return list
    }

    public fun <T> Iterable<T>.nonInlineFilter(predicate: (T) -> Boolean): List<T> {
        val list = ArrayList<T>()
        for (item in this)
            if (predicate(item))
                list.add(item)
        return list
    }

    public fun nonInlineFilter(iterable: Iterable<String>, predicate: (String) -> Boolean): Iterable<String> {
        val list = ArrayList<String>()
        for (item in iterable)
            if (predicate(item))
                list.add(item)
        return list
    }
}

