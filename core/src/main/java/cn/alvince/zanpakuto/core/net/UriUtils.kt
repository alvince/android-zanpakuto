package cn.alvince.zanpakuto.core.net

import android.net.Uri

object UriUtils {

    @JvmStatic
    fun appendParameter(src: Uri, name: String, param: String): Uri {
        if (name.isEmpty() || param.isEmpty()) {
            return src
        }
        if (!src.getQueryParameter(name).isNullOrEmpty()) {
            return src
        }
        return src.buildUpon()
            .apply {
                appendQueryParameter(name, param)
            }
            .build()
    }

    @JvmStatic
    fun appendParameters(src: Uri.Builder, vararg args: String): Uri.Builder {
        if (args.isEmpty()) {
            return src
        }
        val len = args.size
        val size = if (len and 1 == 1) (len - 1) / 2 else len / 2
        if (size < 1) {
            return src
        }
        var result: Uri.Builder = src
        for (i in 0 until size) {
            val name = args[i]
            if (name.isEmpty()) {
                continue
            }
            result += name to args[i + 1]
        }
        return result
    }

    @JvmStatic
    fun replaceParameter(src: Uri, name: String, replacement: String): Uri {
        if (name.isEmpty()) {
            return src
        }
        return Uri.Builder()
            .apply {
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
            .build()
    }
}


