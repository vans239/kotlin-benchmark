package ru.benchmark.functional;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.logic.BlackHole;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author evans
 *         29.03.14.
 */
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class InliningDouble {
    public List<Double> objects = new ArrayList<Double>();

//    @Param({"10000", "100000", "1000000"})
    @Param("1000000")
    private int elemCount;
    private Random random = new Random();

    @Setup
    public void setup() {
        for (int i = 0; i < elemCount; ++i) {
            objects.add(random.nextDouble());
        }
    }

    @GenerateMicroBenchmark
    public void reduceSumMy(BlackHole bh){
        bh.consume(reduce(objects));

    }

    //exact realization of reduce in kotlin
    private double reduce(Iterable<Double> iterable) {
        Iterator<Double> iterator = iterable.iterator();
        if (!iterator.hasNext()) {
            throw new UnsupportedOperationException("Empty iterable can't be reduced");
        }

        double accumulator = iterator.next();
        while (iterator.hasNext()) {
            accumulator = sum(accumulator, iterator.next());
        }
        return accumulator;
    }

    private double sum(double accumulator, double next) {
        return accumulator + next;
    }
}
