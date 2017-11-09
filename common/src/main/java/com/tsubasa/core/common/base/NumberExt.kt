package com.tsubasa.core.common.base

import com.tsubasa.core.common.transfor.NumberPattern
import java.math.BigDecimal
import java.math.BigInteger
import java.text.DecimalFormat

/**
 * 数字相关扩展
 * Created by tsubasa on 2017/11/7.
 */

/**
 * 转化为Byte
 */
fun String?.trans2Byte(default: Byte = 0): Byte {
    return try {
        java.lang.Byte.parseByte(this)
    } catch (exception: Exception) {
        default
    }
}

/**
 * 转化为Short
 */
fun String?.trans2Short(default: Short = 0): Short {
    return try {
        java.lang.Short.parseShort(this)
    } catch (exception: Exception) {
        default
    }
}

/**
 * 转化为Int
 */
fun String?.trans2Int(default: Int = 0): Int {
    return try {
        Integer.parseInt(this)
    } catch (exception: Exception) {
        default
    }
}

/**
 * 转化为Long
 */
fun String?.trans2Long(default: Long = 0): Long {
    return try {
        java.lang.Long.parseLong(this)
    } catch (exception: Exception) {
        default
    }
}

/**
 * 转为Float
 */
fun String?.trans2Float(default: Float = 0f): Float {
    return try {
        java.lang.Float.parseFloat(this)
    } catch (exception: Exception) {
        default
    }
}

/**
 * 转为Double
 */
fun String?.trans2Double(default: Double = 0.0): Double {
    return try {
        java.lang.Double.parseDouble(this)
    } catch (exception: Exception) {
        default
    }
}


fun String?.trans2BigDecimal(default: BigDecimal = BigDecimal(0)): BigDecimal {
    return try {
        BigDecimal(this)
    } catch (exception: Exception) {
        default
    }
}

fun String?.trans2BigInteger(default: BigInteger = BigInteger("0")): BigInteger {
    return try {
        BigInteger(this)
    } catch (exception: Exception) {
        default
    }
}


fun String?.trans2NumberStr(pattern: NumberPattern = NumberPattern.NumberFoc2Digit, errorStr: String = ""): String {
    return try {
        BigDecimal(this).trans2NumberStr(pattern)
    } catch (ignore: Exception) {
        errorStr
    }
}

fun BigDecimal?.trans2NumberStr(pattern: NumberPattern = NumberPattern.NumberFoc2Digit, errorStr: String = ""): String {
    return try {
        DecimalFormat(pattern.pattern).format(this)
    } catch (ignore: Exception) {
        errorStr
    }
}