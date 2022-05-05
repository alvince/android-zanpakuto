package cn.alvince.zanpakuto.serialization.gson

import cn.alvince.zanpakuto.core.serialization.JsonDslMarker
import cn.alvince.zanpakuto.core.text.useNotEmpty
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import org.json.JSONObject

@JsonDslMarker
inline class JsonObjectAssembler(private val json: JsonObject) {

    infix fun String.to(value: Boolean?) {
        this.useNotEmpty { json.addProperty(it, value == true) }
    }

    infix fun String.to(value: Char) {
        this.useNotEmpty { json.addProperty(it, value) }
    }

    infix fun String.to(value: Number) {
        this.useNotEmpty { json.addProperty(it, value) }
    }

    infix fun String.to(value: String) {
        this.useNotEmpty { json.addProperty(it, value) }
    }

    infix fun String.to(obj: JsonElement) {
        this.useNotEmpty { json.add(it, obj) }
    }

    infix fun String.toNonNull(value: Boolean?) {
        this.useNotEmpty {
            value?.also { json.addProperty(this, it) }
        }
    }

    infix fun String.toNonNull(value: Char?) {
        this.useNotEmpty { name ->
            value?.also { json.addProperty(name, it) }
        }
    }

    infix fun String.toNonNull(value: Number?) {
        this.useNotEmpty { name ->
            value?.also { json.addProperty(name, it) }
        }
    }

    infix fun String.toNonNull(value: String?) {
        this.useNotEmpty { name ->
            value?.also { json.addProperty(name, it) }
        }
    }

    infix fun String.toNonNull(obj: JsonElement?) {
        this.useNotEmpty {
            obj?.also { json.add(this, it) }
        }
    }

    infix fun <T> String.array(init: JsonArrayAssembler<T>.() -> Unit) {

    }
}

@JsonDslMarker
inline class JsonArrayAssembler<T>(private val json: JsonArray) {

    infix fun String.element(value: T) {
        json + value
    }

    infix fun String.elementNotNull(value: T?) {
        value?.also { json + it }
    }
}

/**
 * ```
 * val json: JsonObject = gson {
 *     "name1" to true
 *     "name2" to 'c'
 *     "name3" to 2
 *     "name4" to "text"
 *     "name5" to gson {
 *         …
 *     }
 *     // add only non-null
 *     var value: String? = null
 *     "name6" toNonNull value
 * }
 * ```
 */
fun gson(supplier: JsonObjectAssembler.() -> Unit): JsonObject {
    return JsonObject().also { JsonObjectAssembler(it).apply(supplier) }
}

fun <T> gsonArray(supplier: JsonArrayAssembler<T>.() -> Unit): JsonArray {
    return JsonArray().also { JsonArrayAssembler<T>(it).apply(supplier) }
}

fun JsonObject.objectOf(name: String): JsonObject? {
    return elementOf(name)
        ?.takeIf { it.isJsonObject }
        ?.asJsonObject
}

fun JsonObject.arrayOf(name: String): JsonArray? {
    return elementOf(name)
        ?.takeIf { it.isJsonArray }
        ?.asJsonArray
}

fun JsonObject.primitiveOf(name: String): JsonPrimitive? {
    return elementOf(name)
        ?.takeIf { it.isJsonPrimitive }
        ?.asJsonPrimitive
}

fun JsonObject.intOf(name: String, defaultValue: Int = 0): Int {
    return primitiveOf(name)
        ?.takeIf { it.isNumber }
        ?.asInt
        ?: defaultValue
}

fun JsonObject.boolOf(name: String, defaultValue: Boolean = false): Boolean {
    return primitiveOf(name)
        ?.takeIf { it.isBoolean }
        ?.asBoolean
        ?: defaultValue
}

fun JsonObject.stringOf(name: String, strict: Boolean = true): String? = primitiveOf(name)?.stringOrNull(strict)

/**
 * Get child element in [JsonObject]
 *
 * @param path key name, or `key:chid-key[:child-key[…]]`
 */
fun JsonObject.elementOf(path: String): JsonElement? {
    if (!path.contains(":")) {
        return this.get(path)
    }
    val keyChain = path.split(":")
    var target: JsonElement? = this
    var dep = 0
    do {
        target = (target as? JsonObject)?.get(keyChain[dep++])
    } while (dep < keyChain.size)
    return target
}

inline fun <R : Any> JsonObject.mapArrayOf(name: String, transform: (JsonElement) -> R?): List<R> {
    return arrayOf(name)?.mapNotNull(transform) ?: emptyList()
}

fun JsonPrimitive.stringOrNull(strict: Boolean = true): String? = this.takeIf { !strict || it.isString }?.asString

fun JsonElement.primitiveOrNull(): JsonPrimitive? = this.takeIf { it.isJsonPrimitive }?.asJsonPrimitive

fun JsonElement.stringOrNull(): String? = this.primitiveOrNull()?.stringOrNull()

inline fun JsonElement.stringOr(strict: Boolean = true, fallback: () -> String): String {
    return this.primitiveOrNull()?.stringOrNull(strict) ?: fallback()
}

///////////////////////////////////////////////////////////////////////////
// JsonArray ext
///////////////////////////////////////////////////////////////////////////

operator fun <T> JsonArray.plus(value: T): JsonArray = this.apply {
    when (value) {
        is Boolean -> add(JsonPrimitive(value))
        is Number -> add(JsonPrimitive(value))
        is Char -> add(JsonPrimitive(value))
        is String -> add(JsonPrimitive(value))
        is JsonElement -> add(value)
        else -> throw IllegalArgumentException("unsupported type")
    }
}

///////////////////////////////////////////////////////////////////////////
// Gson ext utils
///////////////////////////////////////////////////////////////////////////

inline fun <reified T> String.parseWithGson(gson: Gson): T = gson.fromJson(this, T::class.java)

inline fun <reified T> JSONObject.convertWithGson(gson: Gson): T = gson.fromJson(this.toString(), T::class.java)
