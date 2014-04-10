package ru.benchmark.functional;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.logic.BlackHole;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author evans
 *         27.03.14.
 */

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
}

