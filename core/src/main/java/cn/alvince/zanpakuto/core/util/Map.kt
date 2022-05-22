package cn.alvince.zanpakuto.core.util

import androidx.annotation.RestrictTo
import androidx.collection.ArrayMap
import java.util.Collections

@DslMarker
annotation class MapMaker

@RestrictTo(RestrictTo.Scope.LIBRARY)
@MapMaker
@JvmInline
value class MapBuilder<K, V>(internal val map: MutableMap<K, V>) {

    infix fun K.to(v: V) {
        map[this] = v
    }

    infix fun K.toNotNull(value: V?) {
        value?.also { map[this] = it }
    }
}

fun <K, V> singleMapOf(pair: Pair<K, V>): Map<K, V> = Collections.singletonMap(pair.first, pair.second)

fun <K, V> lightMapOf(pair1: Pair<K, V>, pair2: Pair<K, V>): Map<K, V> =
    ArrayMap<K, V>(2).apply {
        put(pair1.first, pair1.second)
        put(pair2.first, pair2.second)
    }

fun <K, V> lightMapOf(pair1: Pair<K, V>, pair2: Pair<K, V>, pair3: Pair<K, V>): Map<K, V> =
    ArrayMap<K, V>(3).apply {
        put(pair1.first, pair1.second)
        put(pair2.first, pair2.second)
        put(pair3.first, pair3.second)
    }

fun <K, V> lightMapOf(pair1: Pair<K, V>, pair2: Pair<K, V>, pair3: Pair<K, V>, pair4: Pair<K, V>): Map<K, V> =
    ArrayMap<K, V>(4).also {
        it[pair1.first] = pair1.second
        it[pair2.first] = pair2.second
        it[pair3.first] = pair3.second
        it[pair4.first] = pair4.second
    }

fun <K, V> lightMapOf(pair1: Pair<K, V>, pair2: Pair<K, V>, pair3: Pair<K, V>, pair4: Pair<K, V>, pair5: Pair<K, V>): Map<K, V> =
    ArrayMap<K, V>(5).also {
        it[pair1.first] = pair1.second
        it[pair2.first] = pair2.second
        it[pair3.first] = pair3.second
        it[pair4.first] = pair4.second
        it[pair5.first] = pair5.second
    }

fun <K, V> lightMapOf(vararg pairs: Pair<K, V>): Map<K, V> =
    pairs.associateTo(ArrayMap(pairs.size)) { it }

fun <K, V> lightMap(supplier: MapBuilder<K, V>.() -> Unit): MutableMap<K, V> {
    return MapBuilder(ArrayMap<K, V>()).apply(supplier).map
}

operator fun <K, V> MutableMap<K, V>.plus(pair: Pair<K, V>): Map<K, V> =
    this.apply {
        put(pair.first, pair.second)
    }
