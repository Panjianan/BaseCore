package com.tsubasa.core.common.transfor

import com.tsubasa.core.common.base.otherwise
import com.tsubasa.core.common.base.yes
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/***
 * <br> Project BaseCore
 * <br> Package com.tsubasa.core.common.transfor
 * <br> Description 时间相关扩展
 * <br> Version 1.0
 * <br> Author Tsubasa
 * <br> Creation 2017/11/10 11:50
 * <br> Mender Tsubasa
 * <br> Modification 2017/11/10 11:50
 * <br> Copyright Copyright © 2012 - 2017 Tsubasa.All Rights Reserved.
 */

/**
 * 毫秒与毫秒的倍数
 */
const val MILLISSECCOND: Long = 1.toLong()

/**
 * 秒与毫秒的倍数
 */
const val SECOND: Long = 1000.toLong()

/**
 * 分与毫秒的倍数
 */
const val MIN: Long = 60000.toLong()

/**
 * 时与毫秒的倍数
 */
const val HOUR: Long = 3600000.toLong()

/**
 * 天与毫秒的倍数
 */
const val DAY: Long = 86400000.toLong()

open class TimePattern private constructor(val pattern: String) {
    companion object {
        val COMMON_PATTERN = TimePattern("yyyy-MM-dd HH:mm:ss")
        val ONLY_DATA_PATTERN = TimePattern("yyyy-MM-dd")
        fun getInstance(pattern: String) = TimePattern(pattern)
    }
}

/**
 * 获得当前时间戳的时间字符串
 * @param pattern time格式
 * @return 时间字符串, 默认格式为yyyy-MM-dd HH:mm:ss
 */
fun getCurrentTimeStampStr(pattern: TimePattern = TimePattern.COMMON_PATTERN): String {
    return System.currentTimeMillis().trans2TimeStampStr(pattern)
}

/**
 * 获得指定时间戳的时间字符串
 * @param pattern time格式
 * @return 时间字符串, 默认格式为yyyy-MM-dd HH:mm:ss
 */
fun Long?.trans2TimeStampStr(pattern: TimePattern = TimePattern.COMMON_PATTERN): String {
    (this == null).yes { return "" }
    return Date(this!!).trans2TimeStampStr(pattern)
}

/**
 * 将Date类型转为时间字符串
 * @return 时间字符串, 默认格式为yyyy-MM-dd HH:mm:ss
 */
fun Date?.trans2TimeStampStr(pattern: TimePattern = TimePattern.COMMON_PATTERN): String {
    (this == null).yes { return "" }
    return SimpleDateFormat(pattern.pattern, Locale.getDefault()).format(this)
}

/**
 * 时间字符串转化为时间戳
 * @param pattern time格式
 */
fun String?.trans2Millis(pattern: TimePattern = TimePattern.COMMON_PATTERN): Long {
    return try {
        SimpleDateFormat(pattern.pattern, Locale.getDefault()).parse(this).time
    } catch (e: Exception) {
        -1
    }
}

/**
 * 时间字符串转化为Date类型
 * @param pattern time格式
 */
fun String?.trans2Date(pattern: TimePattern = TimePattern.COMMON_PATTERN): Date? {
    return trans2Millis(pattern).let {
        (it == (-1).toLong()).yes {
            null
        }.otherwise {
            Date(it)
        }
    }
}

class FriendlyTimeSpanConverter internal constructor(private val currentTime: Long, private val timeMillis: Long, init: (FriendlyTimeSpanConverter.() -> Unit)? = null) {

    private val timeSpan = currentTime - timeMillis

    private val interceptorList: MutableList<Interceptor> = mutableListOf()

    private interface Interceptor {
        /** 优先级，默认按照阶段数排序 */
        val priority: Long

        /** 阶段数，在这个范围之下的数字归这个拦截器处理 */
        val stage: Long

        val range: LongRange?

        /** 具体执行格式转化的方法 */
        fun format(timeSpan: Long, timeBeforeSpanMillis: Long): String
    }

    init {
        init?.invoke(this)
    }

    /**
     * 添加时间段的拦截(当轮到这个拦截器时，时间差在比设定的时间差要大时)
     * @param stage 和当前时间的时间差，单位是stageUnit
     * @param stageUnit 和当前时间的时间差的单位
     * @param priority 当前拦截器的优先度
     * @param formatter 转化为字符串的回调
     */
    fun addInterceptorWithStage(stage: Long, stageUnit: TimeUnit = TimeUnit.MILLISECONDS, priority: Long = 0, formatter: ((timeSpan: Long, timeBeforeSpanMillis: Long) -> String)) {
        interceptorList.add(object : Interceptor {
            override val range: LongRange? = null
            override val priority: Long = priority
            override var stage: Long = stageUnit.toMillis(stage)

            override fun format(timeSpan: Long, timeBeforeSpanMillis: Long): String {
                return formatter.invoke(timeSpan, timeBeforeSpanMillis)
            }
        })
    }

