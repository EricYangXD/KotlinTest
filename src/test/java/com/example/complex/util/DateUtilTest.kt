package com.example.complex.util

import org.junit.Test

import org.junit.Assert.*
import java.util.*

class DateUtilTest {


    @Test
    fun getNowDate() {
        // Test preparation
        val calendar = Calendar.getInstance()
        val month = String.format("%02d", calendar.get(Calendar.MONTH) + 1)
        val day = String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH))
        val expectedValue = "${calendar.get(Calendar.YEAR)}-$month-$day"

        // Test execution
        val receivedValue = DateUtil.nowDate

        // Test evaluation
        assertEquals(expectedValue, receivedValue)
    }

}
