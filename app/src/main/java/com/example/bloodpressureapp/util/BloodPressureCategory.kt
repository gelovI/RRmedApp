package com.example.bloodpressureapp.util

import android.content.Context
import com.example.bloodpressureapp.R

fun getBpCategory(systolic: Int, diastolic: Int): Int {
    val systolicLevel = when {
        systolic >= 180 -> 5
        systolic in 160..179 -> 4
        systolic in 140..159 -> 3
        systolic in 130..139 -> 2
        systolic in 120..129 -> 1
        else -> 0
    }

    val diastolicLevel = when {
        diastolic >= 110 -> 5
        diastolic in 100..109 -> 4
        diastolic in 90..99 -> 3
        diastolic in 85..89 -> 2
        diastolic in 80..84 -> 1
        else -> 0
    }

    return maxOf(systolicLevel, diastolicLevel)
}

fun getBpCategoryLabel(context: Context, category: Int): String {
    return when (category) {
        0 -> context.getString(R.string.bp_optimal)
        1 -> context.getString(R.string.bp_normal)
        2 -> context.getString(R.string.bp_high_normal)
        3 -> context.getString(R.string.bp_hypertension_1)
        4 -> context.getString(R.string.bp_hypertension_2)
        5 -> context.getString(R.string.bp_hypertension_severe)
        else -> context.getString(R.string.bp_unspecified)
    }
}