    /**
     * 添加时间点的拦截(当轮到这个拦截器时，时间比拦截器的时间点要早时)
     * @param timePointMillis 判断的时间点
     * @param priority 当前拦截器的优先度
     * @param formatter 转化为字符串的回调
     */
    fun addInterceptorWithTimePoint(timePointMillis: Long, priority: Long = 0, formatter: ((timeSpan: Long, timeBeforeSpanMillis: Long) -> String)) {
        addInterceptorWithStage(currentTime.minus(timePointMillis.minus(1)), TimeUnit.MILLISECONDS, priority, formatter)
    }

    /**
     * 添加时间段的拦截(当轮到这个拦截器时，时间在拦截器的区间范围时)
     * @param millisRange 时间点的区间
     * @param priority 当前拦截器的优先度
     * @param formatter 转化为字符串的回调
     */
    fun addInterceptorWithRange(millisRange: LongRange, priority: Long = 0, formatter: ((timeSpan: Long, timeBeforeSpanMillis: Long) -> String)) {
        interceptorList.add(object : Interceptor {
            override val range: LongRange? = millisRange
            override val priority: Long = priority
            override var stage: Long = millisRange.first

            override fun format(timeSpan: Long, timeBeforeSpanMillis: Long): String {
                return formatter.invoke(timeSpan, timeBeforeSpanMillis)
            }
        })
    }

    fun format(): String {
        // 按优先级（如果一致按阶段数）从大到小排序
        interceptorList.filter { it.stage > 0 }.sortedWith(kotlin.Comparator { o1, o2 ->
            o2.priority.compareTo(o1.priority).let {
                if (it == 0) {
                    o2.stage.compareTo(o1.stage)
                } else {
                    it
                }
            }
        })

        interceptorList.forEach {
            (it.range?.contains(timeMillis) == true).or(timeSpan >= it.stage).yes {
                return it.format(timeSpan, timeMillis)
            }
        }
        return ""
    }
}

fun Long.trans2FriendTimeSpan(currentTime: Long = System.currentTimeMillis(), block: (FriendlyTimeSpanConverter.() -> Unit)? = null): String {
    return FriendlyTimeSpanConverter(currentTime, this).apply {
        if (block != null) {
            block.invoke(this)
        } else {
            val instance = Calendar.getInstance()
            instance.timeInMillis = currentTime
            // 去年之前的时间
            instance.set(instance.get(Calendar.YEAR), 0, 1, 0, 0, 0)
            addInterceptorWithTimePoint(instance.timeInMillis, 1) { _, timeBeforeSpanMillis ->
                timeBeforeSpanMillis.trans2TimeStampStr(TimePattern.getInstance("yyyy年MM月dd日 HH时mm分"))
            }

            // 拿今天的第一秒的时间
            instance.timeInMillis = currentTime
            instance.set(Calendar.HOUR_OF_DAY, 0)
            instance.set(Calendar.MINUTE, 0)
            instance.set(Calendar.SECOND, 0)
            instance.set(Calendar.MILLISECOND, 0)
            val todayStartMillis = instance.timeInMillis

            // 到今年第一天0点以内
            instance.add(Calendar.DAY_OF_MONTH, -2)
            addInterceptorWithTimePoint(instance.timeInMillis) { _, timeBeforeSpanMillis ->
                timeBeforeSpanMillis.trans2TimeStampStr(TimePattern.getInstance("MM月dd日 HH时mm分"))
            }

            // 到前天0点以内
            instance.timeInMillis = todayStartMillis
            instance.add(Calendar.DAY_OF_MONTH, -1)
            addInterceptorWithTimePoint(instance.timeInMillis) { _, timeBeforeSpanMillis ->
                timeBeforeSpanMillis.trans2TimeStampStr(TimePattern.getInstance("前天 HH时mm分"))
            }

            // 到昨天0点以内
            addInterceptorWithTimePoint(todayStartMillis) { _, timeBeforeSpanMillis ->
                timeBeforeSpanMillis.trans2TimeStampStr(TimePattern.getInstance("昨天 HH时mm分"))
            }

            // 到今天0点以内
            addInterceptorWithStage(1, TimeUnit.HOURS) { _, timeBeforeSpanMillis ->
                timeBeforeSpanMillis.trans2TimeStampStr(TimePattern.getInstance("HH时mm分"))
            }

            // 一小时内
            addInterceptorWithStage(1, TimeUnit.MINUTES) { timeSpan, _ ->
                "${TimeUnit.MILLISECONDS.toMinutes(timeSpan)}分钟前"
            }

            // 一分钟内
            addInterceptorWithStage(0, TimeUnit.MILLISECONDS) { timeSpan, timeBeforeSpanMillis ->
                timeBeforeSpanMillis.trans2TimeStampStr(TimePattern.getInstance("刚刚"))
                "${TimeUnit.MILLISECONDS.toSeconds(timeSpan)}秒前"
            }
        }
    }.format()
}