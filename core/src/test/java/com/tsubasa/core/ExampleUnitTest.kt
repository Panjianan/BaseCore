package com.tsubasa.core

import com.tsubasa.core.common.transfor.trans2FriendTimeSpan
import com.tsubasa.core.common.transfor.trans2Millis
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {
    @Test
    @Throws(Exception::class)
    fun testTimeTrans2FriendlyHint() {
        val currentTime = "2017-03-04 14:00:00"
        val currentTimeMillis = currentTime.trans2Millis()
        val time = arrayOf(
                "2017-03-05 13:59:59",
                "2017-03-04 13:59:59",
                "2017-03-04 13:59:00",
                "2017-03-04 13:58:59",
                "2017-03-04 13:58:00",
                "2017-03-04 13:30:01",
                "2017-03-04 13:29:01",
                "2017-03-04 13:28:59",
                "2017-03-04 13:00:01",
                "2017-03-04 13:00:00",
                "2017-03-04 12:58:00",
                "2017-03-03 14:00:00",
                "2017-03-02 00:00:00",
                "2017-03-01 00:00:00",
                "2017-01-01 00:00:00",
                "2016-12-31 23:59:59",
                "2016-12-04 11:55:00"
        )
        time.forEach {
            println("time = $it  currentTime = $currentTime  friendlyTimeHint = ${it.trans2Millis().trans2FriendTimeSpan(currentTimeMillis)}")
        }
    }
}