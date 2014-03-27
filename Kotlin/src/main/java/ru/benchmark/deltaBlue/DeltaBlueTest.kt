package ru.benchmark.deltaBlue

import org.openjdk.jmh.annotations.GenerateMicroBenchmark


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

    class  object {
        private val length = 100000
    }
}
