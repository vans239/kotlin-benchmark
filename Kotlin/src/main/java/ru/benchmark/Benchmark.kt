package ru.benchmark

import org.openjdk.jmh.runner.Runner
import java.util.concurrent.TimeUnit
import org.openjdk.jmh.runner.options.Options
import org.openjdk.jmh.runner.options.OptionsBuilder
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.runner.options.ChainedOptionsBuilder
import ru.benchmark.fib.FibKt
import org.openjdk.jmh.runner.options.VerboseMode
import ru.benchmark.deltaBlue.DeltaBlueTest
import ru.benchmark.functional.InliningDouble

/**
 * Evgeny Vanslov
 * vans239@gmail.com
 */
class Benchmark {
    class object {
        private val FORK_COUNT: Int = 1
        private val MEASURE_COUNT: Int = 10
        private val WARMUP_COUNT: Int = 10
        private val TIMEUNIT: TimeUnit = TimeUnit.MICROSECONDS

        private fun createEmpty(): ChainedOptionsBuilder {
            return OptionsBuilder()
                    .measurementIterations(MEASURE_COUNT)
            ?.forks(FORK_COUNT)
            ?.warmupIterations(WARMUP_COUNT)
            ?.timeUnit(TIMEUNIT)
            ?.mode(Mode.AverageTime)!!
        }
        public fun create(clazz: Class<*>): Options =
                createEmpty().verbosity(VerboseMode.EXTRA)?.include(".*" + clazz.getSimpleName() + ".*")?.build()!!

        public fun create(regexp: String): Options = createEmpty().include(regexp)?.build()!!
    }
}

//-XX:+PrintCompilation
//-server -XX:+UnlockDiag  nosticVMOptions '-XX:CompileCommand=print,*Main.main'
//    -XX:+PrintCompilation  -XX:+UnlockDiagnosticVMOptions -XX:+PrintAssembly
fun main(args: Array<String>) {
    Thread.sleep(1500)
   Runner(Benchmark.create(javaClass<InliningDouble>())).run()
}