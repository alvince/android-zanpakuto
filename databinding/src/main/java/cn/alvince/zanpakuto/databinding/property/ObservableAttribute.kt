@file:Suppress("unused")

package cn.alvince.zanpakuto.databinding.property

import androidx.databinding.BaseObservable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Observable field delegate for data-binding in [BaseObservable]
 *
 * Created by alvince on 2021/9/8
 *
 * @author alvince.zy@gmail.com
 */
class ObservableDelegate<T>(
    private val fieldId: Int,
    initialValue: T,
    private val notifyEquivalent: Boolean = false,
    private val afterChange: ((T) -> Unit)? = null,
) : ReadWriteProperty<BaseObservable, T> {

    private var value: T = initialValue

    override fun getValue(thisRef: BaseObservable, property: KProperty<*>): T = value

    override fun setValue(thisRef: BaseObservable, property: KProperty<*>, value: T) {
        val valueNoChange = value == this.value
        if (valueNoChange && !notifyEquivalent) {
            return
        }
        this.value = value
        thisRef.notifyPropertyChanged(fieldId)

        if (valueNoChange) {
            return
        }
        afterChange?.invoke(value)
    }
}

inline fun <reified T> observableField(fieldId: Int, initialValue: T): ObservableDelegate<T> = ObservableDelegate(fieldId, initialValue)

inline fun <reified T> observableField(fieldId: Int, initialValue: T, noinline afterChange: ((T) -> Unit)): ObservableDelegate<T> =
    ObservableDelegate(fieldId, initialValue, afterChange = afterChange)

inline fun <reified T> nullableField(fieldId: Int): ObservableDelegate<T?> = ObservableDelegate(fieldId, null)

inline fun <reified T> nullableField(fieldId: Int, noinline afterChange: (T?) -> Unit): ObservableDelegate<T?> =
    ObservableDelegate(fieldId, null, afterChange = afterChange)

fun booleanField(fieldId: Int, initialValue: Boolean = false): ObservableDelegate<Boolean> = ObservableDelegate(fieldId, initialValue)

fun booleanField(fieldId: Int, initialValue: Boolean = false, afterChange: (Boolean) -> Unit): ObservableDelegate<Boolean> =
    ObservableDelegate(fieldId, initialValue, afterChange = afterChange)

fun intField(fieldId: Int, initialValue: Int = 0): ObservableDelegate<Int> = ObservableDelegate(fieldId, initialValue)

fun intField(fieldId: Int, initialValue: Int = 0, afterChange: (Int) -> Unit): ObservableDelegate<Int> =
    ObservableDelegate(fieldId, initialValue, afterChange = afterChange)

fun longField(fieldId: Int, initialValue: Long = 0L): ObservableDelegate<Long> = ObservableDelegate(fieldId, initialValue)

fun longField(fieldId: Int, initialValue: Long = 0L, afterChange: (Long) -> Unit): ObservableDelegate<Long> =
    ObservableDelegate(fieldId, initialValue, afterChange = afterChange)

fun floatField(fieldId: Int, initialValue: Float = 0F): ObservableDelegate<Float> = ObservableDelegate(fieldId, initialValue)

fun floatField(fieldId: Int, initialValue: Float = 0F, afterChange: (Float) -> Unit): ObservableDelegate<Float> =
    ObservableDelegate(fieldId, initialValue, afterChange = afterChange)

fun doubleField(fieldId: Int, initialValue: Double = 0.0): ObservableDelegate<Double> = ObservableDelegate(fieldId, initialValue)

fun doubleField(fieldId: Int, initialValue: Double = 0.0, afterChange: (Double) -> Unit): ObservableDelegate<Double> =
    ObservableDelegate(fieldId, initialValue, afterChange = afterChange)
