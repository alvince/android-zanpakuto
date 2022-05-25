package cn.alvince.zanpakuto.core.time

import org.junit.Test
import java.util.concurrent.TimeUnit

/**
 * Created by alvince on 2021/12/29
 *
 * @author alvince.zy@gmail.com
 */
class TimeUnitTest {
    @Test
    fun testDuration() {
        Duration.millis(1L).also { println(it) }
        Duration.millis(100L).also { println(it) }
        Duration.millis(1000L).also { println(it) }
        Duration.millis(60_000 + 1_000 + 1L).also { println(it) }
        Duration.millis(24 * 3600_000 + 3600_000 + 60_000 + 1_000 + 1L).also { println(it) }
        Duration.millis(48 * 3600_000 + 3600_000 + 60_000 + 1_000 + 1L).also { println(it) }
    }

    @Test
    fun testTimestamp() {
        Timestamp.now().also {
            println("now: $it")

            println("[min](ms) ${it - Duration.of(100, TimeUnit.MILLISECONDS)}")
            println("[min](sec) ${it - Duration.of(1, TimeUnit.SECONDS)}")
            println("[min](min) ${it - Duration.of(1, TimeUnit.MINUTES)}")
            println("[min](hour) ${it - Duration.of(1, TimeUnit.HOURS)}")
            println("[min](day) ${it - Duration.of(1, TimeUnit.DAYS)}")

            println("[plus](ms) ${it + Duration.of(100, TimeUnit.MILLISECONDS)}")
            println("[plus](sec) ${it + Duration.of(1, TimeUnit.SECONDS)}")
            println("[plus](min) ${it + Duration.of(1, TimeUnit.MINUTES)}")
            println("[plus](hour) ${it + Duration.of(1, TimeUnit.HOURS)}")
            println("[plus](day) ${it + Duration.of(1, TimeUnit.DAYS)}")
            println("[plus](2days) ${it + Duration.of(2, TimeUnit.DAYS)}")
        }
    }

    @Test
    fun testTimestampCalc() {
        val now = Timestamp.now()
        val nextSec = now + Duration.of(1, TimeUnit.SECONDS)
        nextSec.also { t ->
            println(t)
            (t - now).also { println(it) }
        }
        val nextMin = now + Duration.of(1, TimeUnit.MINUTES)
        nextMin.also { t ->
            println(t)
            (t - now).also { println(it) }
        }
        val nextH = now + Duration.of(1, TimeUnit.HOURS)
        nextH.also { t ->
            println(t)
            (t - now).also { println(it) }
        }
    }
}
