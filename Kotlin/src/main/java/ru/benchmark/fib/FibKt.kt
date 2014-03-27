package ru.benchmark.fib
import org.openjdk.jmh.annotations.GenerateMicroBenchmark
import java.util.concurrent.TimeUnit
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.Warmup
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.OutputTimeUnit

[Warmup(iterations = 5, timeUnit = TimeUnit.SECONDS)]
[Measurement(iterations = 5, timeUnit = TimeUnit.SECONDS)]
[Fork(1)]
[BenchmarkMode(Mode.AverageTime)]
[OutputTimeUnit(TimeUnit.MILLISECONDS)]
open class FibKt() {
    class object {
        private val N = 40

        public fun fibStaticTernaryIf(n: Int): Int {
            return if (n >= 2)
                fibStaticTernaryIf(n - 1) + fibStaticTernaryIf(n - 2)
            else
                1
        }

        public fun fibStaticIf(n: Int): Int {
            if (n >= 2)
                return fibStaticIf(n - 1) + fibStaticIf(n - 2)
            else
                return 1
        }
    }

    public fun fibIf(n: Int): Int {
        if (n >= 2)
            return fibIf(n - 1) + fibIf(n - 2)
        else
            return 1
    }

    public fun fibTernaryIf(n: Int): Int {
        return if (n >= 2)
            fibTernaryIf(n - 1) + fibTernaryIf(n - 2)
        else
            1
    }

    [GenerateMicroBenchmark]
    public fun fibStaticTernaryBenchmarkKt() : Int {
        return FibKt.fibStaticTernaryIf(N)
    }

    [GenerateMicroBenchmark]
    public fun fibStaticIfBenchmarkKt() : Int {
        return FibKt.fibStaticIf(N)
    }

    [GenerateMicroBenchmark]
    public fun fibTernaryBenchmarkKt(): Int {
        return FibKt().fibTernaryIf(N)
    }

    [GenerateMicroBenchmark]
    public fun fibIfBenchmarkKt() : Int{
        return FibKt().fibIf(N)
    }

}



