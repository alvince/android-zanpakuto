package cn.alvince.zanpakuto.core.time

import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt
import kotlin.math.sign

inline class Duration(private val rawValue: Long) : Comparable<Duration> {

    val inMillis: Long get() = if (isInMillis() && isFinite()) value else toLong(TimeUnit.MILLISECONDS)

    val inSeconds: Long get() = toLong(TimeUnit.SECONDS)

    val inMinutes: Long get() = toLong(TimeUnit.MINUTES)

    val inHours: Long get() = toLong(TimeUnit.HOURS)

    val inDays: Long get() = toLong(TimeUnit.DAYS)

    val absoluteValue: Duration get() = if (isNegative()) -this else this

    @PublishedApi
    internal val hoursComponent: Int
        get() = if (isInfinite()) 0 else (inHours % 24).toInt()

    @PublishedApi
    internal val minutesComponent: Int
        get() = if (isInfinite()) 0 else (inMinutes % 60).toInt()

    @PublishedApi
    internal val secondsComponent: Int
        get() = if (isInfinite()) 0 else (inSeconds % 60).toInt()

    @PublishedApi
    internal val nanosecondsComponent: Int
        get() = when {
            isInfinite() -> 0
            isInMillis() -> millisToNanos(value % 1_000).toInt()
            else -> (value % 1_000_000_000).toInt()
        }

    private val value: Long get() = rawValue shr 1
    private inline val unitDiscriminator: Int get() = rawValue.toInt() and 1
    private val storageUnit get() = if (isInNanos()) TimeUnit.NANOSECONDS else TimeUnit.MILLISECONDS

//    init {
//        if (isInNanos()) {
//            if (value !in -MAX_NANOS..MAX_NANOS) throw AssertionError("$value ns is out of nanoseconds range")
//        } else {
//            if (value !in -MAX_MILLIS..MAX_MILLIS) throw AssertionError("$value ms is out of milliseconds range")
//            if (value in -MAX_NANOS_IN_MILLIS..MAX_NANOS_IN_MILLIS) throw AssertionError("$value ms is denormalized")
//        }
//    }

    operator fun unaryMinus(): Duration = durationOf(-value, unitDiscriminator)

    operator fun plus(other: Duration): Duration {
        when {
            this.isInfinite() -> {
                if (other.isFinite() || (this.rawValue xor other.rawValue >= 0))
                    return this
                else
                    throw IllegalArgumentException("Summing infinite durations of different signs yields an undefined result.")
            }
            other.isInfinite() -> return other
        }

        return when {
            this.unitDiscriminator == other.unitDiscriminator -> {
                val result = this.value + other.value // never overflows long, but can overflow long63
                when {
                    isInNanos() ->
                        durationOfNanosNormalized(result)
                    else ->
                        durationOfMillisNormalized(result)
                }
            }
            this.isInMillis() -> addValuesMixedRanges(this.value, other.value)
            else -> addValuesMixedRanges(other.value, this.value)
        }
    }

    operator fun minus(other: Duration): Duration = this + (-other)

    operator fun times(scale: Int): Duration {
        if (isInfinite()) {
            return when {
                scale == 0 -> throw IllegalArgumentException("Multiplying infinite duration by zero yields an undefined result.")
                scale > 0 -> this
                else -> -this
            }
        }
        if (scale == 0) return ZERO

        val value = value
        val result = value * scale
        return if (isInNanos()) {
            if (value in (MAX_NANOS / Int.MIN_VALUE)..(-MAX_NANOS / Int.MIN_VALUE)) {
                // can't overflow nanos range for any scale
                durationOfNanos(result)
            } else {
                if (result / scale == value) {
                    durationOfNanosNormalized(result)
                } else {
                    val millis = nanosToMillis(value)
                    val remNanos = value - millisToNanos(millis)
                    val resultMillis = millis * scale
                    val totalMillis = resultMillis + nanosToMillis(remNanos * scale)
                    if (resultMillis / scale == millis && totalMillis xor resultMillis >= 0) {
                        durationOfMillis(totalMillis.coerceIn(-MAX_MILLIS..MAX_MILLIS))
                    } else {
                        if (value.sign * scale.sign > 0) INFINITE else NEG_INFINITE
                    }
                }
            }
        } else {
            if (result / scale == value) {
                durationOfMillis(result.coerceIn(-MAX_MILLIS..MAX_MILLIS))
            } else {
                if (value.sign * scale.sign > 0) INFINITE else NEG_INFINITE
            }
        }
    }

    operator fun times(scale: Double): Duration {
        val intScale = scale.roundToInt()
        if (intScale.toDouble() == scale) {
            return times(intScale)
        }

        val unit = storageUnit
        val result = toLong(unit) * scale
        return of(result.toLong(), unit)
    }

    operator fun div(scale: Int): Duration {
        if (scale == 0) {
            return when {
                isPositive() -> INFINITE
                isNegative() -> NEG_INFINITE
                else -> throw IllegalArgumentException("Dividing zero duration by zero yields an undefined result.")
            }
        }
        if (isInNanos()) {
            return durationOfNanos(value / scale)
        } else {
            if (isInfinite())
                return this * scale.sign

            val result = value / scale

            if (result in -MAX_NANOS_IN_MILLIS..MAX_NANOS_IN_MILLIS) {
                val rem = millisToNanos(value - (result * scale)) / scale
                return durationOfNanos(millisToNanos(result) + rem)
            }
            return durationOfMillis(result)
        }
    }

    operator fun div(scale: Double): Duration {
        val intScale = scale.roundToInt()
        if (intScale.toDouble() == scale && intScale != 0) {
            return div(intScale)
        }

        val unit = storageUnit
        val result = toLong(unit) / scale
        return of(result.toLong(), unit)
    }

    /** Returns a number that is the ratio of this and [other] duration values. */
    operator fun div(other: Duration): Double {
        val coarserUnit = maxOf(this.storageUnit, other.storageUnit)
        return this.toLong(coarserUnit).toDouble() / other.toLong(coarserUnit)
    }

    override fun toString(): String = when (rawValue) {
        0L -> "0s"
        INFINITE.rawValue -> "Infinity"
        NEG_INFINITE.rawValue -> "-Infinity"
        else -> {
            val isNegative = isNegative()
            buildString {
                if (isNegative) {
                    append('-')
                }
                absoluteValue.toComponents { days, hours, minutes, seconds, nanoseconds ->
                    val hasDays = days != 0L
                    val hasHours = hours != 0
                    val hasMinutes = minutes != 0
                    val hasSeconds = seconds != 0 || nanoseconds != 0
                    var components = 0
                    if (hasDays) {
                        append(days).append('d')
                        components++
                    }
                    if (hasHours || (hasDays && (hasMinutes || hasSeconds))) {
                        if (components++ > 0) {
                            append(' ')
                        }
                        append(hours).append('h')
                    }
                    if (hasMinutes || (hasSeconds && (hasHours || hasDays))) {
                        if (components++ > 0) {
                            append(' ')
                        }
                        append(minutes).append('m')
                    }
                    if (hasSeconds) {
                        if (components++ > 0) {
                            append(' ')
                        }
                        when {
                            seconds != 0 || hasDays || hasHours || hasMinutes -> appendFractional(seconds, nanoseconds, 9, "s", isoZeroes = false)
                            nanoseconds >= 1_000_000 -> appendFractional(nanoseconds / 1_000_000, nanoseconds % 1_000_000, 6, "ms", isoZeroes = false)
                            nanoseconds >= 1_000 -> appendFractional(nanoseconds / 1_000, nanoseconds % 1_000, 3, "us", isoZeroes = false)
                            else -> append(nanoseconds).append("ns")
                        }
                    }
                    if (isNegative && components > 1) {
                        insert(1, '(').append(')')
                    }
                }
            }
        }
    }

    override fun compareTo(other: Duration): Int {
        val compareBits = this.rawValue xor other.rawValue
        if (compareBits < 0 || compareBits.toInt() and 1 == 0) // different signs or same sign/same range
            return this.rawValue.compareTo(other.rawValue)
        // same sign/different ranges
        val r = this.unitDiscriminator - other.unitDiscriminator // compare ranges
        return if (isNegative()) -r else r
    }

    /** Returns true, if the duration value is less than zero. */
    fun isNegative(): Boolean = rawValue < 0

    /** Returns true, if the duration value is greater than zero. */
    fun isPositive(): Boolean = rawValue > 0

    /** Returns true, if the duration value is infinite. */
    fun isInfinite(): Boolean = rawValue == INFINITE.rawValue || rawValue == NEG_INFINITE.rawValue

    /** Returns true, if the duration value is finite. */
    fun isFinite(): Boolean = !isInfinite()

    fun toLong(unit: TimeUnit): Long {
        return when (rawValue) {
            INFINITE.rawValue -> Long.MAX_VALUE
            NEG_INFINITE.rawValue -> Long.MIN_VALUE
            else -> convertDurationUnit(value, storageUnit, unit)
        }
    }

    inline fun <T> toComponents(action: (days: Long, hours: Int, minutes: Int, seconds: Int, milliseconds: Int) -> T): T {
        return action(inDays, hoursComponent, minutesComponent, secondsComponent, nanosecondsComponent)
    }

    private fun isInNanos() = unitDiscriminator == 0

    private fun isInMillis() = unitDiscriminator == 1

    private fun convertDurationUnit(value: Long, sourceUnit: TimeUnit, targetUnit: TimeUnit): Long {
        var actualValue = value
        if (sourceUnit == TimeUnit.NANOSECONDS) {
            when (targetUnit) {
                TimeUnit.NANOSECONDS -> return actualValue
                TimeUnit.MICROSECONDS -> return actualValue / 1000
                else -> actualValue = nanosToMillis(actualValue) // to ms below
            }
        }
        return when (targetUnit) {
            TimeUnit.NANOSECONDS -> millisToNanos(actualValue)
            TimeUnit.MICROSECONDS -> actualValue * 1000
            TimeUnit.MILLISECONDS -> actualValue
            TimeUnit.SECONDS -> if (actualValue >= 1000L) actualValue / 1000 else 0L
            TimeUnit.MINUTES -> (actualValue / 1000).let { if (it >= 60) it / 60 else 0L }
            TimeUnit.HOURS -> (actualValue / 60_000).let { if (it >= 60) it / 60 else 0L }
            TimeUnit.DAYS -> (actualValue / 3600_000).let { if (it >= 24) it / 24 else 0L }
        }
    }

    private fun addValuesMixedRanges(thisMillis: Long, otherNanos: Long): Duration {
        val otherMillis = nanosToMillis(otherNanos)
        val resultMillis = thisMillis + otherMillis
        return if (resultMillis in -MAX_NANOS_IN_MILLIS..MAX_NANOS_IN_MILLIS) {
            val otherNanoRemainder = otherNanos - millisToNanos(otherMillis)
            durationOfNanos(millisToNanos(resultMillis) + otherNanoRemainder)
        } else {
            durationOfMillis(resultMillis.coerceIn(-MAX_MILLIS, MAX_MILLIS))
        }
    }

    private fun StringBuilder.appendFractional(whole: Int, fractional: Int, fractionalSize: Int, unit: String, isoZeroes: Boolean) {
        append(whole)
        if (fractional != 0) {
            append('.')
            val fracString = fractional.toString().padStart(fractionalSize, '0')
            val nonZeroDigits = fracString.indexOfLast { it != '0' } + 1
            when {
                !isoZeroes && nonZeroDigits < 3 -> fracString.substring(0, nonZeroDigits).also { append(it) }
                else -> fracString.substring(0, ((nonZeroDigits + 2) / 3) * 3).also { append(it) }
            }
        }
        append(unit)
    }

    companion object {
        val ZERO: Duration = Duration(0L)
        val INFINITE: Duration = durationOfMillis(MAX_MILLIS)
        internal val NEG_INFINITE: Duration = durationOfMillis(-MAX_MILLIS)

        fun of(value: Long, unit: TimeUnit): Duration {
            val unitDiscriminator = if (unit < TimeUnit.MILLISECONDS) 0 else 1
            return durationOf(
                when (unit) {
                    TimeUnit.NANOSECONDS -> value
                    TimeUnit.MICROSECONDS -> value * 1000
                    TimeUnit.MILLISECONDS -> value
                    TimeUnit.SECONDS -> value * 1000
                    TimeUnit.MINUTES -> value * 60_000
                    TimeUnit.HOURS -> value * 3600_000
                    TimeUnit.DAYS -> value * 3600_000 * 24
                }, unitDiscriminator
            )
        }

        fun millis(value: Long): Duration = durationOf(value, 1)
    }
}

