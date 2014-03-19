package ru.benchmark.fib

import org.openjdk.jmh.annotations.GenerateMicroBenchmark

/**
 * Created by evans on 19.03.14.
 */

open class FibTest() {
    /*
        Kotlin tests
    */
    [GenerateMicroBenchmark]
    public fun fibStaticTernaryBenchmarkKt() {
        FibKt.fibStaticTernaryIf(40)
    }

    [GenerateMicroBenchmark]
    public fun fibStaticIfBenchmarkKt() {
        FibKt.fibStaticIf(40)
    }

    [GenerateMicroBenchmark]
    public fun fibTernaryBenchmarkKt() {
        FibKt().fibTernaryIf(40)
    }

    [GenerateMicroBenchmark]
    public fun fibIfBenchmarkKt() {
        FibKt().fibIf(40)
    }

    /*
        Java tests
     */
    [GenerateMicroBenchmark]
    public fun fibStaticTernaryBenchmark() {
        Fib.fibStaticTernaryIf(40)
    }

    [GenerateMicroBenchmark]
    public fun fibStaticIfBenchmark() {
        Fib.fibStaticIf(40)
    }

    [GenerateMicroBenchmark]
    public fun fibTernaryBenchmark() {
        Fib().fibTernaryIf(40)
    }

    [GenerateMicroBenchmark]
    public fun fibIfBenchmark() {
        Fib().fibIf(40)
    }
}