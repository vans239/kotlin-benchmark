package org
import java.util.ArrayList

class MyHashMap<K : Any, V : Any> {
    private val initSize = 10
    private val factor = 0.75
    private val growFactor = 2
    private var table = Array<List<Entry<K, V>>?>(initSize) { i -> null }
    private var size = 0

    public fun put(key: K, value: V): Unit {
        resize()
        val indexOf = indexOf(key)
        val iter = table[indexOf]
        val entry = Entry(key, value)
        if (iter != null ) {
            if(iter.find { curr -> curr.key.equals(key) } == null){
                ++size
            }
            table[indexOf] = iter.filter({ curr -> !curr.key.equals(key) }).plus(entry)
        } else {
            table[indexOf] = ArrayList<Entry<K, V>>().plus(entry)
        }
    }

    public fun remove(key: K): Unit {
        val indexOf = indexOf(key)
        table[indexOf] = table[indexOf]?.filter({ entry -> !entry.key.equals(key)})
    }

    public fun get(key: K): V? = table[indexOf(key)]?.find({ entry -> entry.key.equals(key) })?.value
    public fun contains(k: K): Boolean = table[indexOf(k)] != null
    public fun size(): Int = size
    public fun isEmpty(): Boolean = size == 0

    private fun indexOf(key: K) = hash(key) % table.size
    private fun hash(key: K) = key.hashCode()
    private fun resize() {
        if (size < table.size * factor) {
            return
        }
        println("Rehash")
        val oldTable = table
        table = Array<List<Entry<K, V>>?>(oldTable.size * growFactor) { i -> null }
        for (iter in oldTable) {
            if (iter != null) {
                for (entry in iter) {
                    put(entry.key, entry.value)
                }
            }
        }
    }

    private data class Entry<K, V> (val key: K, val value: V)

}

/*fun main(args: Array<String>) {
    val map = MyHashMap<Int, String>()
    assert(map.size() == 0)
    assert(map.isEmpty())


    map.put(1, "one")
    assert(map.contains(1))
    assert(map.get(1).equals("one"))

    for(i in 1..10000){
        map.put(i, i.toString())
    }
    assert(map.get(500).equals("500"))
    assert(map.size() == 10000)
}*/

//todo http://jetbrains.github.io/kotlin/versions/snapshot/apidocs/jet/Boolean.html where is doc?
//todo why lazyval private?