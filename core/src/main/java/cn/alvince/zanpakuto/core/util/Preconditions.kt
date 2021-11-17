package cn.alvince.zanpakuto.core.util

inline fun checkIf(condition: Boolean, value: Boolean, lazyMessage: () -> Any) {
    if (condition) {
        check(value, lazyMessage)
    }
}

inline fun requireIf(condition: Boolean, value: Boolean, lazyMessage: () -> Any) {
    if (condition) {
        require(value, lazyMessage)
    }
}
