package cn.alvince.zanpakuto.core.net

import android.content.ContentResolver
import android.net.Uri

operator fun Uri.plus(param: Pair<String, String>): Uri {
    val (name, value) = param
    if (name.isEmpty()) {
        return this
    }
    return addParameters(name, value)
}

fun Uri.addParameters(vararg args: String): Uri {
    return this.buildUpon().addParameters(*args).build()
}

fun Uri.replaceParameter(name: String, replacement: String): Uri {
    return UriUtils.replaceParameter(this, name, replacement)
}

fun Uri.isContent(): Boolean = scheme == ContentResolver.SCHEME_CONTENT

fun Uri.isFile(): Boolean = scheme == ContentResolver.SCHEME_FILE

/**
 * Fast append path
 */
operator fun Uri.Builder.plus(newSegment: String): Uri.Builder {
    if (newSegment.isEmpty()) {
        return this
    }
    return this.appendPath(newSegment)
}

/**
 * Fast append query parameter
 */
operator fun Uri.Builder.plus(param: Pair<String, String>): Uri.Builder {
    val (name, value) = param
    if (name.isEmpty()) {
        return this
    }
    return this.appendQueryParameter(name, value)
}

/**
 * Append multiple query parameters
 */
fun Uri.Builder.addParameters(vararg args: String): Uri.Builder {
    return UriUtils.appendParameters(this, *args)
}

/**
 * Replace or append query parameter that name specified
 */
fun Uri.Builder.replaceParameter(name: String, replacement: String): Uri.Builder {
    if (name.isEmpty() || replacement.isEmpty()) {
        throw IllegalArgumentException("Invalid param name or value: empty")
    }
    val src = build()
    if (src.getQueryParameter(name) == null) {
        return this.apply { appendQueryParameter(name, replacement) }
    }
    return Uri.Builder().apply {
        scheme(src.scheme)
        authority(src.authority)
        path(src.path)
        src.queryParameterNames
            .filterNot { it == name }
            .forEach { n ->
                appendQueryParameter(n, src.getQueryParameter(n))
            }
        appendQueryParameter(name, replacement)
        encodedFragment(src.encodedFragment)
    }
}
