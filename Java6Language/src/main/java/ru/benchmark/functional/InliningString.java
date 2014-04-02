package ru.benchmark.functional;

import org.jetbrains.annotations.NotNull;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.logic.BlackHole;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author evans
 *         02.04.14.
 */
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class InliningString {
    public List<String> objects = new ArrayList<String>();

    //        @Param({"10000", "100000", "1000000"})
    @Param("1000000")
    private int elemCount;

    @Setup
    public void setup() {
        for (int i = 0; i < elemCount; ++i) {
            objects.add("");
        }
    }

//    @GenerateMicroBenchmark
    public void reduceSumMy(BlackHole bh) {
        bh.consume(reduce(objects));

    }

    @GenerateMicroBenchmark
    public void mapIdMy(BlackHole bh) {
        bh.consume(map(objects));
    }

    private Iterable<String> map(Iterable<String> iterable) {
        ArrayList<String> list = new ArrayList<String>();
        for (String item : iterable) {
            list.add(id(item));
        }
        return list;
    }

    private String id(String s) {
        return s;
    }

    //exact realization of reduce in kotlin
    private String reduce(Iterable<String> iterable) {
        Iterator<String> iterator = iterable.iterator();
        if (!iterator.hasNext()) {
            throw new UnsupportedOperationException("Empty iterable can't be reduced");
        }
        String accumulator = iterator.next();
        while (iterator.hasNext()) {
            accumulator = sum(accumulator, iterator.next());
        }
        return accumulator;
    }

    private String sum(String accumulator, String next) {
        return accumulator + next;
    }
}