internal const val NANOS_IN_MILLIS = 1_000_000

// maximum number duration can store in nanosecond range
internal const val MAX_NANOS = Long.MAX_VALUE / 2 / NANOS_IN_MILLIS * NANOS_IN_MILLIS - 1 // ends in ..._999_999

// maximum number duration can store in millisecond range, also encodes an infinite value
internal const val MAX_MILLIS = Long.MAX_VALUE / 2

// MAX_NANOS expressed in milliseconds
private const val MAX_NANOS_IN_MILLIS = MAX_NANOS / NANOS_IN_MILLIS

private fun nanosToMillis(nanos: Long): Long = nanos / NANOS_IN_MILLIS
private fun millisToNanos(millis: Long): Long = millis * NANOS_IN_MILLIS

private fun durationOfNanos(normalNanos: Long) = Duration(normalNanos shl 1)
private fun durationOfMillis(normalMillis: Long) = Duration((normalMillis shl 1) + 1)
private fun durationOf(normalValue: Long, unitDiscriminator: Int) = Duration((normalValue shl 1) + unitDiscriminator)

private fun durationOfNanosNormalized(nanos: Long) =
    if (nanos in -MAX_NANOS..MAX_NANOS) {
        durationOfNanos(nanos)
    } else {
        durationOfMillis(nanosToMillis(nanos))
    }

private fun durationOfMillisNormalized(millis: Long) =
    if (millis in -MAX_NANOS_IN_MILLIS..MAX_NANOS_IN_MILLIS) {
        durationOfNanos(millisToNanos(millis))
    } else {
        durationOfMillis(millis.coerceIn(-MAX_MILLIS, MAX_MILLIS))
    }
