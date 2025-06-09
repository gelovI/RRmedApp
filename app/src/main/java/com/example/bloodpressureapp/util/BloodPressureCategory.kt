package com.example.bloodpressureapp.util

fun getBpCategory(systolic: Int, diastolic: Int): Int {
    val systolicLevel = when {
        systolic < 120 -> 0
        systolic in 120..129 -> 1
        systolic in 130..139 -> 2
        systolic in 140..159 -> 3
        systolic in 160..179 -> 4
        systolic >= 180 -> 5
        else -> -1
    }

    val diastolicLevel = when {
        diastolic < 80 -> 0
        diastolic in 80..84 -> 1
        diastolic in 85..89 -> 2
        diastolic in 90..99 -> 3
        diastolic in 100..109 -> 4
        diastolic >= 110 -> 5
        else -> -1
    }

    return maxOf(systolicLevel, diastolicLevel)
}