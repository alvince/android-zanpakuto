package cn.alvince.zanpakuto.core.time

/**
 * Created by alvince on 2021/12/28
 *
 * @author alvince.zy@gmail.com
 */
object TimeSource {

    private var outerTimeSource: (() -> Long)? = null

    fun setCustomTimeSource(provider: () -> Long) {
        outerTimeSource = provider
    }

    fun currentTimeMillis(): Long {
        return outerTimeSource?.invoke() ?: System.currentTimeMillis()
    }
}
