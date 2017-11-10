package com.tsubasa.core

import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {
    @Test
    @Throws(Exception::class)
    fun addition_isCorrect() {
        TestClass2(TestInterfaceImpl()).test1()
        TestClass2(TestInterfaceImpl()).test2()
    }
}

interface TestInterface {
    fun test1()
    fun test2()
}

class TestInterfaceImpl : TestInterface {
    override fun test1() {
        println("test1 fun impl")
    }
    override fun test2() {
        println("test2 fun impl")
    }
}

class TestClass2(val t: TestInterfaceImpl) : TestInterface by t {

    override fun test1() {
        t.test1()
        println("test fun in TestClass2")
    }
}