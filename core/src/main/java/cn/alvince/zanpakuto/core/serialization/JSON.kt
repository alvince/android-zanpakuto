package cn.alvince.zanpakuto.core.serialization

import cn.alvince.zanpakuto.core.text.useNotEmpty
import cn.alvince.zanpakuto.core.time.Timestamp
import org.json.JSONArray
import org.json.JSONObject

@JsonDslMarker
inline class JSONCreator(private val json: JSONObject) {

    infix fun String.to(value: Boolean?) {
        this.useNotEmpty { json.putOpt(it, value) }
    }

    infix fun String.to(value: Number?) {
        this.useNotEmpty { json.putOpt(it, value) }
    }

    infix fun String.to(value: String?) {
        this.useNotEmpty { json.putOpt(it, value) }
    }

    infix fun String.to(time: Timestamp) {
        this.useNotEmpty { json.put(it, time.inMillis) }
    }

    infix fun String.to(jsonObj: JSONObject?) {
        this.useNotEmpty { json.putOpt(it, jsonObj) }
    }

    infix fun String.to(array: JSONArray) {
        this.useNotEmpty { json.putOpt(it, array) }
    }

    infix fun <T> String.to(array: Array<T>?) {
        this.useNotEmpty { json.putOpt(it, array?.toJSON()) }
    }

    infix fun <T> String.to(collection: Collection<T>?) {
        this.useNotEmpty { name ->
            collection?.takeIf { it.isNotEmpty() }
                ?.also {
                    json.putOpt(name, it.toJSON())
                }
        }
    }

    infix fun String.indeed(value: String?) {
        this.useNotEmpty { json.put(it, value.orEmpty()) }
    }

    infix fun String.boolArray(init: JSONArrayCreator<Boolean>.() -> Unit) {
        this.useNotEmpty { name ->
            json.putOpt(name, JSONArray().also { JSONArrayCreator<Boolean>(it).apply(init) })
        }
    }

    infix fun String.intArray(init: JSONArrayCreator<Int>.() -> Unit) {
        this.useNotEmpty { name ->
            json.putOpt(name, JSONArray().also { JSONArrayCreator<Int>(it).apply(init) })
        }
    }

    infix fun String.longArray(init: JSONArrayCreator<Long>.() -> Unit) {
        this.useNotEmpty { name ->
            json.putOpt(name, JSONArray().also { JSONArrayCreator<Long>(it).apply(init) })
        }
    }

    infix fun String.doubleArray(init: JSONArrayCreator<Int>.() -> Unit) {
        this.useNotEmpty { name ->
            json.putOpt(name, JSONArray().also { JSONArrayCreator<Int>(it).apply(init) })
        }
    }

    infix fun String.stringArray(init: JSONArrayCreator<String>.() -> Unit) {
        this.useNotEmpty { name ->
            json.putOpt(name, JSONArray().also { JSONArrayCreator<String>(it).apply(init) })
        }
    }

    fun <T> String.array(init: JSONArrayCreator<T>.() -> Unit) {
        this.useNotEmpty { name ->
            json.putOpt(name, JSONArray().also { JSONArrayCreator<T>(it).apply(init) })
        }
    }
}

@JsonDslMarker
inline class JSONArrayCreator<T>(private val json: JSONArray) {

    infix fun String.element(element: T) {
        json + element
    }

    infix fun String.elementNotNull(element: T?) {
        element?.also { json + it }
    }
}

/**
 * ```
 * json {
 *     "name1" to false
 *     "name2" to 1
 *     "name3" to "value"
 *     "name4" to Timestamp.now()
 *     "name5" to json {
 *         …
 *     }
 *     "name6" to jsonArrayOf(…)
 *
 *     /* put element force with non-null value */
 *     var nullable: String? = null
 *     "name7" indeed nullable // put "name7" with empty string
 * }
 * ```
 */
fun json(supplier: JSONCreator.() -> Unit): JSONObject {
    return JSONObject().also { JSONCreator(it).apply(supplier) }
}

/**
 * ```
 * jsonArray {
 *
 * }
 * ```
 */
fun <T> jsonArray(supplier: JSONArrayCreator<T>.() -> Unit): JSONArray {
    return JSONArray().also { JSONArrayCreator<T>(it).apply(supplier) }
}

/**
 * check [JSONArray] is null or empty
 */
fun JSONArray?.isNullOrEmpty(): Boolean = this == null || this.length() == 0

/**
 * Check [JSONArray] neither `null` nor empty
 */
fun JSONArray?.isNotEmpty(): Boolean = this?.length()?.let { it > 0 } == true

/**
 * Fast create [JSONArray] with elements and init block specified
 */
inline fun <T> jsonArrayOf(vararg element: T, action: JSONArray.() -> Unit = {}): JSONArray {
    return JSONArray().apply {
        element.forEach { put(it) }
        action.invoke(this)
    }
}

fun <T> Array<T>.toJSON(): JSONArray {
    return JSONArray().also { json ->
        this.mapTo(json) { it }
    }
}

fun <T> Iterable<T>.toJSON(): JSONArray {
    return JSONArray().also { json ->
        this.mapTo(json) { it }
    }
}

inline fun <T, R> Array<T>.mapTo(destination: JSONArray, transform: (T) -> R): JSONArray {
    for (item in this)
        destination.put(transform(item))
    return destination
}

inline fun <T, R> Iterable<T>.mapTo(destination: JSONArray, transform: (T) -> R): JSONArray {
    for (item in this)
        destination.put(transform(item))
    return destination
}

operator fun <T> JSONArray.plus(value: T): JSONArray = this.apply {
    when (value) {
        is Boolean -> put(value)
        is Int -> put(value)
        is Long -> put(value)
        is Float -> put(value.toDouble())
        is Double -> put(value)
        else -> put(value)
    }
}
