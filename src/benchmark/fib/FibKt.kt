package org

import org.openjdk.jmh.annotations.GenerateMicroBenchmark

open class FibKt() {
    class object {
        fun fibStaticTernaryIf(n: Int): Int {
            return if (n >= 2)
                fibStaticTernaryIf(n - 1) + fibStaticTernaryIf(n - 2)
            else
                1
        }

        fun fibStaticIf(n: Int): Int {
            if (n >= 2)
                return fibStaticIf(n - 1) + fibStaticIf(n - 2)
            else
                return 1
        }
    }

    fun fibIf(n: Int): Int {
        if (n >= 2)
            return fibIf(n - 1) + fibIf(n - 2)
        else
            return 1
    }

    fun fibTernaryIf(n: Int): Int {
        return if (n >= 2)
            fibTernaryIf(n - 1) + fibTernaryIf(n - 2)
        else
            1
    }

    [GenerateMicroBenchmark]
    public fun fibStaticTernaryBenchmark() {
        FibKt.fibStaticTernaryIf(40)
    }

    [GenerateMicroBenchmark]
    public fun fibStaticIfBenchmark() {
        FibKt.fibStaticIf(40)
    }

    [GenerateMicroBenchmark]
    public fun fibTernaryBenchmark() {
        FibKt().fibTernaryIf(40)
    }

    [GenerateMicroBenchmark]
    public fun fibIfBenchmark() {
        FibKt().fibIf(40)
    }
}





var warmup = true
fun warmUp(f: () -> Unit): Unit {
    var warmUpIters = 1 //it's equal like 2^40 -> it's enough
    if (warmup) {
        for (i in 1..warmUpIters) {
            f()
            print(".")
        }
    }
}

//-XX:+PrintCompilation
//-server -XX:+UnlockDiag  nosticVMOptions '-XX:CompileCommand=print,*Main.main'
//    -XX:+PrintCompilation  -XX:+UnlockDiagnosticVMOptions -XX:+PrintAssembly
fun main(args: Array<String>) {
    warmUp { FibKt.fibStaticTernaryIf(40) }
    var fibKt = FibKt()

    var start = System.currentTimeMillis();
    FibKt.fibStaticTernaryIf(40);
    System.out.println("Kotlin(static ternary): " + (System.currentTimeMillis() - start) + "ms\n")

    warmUp { FibKt.fibStaticIf(40) }

    start = System.currentTimeMillis()
    FibKt.fibStaticIf(40)
    System.out.println("Kotlin(static if): " + (System.currentTimeMillis() - start) + "ms\n")

    warmUp { fibKt.fibTernaryIf(40) }

    start = System.currentTimeMillis();
    fibKt.fibTernaryIf(40);
    System.out.println("Kotlin(instance ternary): " + (System.currentTimeMillis() - start) + "ms\n");

    warmUp { fibKt.fibIf(40) }

    start = System.currentTimeMillis();
    fibKt.fibIf(40);
    System.out.println("Kotlin(instance if): " + (System.currentTimeMillis() - start) + "ms\n");
}

