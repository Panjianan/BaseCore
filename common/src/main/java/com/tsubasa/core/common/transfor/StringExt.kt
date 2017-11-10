package com.tsubasa.core.common.transfor

import com.tsubasa.core.common.base.*
import java.math.BigDecimal
import java.text.DecimalFormat

/**
 * 字符串的相关扩展
 * Created by tsubasa on 2017/11/7.
 */

/**
 * 转化为有最大限制的数字,超过的话则显示为"xxx+"
 * 默认最大为9999
 * eg:"9999+"
 */
fun String?.trans2LimitedInt(defaultMax: Long = 9999): String {
    val number = trans2Long()
    return if (number > defaultMax) {
        defaultMax.toString().plus("+")
    } else {
        number.toString()
    }
}

/**
 * 数字转字符的格式 <br/>
 * 这里用companion获取默认提供实例,如果有其他实现，继承此类即可 <br/>
 * regex 传递给[DecimalFormat]构造参数的，用于格式转化
 */
open class NumberPattern private constructor(val pattern: String) {
    companion object {
        /** 带千分位的，整数 */
        val IntWithCurrency = NumberPattern("#,##0")
        /** 带千分位的，强制保留两位小数 */
        val Double2DigitWithCurrency = NumberPattern("#,##0.00")
        /** 带千分位的，不强制保留两位小数 */
        val Number2DigitWithCurrency = NumberPattern("#,##0.##")
        /** 整数 */
        val Integer = NumberPattern("#0")
        /** 强制保留两位小数 */
        val Double2Digit = NumberPattern("#0.00")
        /** 不强制保留两位小数 */
        val NumberFoc2Digit = NumberPattern("#0.##")

        /** 如果有其他的格式用这个 */
        fun getInstance(pattern: String) = NumberPattern(pattern)
    }
}

/**
 * 用来处理相应位数对应的数字加单位
 * exp:
 * defaultUnit = "m"
 * addStage(4, "km", true)
 * 999 ->
 *
 */
class StageNumberConverter internal constructor(private val value: String?) {

    var defaultUnit: String = ""

    private var pattern: NumberPattern = NumberPattern.NumberFoc2Digit

    private val stageStep = mutableListOf<BigDecimal>()

    private val stageUnit = mutableListOf<String>()

    fun addStage(stageStep: Number, unit: String): StageNumberConverter {
        this.stageStep.add(BigDecimal(stageStep.toString()))
        this.stageUnit.add(unit)
        return this
    }

    fun setPattern(pattern: NumberPattern) {
        this.pattern = pattern
    }

    private fun trans(originValue: BigDecimal, stepIndex: Int = 0): String {
        val unit = (stepIndex >= stageUnit.size).yes { defaultUnit }.otherwise { stageStep[stepIndex] }
        (stepIndex >= stageStep.size.minus(1)).yes {
            return originValue.trans2NumberStr(pattern).plus(unit)
        }.otherwise {
            val stageValue = this.stageStep[stepIndex]
            return (originValue < stageValue).yes {
                originValue.trans2NumberStr(pattern).plus(unit)
            }.otherwise {
                trans(originValue.divide(stageValue), stepIndex.plus(1))
            }
        }
    }

    fun trans2Str(): String = trans(value.trans2BigDecimal())
}

/**
 * 获取数字转换器
 */
fun String?.trans2StageNumberConverter(init: (StageNumberConverter.() -> Unit)? = null): String {
    return StageNumberConverter(this).apply { init?.invoke(this) }.trans2Str()
}

/**
 * 转化为打码的字符串
 */
fun String?.trans2MosaicStr(): String {
    if (this == null) {
        return ""
    }
    val afterTrim = this.trim().replace(" ", "").replace("　", "")
    // 邮箱的情况下
    afterTrim.regexIsMatch(RegexPattern.REGEX_EMAIL).yes {
        val atIndex = indexOf('@')
        val substring = substring(0, atIndex)
        return when (substring.length) {
            1, 2 -> "****".plus(substring(atIndex, length))
            else -> toCharArray().let { it[0] + "****" + it[atIndex - 1] + substring(atIndex, length) }
        }
    }
    // 为手机号的情况下
    afterTrim.regexIsMatch(RegexPattern.REGEX_MOBILE_SIMPLE).yes {
        return substring(0, 3) + "****" + substring(7, length)
    }
    // 为身份证的情况
    afterTrim.regexIsMatch(RegexPattern.REGEX_ID_CARD15).or(afterTrim.regexIsMatch(RegexPattern.REGEX_ID_CARD18)).yes {
        return toCharArray().let { it[0] + "****************" + it[length - 1] }
    }
    // 为姓名的情况下
    (afterTrim.regexIsMatch(RegexPattern.REGEX_ZH)).yes {
        return toCharArray().let { "**" + it[length - 1] }
    }
    // 银行卡的情况
    (length == 19 || length == 16).yes {
        val chars = toCharArray()
        chars.forEachIndexed { index, _ ->
            if (index < length - 4) {
                chars[index] = '*'
            }
        }
        return String(chars)
    }
    return this
}

