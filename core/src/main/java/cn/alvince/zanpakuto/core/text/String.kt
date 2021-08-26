package cn.alvince.zanpakuto.core.text

fun String?.takeIfNotEmpty(): String? = this?.takeIf { it.isNotEmpty() }

fun String?.orDefault(fallbackValue: String): String = this ?: fallbackValue

inline fun String?.orDefault(fallbackValue: () -> String): String {
    return this ?: fallbackValue()
}
