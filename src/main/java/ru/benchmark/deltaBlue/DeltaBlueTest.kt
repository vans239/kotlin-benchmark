package ru.benchmark.deltaBlue

import org.openjdk.jmh.annotations.GenerateMicroBenchmark
import ru.benchmark.fib.Fib


open class DeltaBlueTest {
    /*
        Kotlin tests
    */
    [GenerateMicroBenchmark]
    public fun projectionTestKt() {
        ru.benchmark.deltaBlue.kotlin.DeltaBlue().projectionTest(length)
    }

    [GenerateMicroBenchmark]
    public fun chainTestKt() {
        ru.benchmark.deltaBlue.kotlin.DeltaBlue().chainTest(length)
    }

    /*
        Java tests
    */
    [GenerateMicroBenchmark]
    public fun projectionTest() {
        ru.benchmark.deltaBlue.java.DeltaBlue().projectionTest(length)
    }

    [GenerateMicroBenchmark]
    public fun chainTest() {
        ru.benchmark.deltaBlue.java.DeltaBlue().chainTest(length)
        Fib().fibIf(1);
    }

    class  object {
        private val length = 2000
    }
}
