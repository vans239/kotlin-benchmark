package ru.benchmark.deltaBlue;

import org.openjdk.jmh.annotations.*;
import ru.benchmark.deltaBlue.code.DeltaBlue;

import java.util.concurrent.TimeUnit;

/**
 * @author evans
 *         27.03.14.
 */
@Warmup(iterations = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class DeltaBlueBenchmark {
    private static final int N = 100000;

    @GenerateMicroBenchmark
    public void projectionTest() {
        new DeltaBlue().projectionTest(N);
    }

    @GenerateMicroBenchmark
    public void chainTest() {
        new DeltaBlue().chainTest(N);
    }
}
