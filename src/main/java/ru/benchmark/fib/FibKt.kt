package ru.benchmark.fib
import org.openjdk.jmh.annotations.GenerateMicroBenchmark
import ru.benchmark.fib.Fib

open class FibKt() {
    class object {
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
}



