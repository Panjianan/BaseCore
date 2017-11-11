package com.tsubasa.core_ui_ext

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.tsubasa.core.ui.callback.Status

import com.tsubasa.core.ui.component.statuslayout.StatusUIComponent
import com.tsubasa.core.ui.component.swipetoload.SwipeToLoadComponent
import org.jetbrains.anko.doAsync

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    @Throws(Exception::class)
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()

        assertEquals("com.tsubasa.core.ui.test", appContext.packageName)
    }
}
