package ru.benchmark.functional;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.logic.BlackHole;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author evans 27.03.14.
 */

@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class CollectionLooping {
    public List<Double> objects = new ArrayList<Double>();

    @Param({"1000", "10000", "100000"})
    private int elemCount;
    private Random random = new Random();

    @Setup
    public void setup(){
        for (int i = 0; i < elemCount; ++i) {
            objects.add(random.nextDouble());
        }
    }

//box unbox sum
    @GenerateMicroBenchmark
    public void passObjectsIndexed(BlackHole bh) {
        double sum= 0;
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < objects.size(); ++i) {
            sum += objects.get(i);
        }
        bh.consume(sum);
    }


    @GenerateMicroBenchmark
    public void passObjectsForEachSimple(BlackHole bh) {
        double sum= 0;
        for (Double object : objects) {
            sum += object;
        }
        bh.consume(sum);
    }

    @GenerateMicroBenchmark
    public void passObjectsIterator(BlackHole bh) {
        double sum= 0;
        Iterator<Double> it = objects.iterator();
        //noinspection WhileLoopReplaceableByForEach
        while (it.hasNext()) {
            sum += it.next();
        }
        bh.consume(sum);
    }



    /*This code will be optimized */
    public void passObjectsIndexed2(BlackHole bh) {
        int count = 0;
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < objects.size(); ++i) {
            ++count;
        }
        bh.consume(count);
    }
}
