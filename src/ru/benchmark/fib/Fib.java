package ru.benchmark.fib;

import org.openjdk.jmh.annotations.GenerateMicroBenchmark;

/**
 * Evgeny Vanslov
 * vans239@gmail.com
 */
public class Fib {

    public static int fibStaticTernaryIf(int n) {
        return (n >= 2) ? fibStaticTernaryIf(n - 1) + fibStaticTernaryIf(n - 2) : 1;
    }

    public static int fibStaticIf(int n) {
        if (n >= 2)
            return fibStaticIf(n - 1) + fibStaticIf(n - 2);
        else
            return 1;
    }

    int fibTernaryIf(int n) {
        return (n >= 2) ? fibTernaryIf(n - 1) + fibTernaryIf(n - 2) : 1;
    }

    int fibIf(int n) {
        if (n >= 2)
            return fibIf(n - 1) + fibIf(n - 2);
        else
            return 1;
    }

    @GenerateMicroBenchmark
    public void test(){

    }
}


