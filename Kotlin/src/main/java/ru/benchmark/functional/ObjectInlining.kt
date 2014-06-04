package ru.benchmark.functional

import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.Scope
import java.util.ArrayList
import java.util.LinkedList
import org.openjdk.jmh.annotations.Param
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.logic.BlackHole
import org.openjdk.jmh.annotations.GenerateMicroBenchmark
import org.openjdk.jmh.annotations.Warmup
import java.util.concurrent.TimeUnit
import org.openjdk.jmh.annotations.Measurement
import kotlin.properties.ReadWriteProperty
import org.openjdk.jmh.annotations.OutputTimeUnit

/**
 * @author evans
 * 25.05.14.
 */
State(Scope.Thread)
OutputTimeUnit(TimeUnit.NANOSECONDS)
public open class ObjectInlining {
    class ExampleObservable1 {
        var c: String by observable {
            (desc, old, new) ->
            true
        }
    }
    class ExampleObservable2 {
        var c: String by observable {
            (desc, old, new) ->
            false
        }
    }
    class ExampleObservable3 {
        var c: String by observable {
            (desc, old, new) ->
            true //old.isEmpty()
        }
    }
    class ExampleObservable4 {
        var c: String by observable {
            (desc, old, new) ->
            !old.isEmpty()
        }
    }

    class ExampleObservable5 {
        var c: String by observable {
            (desc, old, new) ->
            true
        }
    }

    //по-разному срабатывают оптимизации у testManySameBody, testManySameClass
    val a = ExampleObservable1()
    val b = ExampleObservable2()
    val c = ExampleObservable3()
    val d = ExampleObservable4()
    val a2 = ExampleObservable1() //same class as a
    val a3 = ExampleObservable5() //same body as a


    //Slow work
//    GenerateMicroBenchmark
    public fun testManyDiffClasses() {
        a.c = "abc"
        b.c = "abc"
        c.c = "abc"
    }

    //Slow work
    GenerateMicroBenchmark
    public fun testManySameBody() {
        a.c = "abc"
        c.c = "abc"
        a3.c = "abc"

        //        d.c = "abc"
    }

    //Fast work
    GenerateMicroBenchmark
    public fun testManySameClass() {
        a.c = "abc"
        c.c = "abc"
        a2.c = "abc"
    }

//    GenerateMicroBenchmark
    public fun inlinedVariantOfObservable(bh: BlackHole) {
        a.c = "abc"
    }

    //Fast work
//    GenerateMicroBenchmark
    public fun testManyOnly2() {
        a.c = "abc"
        b.c = "abc"
    }
}

inline fun observable(onChange: (name: PropertyMetadata, oldValue: String, newValue: String) -> Boolean)
        :ReadWriteProperty<Any?, String> {
    return object : ReadWriteProperty<Any?, String> {
        private var value = ""

        public override fun get(thisRef: Any?, desc: PropertyMetadata): String {
            return value
        }

        public override fun set(thisRef: Any?, desc: PropertyMetadata, value: String) {
            if (onChange(desc, this.value, value)) {
                this.value = value
            }
        }
    }
}



