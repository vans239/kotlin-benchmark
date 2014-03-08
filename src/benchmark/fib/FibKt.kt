package benchmark.fib


open class FibKt() {

    class object {
        fun fibStaticIf(n: Int): Int {
            if (n >= 2)
                return fibStaticIf(n - 1) + fibStaticIf(n - 2)
            else
                return 1
        }

        fun fibStaticTernaryIf(n: Int): Int {
            return if (n >= 2)
                fibStaticTernaryIf(n - 1) + fibStaticTernaryIf(n - 2)
            else
                1
        }
    }

    //-server -XX:+UnlockDiag  nosticVMOptions '-XX:CompileCommand=print,*Main.main'
//    -XX:+PrintCompilation  -XX:+UnlockDiagnosticVMOptions -XX:+PrintAssembly
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
fun main(args: Array<String>) {
    warmUp { FibKt.fibStaticTernaryIf(40) }

    var start = System.currentTimeMillis();
    FibKt.fibStaticTernaryIf(40);
    System.out.println("Kotlin(static ternary): " + (System.currentTimeMillis() - start) + "ms")

    warmUp { Fib.fibStaticIf(40) }

    start = System.currentTimeMillis()
    Fib.fibStaticIf(40)
    System.out.println("Kotlin(static if): " + (System.currentTimeMillis() - start) + "ms")

    var fib = FibKt()
    warmUp { fib.fibTernaryIf(40) }

    start = System.currentTimeMillis();
    fib.fibTernaryIf(40);
    System.out.println("Kotlin(instance ternary): " + (System.currentTimeMillis() - start) + "ms");


    warmUp { fib.fibIf(40) }

    start = System.currentTimeMillis();
    fib.fibIf(40);
    System.out.println("Kotlin(instance if): " + (System.currentTimeMillis() - start) + "ms");
}

