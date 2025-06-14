package com.soze.studio9910

data class CalendarDay(
    val day: Int,
    val isCurrentMonth: Boolean = true,
    val hasDiary: Boolean = false,
    val isSelected: Boolean = false
) 