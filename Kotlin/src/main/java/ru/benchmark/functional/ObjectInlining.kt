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
//    GenerateMicroBenchmark
    public fun inlinedVariantOfObservable(bh: BlackHole) {
        class ExampleObservable {
            var c: String by observable {
                (desc, old, new) ->
                true
            }
        }

        val e = ExampleObservable()
        e.c = "abc"
    }

//    GenerateMicroBenchmark
    //todo code compiles successfully but throws exception in runtime
    public fun testmany(bh: BlackHole) {
        val e = object {
            var c: String by observable {
                (desc, old, new) ->
                bh.consume(new)
                true
            }
        }
        e.c = "abc"
    }

    GenerateMicroBenchmark
    public fun testmany2(bh: BlackHole) {
        val a = object {
            var c: String by observable {
                (desc, old, new) ->
                true
            }
        }
        val b = object {
            var c: String by observable {
                (desc, old, new) ->
                false
            }
        }
        val c = object {
            var c: String by observable {
                (desc, old, new) ->
                old.isEmpty()
            }
        }
        val d = object {
            var c: String by observable {
                (desc, old, new) ->
                !old.isEmpty()
            }
        }
        a.c = "abc"
        b.c = "abc"
        c.c = "abc"
        d.c = "abc"
    }
}

inline fun observable(onChange: (name: PropertyMetadata, oldValue: String, newValue: String) -> Boolean): ReadWriteProperty<Any?, String> {
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



