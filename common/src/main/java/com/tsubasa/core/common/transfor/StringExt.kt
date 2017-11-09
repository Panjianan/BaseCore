package com.tsubasa.core.common.transfor

import com.tsubasa.core.common.base.*
import java.math.BigDecimal

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

class NumberPattern private constructor(val pattern: String) {
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
        this.stageStep[stepIndex].let {
            (originValue < it).or(stepIndex >= stageStep.size.minus(1)).yes {
                return originValue.trans2NumberStr(pattern).plus(stageUnit[stepIndex])
            }
            return trans(originValue.divide(it), stepIndex.plus(1))
        }
    }

    fun trans2Str(): String = trans(value.trans2BigDecimal())
}

/**
 * 获取数字转换器
 */
fun String?.trans2StageNumberConverter(): StageNumberConverter {
    return StageNumberConverter(this)
}