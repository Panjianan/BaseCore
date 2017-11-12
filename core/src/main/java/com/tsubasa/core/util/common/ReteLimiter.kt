package com.tsubasa.core.util.common

import android.os.SystemClock
import android.util.ArrayMap
import com.tsubasa.core.common.base.yes
import java.util.concurrent.TimeUnit

/**
 * Utility class that decides whether we should fetch some data or not.
 * Created by tsubasa on 2017/11/12.
 */
class ReteLimiter<KEY>(val timeout: Int, val timeUnit: TimeUnit) {
    val timestamps = ArrayMap<KEY, Long>()

    fun sholdFetch(key: KEY): Boolean {
        synchronized(this) {
            /*
             *  和System.currentTimeMills()的区别是:
             *  1.这个是开机到现在的毫秒数
             *  2.System.currentTimeMills()获取的时间是可以通过SystemClock.setCurrentTimeMills修改的
             *  这样修改之后再来算时间就不准了
             *  for example: SystemClock.setCurrentTimeMillis(2333)
             */
            val now = SystemClock.uptimeMillis()
            val lastFetched = timestamps[key]
            return (lastFetched == null).or(now.minus(lastFetched ?: 0) > timeout).also {
                it.yes { timestamps.put(key, now) }
            }
        }
    }

    fun reset(key: KEY) {
        synchronized(this) {
            timestamps.remove(key)
        }
    }

}