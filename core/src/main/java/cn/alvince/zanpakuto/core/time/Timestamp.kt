package cn.alvince.zanpakuto.core.time

import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

/**
 * Created by alvince on 2021/12/22
 *
 * @author alvince.zy@gmail.com
 */
@JvmInline
value class Timestamp(val inMillis: Long) : Comparable<Timestamp> {

    val inSeconds: Long get() = inMillis / 1000L

    override fun compareTo(other: Timestamp): Int = this.inMillis.compareTo(other.inMillis)

    operator fun plus(duration: Duration) = Timestamp(inMillis + duration.inMillis)

    operator fun minus(duration: Duration) = Timestamp(inMillis - duration.inMillis)

    operator fun minus(other: Timestamp): Duration = Duration.of(inMillis - other.inMillis, TimeUnit.MILLISECONDS)

    override fun toString(): String {
        return buildString {
            toComponents { year, month, dayOfMonth, hours, minutes, seconds, millis, _ ->
                append(TimeZone.getDefault().let { it.getDisplayName(it.useDaylightTime(), TimeZone.SHORT, Locale.ROOT) })
                append(' ').append(year)
                append('-').append("${month + 1}".padStart(2, '0'))
                append('-').append("$dayOfMonth".padStart(2, '0'))
                append(' ').append("$hours".padStart(2, '0'))
                append(':').append("$minutes".padStart(2, '0'))
                append(':').append("$seconds".padStart(2, '0'))
                if (millis > 0) {
                    append('.').append("$millis".padStart(3, '0'))
                }
            }
        }
    }

    fun isPast() = inMillis < TimeSource.currentTimeMillis()

    fun isFuture() = inMillis > TimeSource.currentTimeMillis()

    fun toDate(): Date = Date(inMillis)

    fun toCalendar(tz: TimeZone = TimeZone.getDefault()): Calendar = Calendar.getInstance(Locale.ROOT)
        .apply {
            timeZone = tz
            timeInMillis = this@Timestamp.inMillis
        }

    inline fun toComponents(tz: TimeZone = TimeZone.getDefault(), action: (year: Int, month: Int, dayOfMonth: Int) -> Unit) {
        toCalendar(tz).apply { action(yearComponent, monthComponent, dayOfMonthComponent) }
    }

    inline fun toComponents(
        tz: TimeZone = TimeZone.getDefault(),
        action: (year: Int, month: Int, dayOfMonth: Int, hours: Int, minutes: Int, seconds: Int, millis: Int, zoneOffset: Int) -> Unit
    ) {
        toCalendar(tz).apply {
            action(
                yearComponent,
                monthComponent,
                dayOfMonthComponent,
                hourComponent,
                minutesComponent,
                secondsComponent,
                millisecondsComponent,
                get(Calendar.ZONE_OFFSET)
            )
        }
    }

    @PublishedApi
    internal inline val Calendar.yearComponent: Int
        get() = this.get(Calendar.YEAR)

    @PublishedApi
    internal inline val Calendar.monthComponent: Int
        get() = this.get(Calendar.MONTH)

    @PublishedApi
    internal inline val Calendar.dayOfMonthComponent: Int
        get() = this.get(Calendar.DAY_OF_MONTH)

    @PublishedApi
    internal inline val Calendar.hourComponent: Int
        get() = this.get(Calendar.HOUR_OF_DAY)

    @PublishedApi
    internal inline val Calendar.minutesComponent: Int
        get() = this.get(Calendar.MINUTE)

    @PublishedApi
    internal inline val Calendar.secondsComponent: Int
        get() = this.get(Calendar.SECOND)

    @PublishedApi
    internal inline val Calendar.millisecondsComponent: Int
        get() = this.get(Calendar.MILLISECOND)

    companion object {
        val ZERO = Timestamp(0L)

        val FAR_FUTURE = Timestamp(Long.MAX_VALUE)

        fun now(): Timestamp = Timestamp(TimeSource.currentTimeMillis())
    }
}

inline val TimeZone.rawOffsetTime: Long get() = this.rawOffset.toLong()
