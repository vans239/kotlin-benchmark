package ru.benchmark.fib;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * Evgeny Vanslov
 * vans239@gmail.com
 */
@Warmup(iterations = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class Fib {
    private static final int N = 40;

    public static int fibStaticTernaryIf(int n) {
        return (n >= 2) ? fibStaticTernaryIf(n - 1) + fibStaticTernaryIf(n - 2) : 1;
    }

    public static int fibStaticIf(int n) {
        if (n >= 2)
            return fibStaticIf(n - 1) + fibStaticIf(n - 2);
        else
            return 1;
    }

    public int fibTernaryIf(int n) {
        return (n >= 2) ? fibTernaryIf(n - 1) + fibTernaryIf(n - 2) : 1;
    }

    public int fibIf(int n) {
        if (n >= 2)
            return fibIf(n - 1) + fibIf(n - 2);
        else
            return 1;
    }

    @GenerateMicroBenchmark
    public int fibStaticTernaryBenchmark(){
        return Fib.fibStaticTernaryIf(N);
    }

    @GenerateMicroBenchmark
    public int fibStaticIfBenchmark(){
        return Fib.fibStaticIf(N);
    }

    @GenerateMicroBenchmark
    public int fibTernaryBenchmark(){
        return new Fib().fibTernaryIf(N);
    }

    @GenerateMicroBenchmark
    public int fibIfBenchmark(){
        return new Fib().fibIf(N);
    }
}


