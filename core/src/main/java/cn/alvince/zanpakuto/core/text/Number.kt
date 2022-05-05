package cn.alvince.zanpakuto.core.text

import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext

fun String?.toByteOr(defaultVal: Byte): Byte = this?.toByteOrNull() ?: defaultVal

fun String?.toByteOr(radix: Int, defaultVal: Byte): Byte = this?.toByteOrNull(radix) ?: defaultVal

inline fun String?.toByteOr(defaultVal: () -> Byte): Byte = toByteOr(radix = 10, defaultVal)

inline fun String?.toByteOr(radix: Int, defaultVal: () -> Byte): Byte = this?.toByteOrNull(radix) ?: defaultVal()

fun String?.toShortOr(defaultVal: Short): Short = this?.toShortOrNull() ?: defaultVal

fun String?.toShortOr(radix: Int, defaultVal: Short): Short = this?.toShortOrNull(radix) ?: defaultVal

inline fun String?.toShortOr(defaultVal: () -> Short): Short = toShortOr(radix = 10, defaultVal)

inline fun String?.toShortOr(radix: Int, defaultVal: () -> Short): Short = this?.toShortOrNull(radix) ?: defaultVal()

fun String?.toIntOr(defaultVal: Int): Int = this?.toIntOrNull() ?: defaultVal

fun String?.toIntOr(radix: Int, defaultVal: Int): Int = this?.toIntOrNull(radix) ?: defaultVal

inline fun String?.toIntOr(defaultVal: () -> Int): Int = toIntOr(radix = 10, defaultVal)

inline fun String?.toIntOr(radix: Int, defaultVal: () -> Int): Int = this?.toIntOrNull(radix) ?: defaultVal()

fun String?.toLongOr(defaultVal: Long): Long = this?.toLongOrNull() ?: defaultVal

fun String?.toLongOr(radix: Int, defaultVal: Long): Long = this?.toLongOrNull(radix) ?: defaultVal

inline fun String?.toLongOr(defaultVal: () -> Long): Long = toLongOr(radix = 10, defaultVal)

inline fun String?.toLongOr(radix: Int, defaultVal: () -> Long): Long = this?.toLongOrNull() ?: defaultVal()

fun String?.toFloatOr(defaultVal: Float): Float = this?.toFloatOrNull() ?: defaultVal

fun String?.toFloatOr(defaultVal: () -> Float): Float = this?.toFloatOrNull() ?: defaultVal()

fun String?.toDoubleOr(defaultVal: Double): Double = this?.toDoubleOrNull() ?: defaultVal

fun String?.toDoubleOr(defaultVal: () -> Double): Double = this?.toDoubleOrNull() ?: defaultVal()

fun String?.toBigIntegerOr(defaultVal: BigInteger): BigInteger = this?.toBigIntegerOrNull() ?: defaultVal

fun String?.toBigIntegerOr(radix: Int, defaultVal: BigInteger): BigInteger = this?.toBigIntegerOrNull(radix) ?: defaultVal

inline fun String?.toBigIntegerOr(defaultVal: () -> BigInteger): BigInteger = toBigIntegerOr(radix = 10, defaultVal)

inline fun String?.toBigIntegerOr(radix: Int, defaultVal: () -> BigInteger): BigInteger = this?.toBigIntegerOrNull(radix) ?: defaultVal()

fun String?.toBigDecimalOr(defaultVal: BigDecimal): BigDecimal = this?.toBigDecimalOrNull() ?: defaultVal

fun String?.toBigDecimalOr(mathContext: MathContext, defaultVal: BigDecimal): BigDecimal = this?.toBigDecimalOrNull(mathContext) ?: defaultVal

inline fun String?.toBigDecimalOr(defaultVal: () -> BigDecimal): BigDecimal = this?.toBigDecimalOrNull() ?: defaultVal()

inline fun String?.toBigDecimalOr(mathContext: MathContext, defaultVal: () -> BigDecimal): BigDecimal = this?.toBigDecimalOrNull(mathContext) ?: defaultVal()
