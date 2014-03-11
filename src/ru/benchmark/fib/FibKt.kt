package ru.benchmark.fib
import org.openjdk.jmh.annotations.GenerateMicroBenchmark
import ru.benchmark.fib.Fib

open class FibKt() {
    class object {
        fun fibStaticTernaryIf(n: Int): Int {
            return if (n >= 2)
                fibStaticTernaryIf(n - 1) + fibStaticTernaryIf(n - 2)
            else
                1
        }

        fun fibStaticIf(n: Int): Int {
            if (n >= 2)
                return fibStaticIf(n - 1) + fibStaticIf(n - 2)
            else
                return 1
        }
    }

    fun fibIf(n: Int): Int {
        if (n >= 2)
            return fibIf(n - 1) + fibIf(n - 2)
        else
            return 1
    }

    fun fibTernaryIf(n: Int): Int {
        return if (n >= 2)
            fibTernaryIf(n - 1) + fibTernaryIf(n - 2)
        else
            1
    }

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
        fibTernaryIf(40)
    }

    [GenerateMicroBenchmark]
    public fun fibIfBenchmark() {
        fibIf(40)
    }
}



