package com.tsubasa.core.common.transfor

import com.tsubasa.core.common.base.otherwise
import com.tsubasa.core.common.base.yes
import java.text.SimpleDateFormat
import java.util.*

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

sealed class TimeSpan(val timeSpace: Long) {
    companion object {
        /**
         * 毫秒与毫秒的倍数
         */
        object MSEC : TimeSpan(1.toLong())

        /**
         * 秒与毫秒的倍数
         */
        object SECOND : TimeSpan(1000.toLong())

        /**
         * 分与毫秒的倍数
         */
        object MIN : TimeSpan(60000.toLong())

        /**
         * 时与毫秒的倍数
         */
        object HOUR : TimeSpan(3600000.toLong())

        /**
         * 天与毫秒的倍数
         */
        object DAY : TimeSpan(86400000.toLong())
    }
}

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

class FriendlyTimeSpanConverter internal constructor(private val timeMillis: Long, init: (FriendlyTimeSpanConverter.() -> Unit)? = null) {

    private val timeSpan = System.currentTimeMillis() - timeMillis

    init {
        init?.invoke(this)
    }

    private var yearAgoStrFormatter: ((timeMillis: Long, year: Int, month: Int, day: Int, hourOfDay: Int, minute: Int, second: Int) -> String) = { timeMillis, year, month, day, hour, minute, second ->
        timeMillis.trans2TimeStampStr(TimePattern.getInstance("yyyy年MM月dd日 hh时mm分"))
    }

    fun onYearAgo(block: ((timeMillis: Long, year: Int, month: Int, day: Int, hourOfDay: Int, minute: Int, second: Int) -> String)) {
        yearAgoStrFormatter = block
    }

    private var thisYearStrFormatter: ((timeMillis: Long, year: Int, month: Int, day: Int, hourOfDay: Int, minute: Int, second: Int) -> String) = { timeMillis, year, month, day, hour, minute, second ->
        timeMillis.trans2TimeStampStr(TimePattern.getInstance("MM月dd日 hh时mm分"))
    }

    fun onThisYear(block: ((timeMillis: Long, year: Int, month: Int, day: Int, hourOfDay: Int, minute: Int, second: Int) -> String)) {
        thisYearStrFormatter = block
    }

    private var theDayBeforeYesterdayStrFormatter: ((timeMillis: Long, year: Int, month: Int, day: Int, hourOfDay: Int, minute: Int, second: Int) -> String) = { timeMillis, year, month, day, hour, minute, second ->
        timeMillis.trans2TimeStampStr(TimePattern.getInstance("前天 hh时mm分"))
    }

    fun onTheDayBeforeYesterday(block: ((timeMillis: Long, year: Int, month: Int, day: Int, hourOfDay: Int, minute: Int, second: Int) -> String)) {
        theDayBeforeYesterdayStrFormatter = block
    }

    private var yesterdayStrFormatter: ((timeMillis: Long, year: Int, month: Int, day: Int, hourOfDay: Int, minute: Int, second: Int) -> String) = { timeMillis, year, month, day, hour, minute, second ->
        timeMillis.trans2TimeStampStr(TimePattern.getInstance("昨天 hh时mm分"))
    }

    fun onYesterday(block: ((timeMillis: Long, year: Int, month: Int, day: Int, hourOfDay: Int, minute: Int, second: Int) -> String)) {
        yesterdayStrFormatter = block
    }

    private var todayStrFormatter: ((timeMillis: Long, year: Int, month: Int, day: Int, hourOfDay: Int, minute: Int, second: Int) -> String) = { timeMillis, year, month, day, hour, minute, second ->
        timeMillis.trans2TimeStampStr(TimePattern.getInstance("今天 hh时mm分"))
    }

    fun onToday(block: ((timeMillis: Long, year: Int, month: Int, day: Int, hourOfDay: Int, minute: Int, second: Int) -> String)) {
        todayStrFormatter = block
    }

    private var inHourStrFormatter: ((timeMillis: Long, year: Int, month: Int, day: Int, hourOfDay: Int, minute: Int, second: Int) -> String) = { timeMillis, year, month, day, hour, minute, second ->
        String.format(Locale.getDefault(), "%d分钟前", timeSpan / TimeSpan.Companion.MIN.timeSpace)
    }

    fun onHour(block: ((timeMillis: Long, year: Int, month: Int, day: Int, hourOfDay: Int, minute: Int, second: Int) -> String)) {
        inHourStrFormatter = block
    }

    private var inMinuteStrFormatter: ((timeMillis: Long, year: Int, month: Int, day: Int, hourOfDay: Int, minute: Int, second: Int) -> String) = { timeMillis, year, month, day, hour, minute, second ->
        "刚刚"
    }

    fun onMinute(block: ((timeMillis: Long, year: Int, month: Int, day: Int, hourOfDay: Int, minute: Int, second: Int) -> String)) {
        inMinuteStrFormatter = block
    }

