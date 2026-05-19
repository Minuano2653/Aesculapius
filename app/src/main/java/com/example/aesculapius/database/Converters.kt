package com.example.aesculapius.database

import androidx.room.TypeConverter
import com.example.aesculapius.data.CurrentMedicineType
import java.lang.Math.abs
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Converters {

    @TypeConverter
    fun fromTimestamp(value: String?): LocalDate? {
        return if (value == null) null else LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE)
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): String? {
        return date?.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    @TypeConverter
    fun fromStringToList(value: String?): MutableList<Int>? {
        if (value == null) {
            return null
        }
        return value.split(",").map { it.toInt() }.toMutableList()
    }

    @TypeConverter
    fun fromList(list: MutableList<Int>?): String? {
        return list?.joinToString(",")
    }

    @TypeConverter
    fun fromStringToMedicineType(value: String): CurrentMedicineType? {
        return enumValueOf<CurrentMedicineType>(value)
    }

    @TypeConverter
    fun enumToString(value: CurrentMedicineType): String {
        return value.name
    }

    companion object {
        fun timeToString(time: LocalDateTime?): String {
            val formatter = DateTimeFormatter.ofPattern("HH:mm dd MM yyyy")
            return if (time == null) LocalDateTime.now().format(formatter) else time.format(formatter)
        }

        fun stringToTime(time: String?): LocalDateTime {
            val formatter = DateTimeFormatter.ofPattern("HH:mm dd MM yyyy")
            return if (time == null) LocalDateTime.now() else LocalDateTime.parse(time, formatter)
        }

        fun dateToStringWithFormat(date: LocalDate): String {
            val formatter = DateTimeFormatter.ofPattern("dd MM yyyy");
            return date.format(formatter)
        }

        fun stringToDate(text: String): LocalDate {
            val formatter = DateTimeFormatter.ofPattern("dd MM yyyy")
            return LocalDate.parse(text, formatter)
        }

        fun countShortDuration(now: LocalDateTime, morning: LocalDateTime, evening: LocalDateTime): String {
            val duration1 = Duration.between(now, morning)
            val duration2 = Duration.between(now, evening)

            return if (duration1 > duration2)
                formatDuration(duration2)
            else
                formatDuration(duration1)
        }

        fun countLongDuration(now: LocalDate, futureString: String): String {
            val formatter = DateTimeFormatter.ofPattern("dd MM yyyy")
            val future = LocalDate.parse(futureString, formatter)
            val duration = Duration.between(now.atStartOfDay(), future.atStartOfDay())
            val days = duration.toDays()
            val daysText = if ((days > 20 || days == 1L) && days % 10 == 1L) "день" else if (days % 10 in 2..4) "дня" else "дней"
            return "Через $days $daysText"
        }

        private fun formatDuration(duration: Duration): String {
            val hours = abs(duration.toHours())
            val minutes = abs(duration.toMinutes() % 60)

            val hoursText = if ((hours > 20 || hours == 1L) && hours == 1L) "час" else if (hours in 2..4) "часа" else "часов"
//            val minutesText = if ((minutes > 20 || minutes == 1L) && minutes % 10 == 1L) "минуту" else if (minutes % 10 in 2..4) "минуты" else "минут"

            return "Через $hours $hoursText $minutes мин"
        }
    }
}