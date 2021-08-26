package cn.alvince.zanpakuto.rxjava2.ktx

import androidx.collection.SparseArrayCompat

/**
 * Created by alvince on 2021/7/6
 *
 * @author alvince.zy@gmail.com
 */
internal class RegistryHolder<T> {

    private val _pool = SparseArrayCompat<T>()

    operator fun get(key: Int): T? {
        if (_pool.containsKey(key)) {
            return _pool[key]
        }
        return null
    }

    fun put(key: Int, value: T) {
        synchronized(this) { _pool.put(key, value) }
    }

    fun clear(key: Int) {
        synchronized(this) {
            if (_pool.containsKey(key)) {
                _pool.remove(key)
            }
        }
    }
}