    fun trans2FriendlyStr(): String {
        val instance = Calendar.getInstance()
        val nowYear = instance.get(Calendar.YEAR)
        val nowMonth = instance.get(Calendar.MONTH)
        val nowDay = instance.get(Calendar.DAY_OF_MONTH)
        val nowHour = instance.get(Calendar.HOUR_OF_DAY)
        val nowMinute = instance.get(Calendar.MINUTE)
        val nowSecond = instance.get(Calendar.SECOND)
        // 先拿今年第一天第一秒的时间
        val lastYear = Calendar.getInstance()
        lastYear.set(nowYear, 0, 1, 0, 0, 0)
        val lastYearInMillis = lastYear.timeInMillis

        val timeCalendar = Calendar.getInstance()
        timeCalendar.timeInMillis = timeMillis
        val year = timeCalendar.get(Calendar.YEAR)
        val month = timeCalendar.get(Calendar.MONTH) + 1
        val day = timeCalendar.get(Calendar.DAY_OF_MONTH)
        val hour = timeCalendar.get(Calendar.HOUR_OF_DAY)
        val minute = timeCalendar.get(Calendar.MINUTE)
        val second = timeCalendar.get(Calendar.SECOND)

        if ((timeMillis < lastYearInMillis)) {
            return yearAgoStrFormatter.invoke(timeMillis, year, month, day, hour, minute, second)
        }

        instance.set(nowYear, nowMonth, nowDay, nowHour, nowMinute, nowSecond)
        instance.add(Calendar.DAY_OF_MONTH, -2)
        (timeMillis < instance.timeInMillis).yes {
            // 三天以前的时间 显示xx月xx日 xx时xx分
            return thisYearStrFormatter.invoke(timeMillis, year, month, day, hour, minute, second)
        }

        instance.set(nowYear, nowMonth, nowDay, nowHour, nowMinute, nowSecond)
        instance.add(Calendar.DAY_OF_MONTH, -1)
        (timeMillis < instance.timeInMillis).yes {
            // 是否大于两天
            return theDayBeforeYesterdayStrFormatter.invoke(timeMillis, year, month, day, hour, minute, second)
        }

        instance.set(nowYear, nowMonth, nowDay, nowHour, nowMinute, nowSecond)
        (timeMillis < instance.timeInMillis).yes {
            // 是否大于一天
            return yesterdayStrFormatter.invoke(timeMillis, year, month, day, hour, minute, second)
        }

        (timeSpan < TimeSpan.Companion.MIN.timeSpace).yes {
            // 是否在一分内
            return inMinuteStrFormatter.invoke(timeMillis, year, month, day, hour, minute, second)
        }

        (timeSpan < TimeSpan.Companion.HOUR.timeSpace).yes {
            // 是否在一小时内
            return inHourStrFormatter.invoke(timeMillis, year, month, day, hour, minute, second)
        }

        // 在今天内
        return todayStrFormatter.invoke(timeMillis, year, month, day, hour, minute, second)
    }
}

fun Long.trans2FriendTimeSpan(block: (FriendlyTimeSpanConverter.() -> Unit)? = null): String {
    return FriendlyTimeSpanConverter(this).apply { block?.invoke(this) }.trans2FriendlyStr()
}

class TimeSpanConverter internal constructor(private val time1: Long, private val time2: Long) {

    private var timeSpanWithUnitFormatter: ((millisSpan: Long, unit: TimeSpan) -> String) = { millisSpan, unit ->
        autoTimeSpanFormatter(millisSpan.div(unit.timeSpace), unit)
    }

    fun onTimeSpanWithUnitFormat(block: ((millisSpan: Long, unit: TimeSpan) -> String)) {
        timeSpanWithUnitFormatter = block
    }

    private var autoTimeSpanFormatter: ((span: Long, unit: TimeSpan) -> String) = { span, unit ->
        when (unit) {
            is TimeSpan.Companion.MIN -> span.toString().plus("分钟前")
            is TimeSpan.Companion.HOUR -> span.toString().plus("小时前")
            is TimeSpan.Companion.DAY -> span.toString().plus("天前")
            is TimeSpan.Companion.SECOND -> span.toString().plus("秒前")
            else -> span.toString()
        }
    }

    fun onAutoTimeSpanFormat(block: ((span: Long, unit: TimeSpan) -> String)) {
        autoTimeSpanFormatter = block
    }

    fun trans2TimeSpan(unit: TimeSpan? = null): String {
        val timeSpan = Math.abs(time1.minus(time2))
        return if (unit != null) {
            timeSpanWithUnitFormatter.invoke(timeSpan.div(unit.timeSpace), unit)
        } else {
            val autoUnit: TimeSpan = when {
                timeSpan >= TimeSpan.Companion.DAY.timeSpace -> TimeSpan.Companion.DAY
                timeSpan >= TimeSpan.Companion.HOUR.timeSpace -> TimeSpan.Companion.HOUR
                timeSpan >= TimeSpan.Companion.MIN.timeSpace -> TimeSpan.Companion.MIN
                timeSpan >= TimeSpan.Companion.SECOND.timeSpace -> TimeSpan.Companion.SECOND
                else -> TimeSpan.Companion.MSEC
            }
            autoTimeSpanFormatter(timeSpan.div(autoUnit.timeSpace), autoUnit)
        }
    }
}

fun Long.trans2TimeSpan(otherMillis: Long, block: (TimeSpanConverter.() -> Unit)? = null): String {
    return TimeSpanConverter(this, otherMillis).apply { block?.invoke(this) }.trans2TimeSpan()
}
