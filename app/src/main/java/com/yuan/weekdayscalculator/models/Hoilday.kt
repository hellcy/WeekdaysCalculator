package com.yuan.weekdayscalculator.models

data class Hoilday (
        var name: String,
        var alwaysOnWeekday: Boolean,
        var willSubstituteOnWeekday: Boolean,
        var dayOfMonth: Int = 0,
        var month: Int = 0
)