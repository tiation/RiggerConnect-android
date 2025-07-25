package com.chasewhiterabbit.riggerconnect.util

object TestUtil {
    fun mainCoroutineRule() = kotlinx.coroutines.test.TestCoroutineDispatcher()
}
