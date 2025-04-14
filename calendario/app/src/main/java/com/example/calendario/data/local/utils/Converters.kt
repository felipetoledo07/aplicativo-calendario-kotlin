package com.example.calendario.data.local.utils

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalTime

class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate): String = date.toString()

    @TypeConverter
    fun toLocalDate(dateString: String): LocalDate = LocalDate.parse(dateString)

    @TypeConverter
    fun fromLocalTime(time: LocalTime): String = time.toString()

    @TypeConverter
    fun toLocalTime(timeString: String): LocalTime = LocalTime.parse(timeString)
}
