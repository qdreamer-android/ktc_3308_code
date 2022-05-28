package com.pwong.library.utils

import android.text.TextUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author android
 * @date 2019/2/15
 */
object TimeUtil {

    const val TIME_FORMAT_DEFAULT = "yyyy-MM-dd HH:mm:ss"
    const val TIME_FORMAT_MONTH_TIME = "MM-dd HH:mm"
    const val TIME_FORMAT_YEAR = "yyyy-MM-dd"
    const val TIME_FORMAT_YEAR_TIME = "yyyy-MM-dd HH:mm"
    const val TIME_FORMAT_HOUR = "HH:mm"
    const val TIME_FORMAT_TODAY_HOUR = "今天 HH:mm"
    const val TIME_FORMAT_YESTERDAY_HOUR = "昨天 HH:mm"
    const val TIME_FORMAT_COMPLETE = "yyyy-MM-dd'T'HH:mm:ss.SSS'+0000'"
    const val TIME_FORMAT_NO_CHAR = "yyyyMMddHHmmssSSS"
    const val TIME_FORMAT_YEAR_SECOND_NO_CHAR = "yyyyMMddHHmmss"

    const val TIME_HOUR_24 = 24 * 60 * 60 * 1000

    fun getCompleteTime() = stampToDate(System.currentTimeMillis(), TIME_FORMAT_NO_CHAR)

    fun dateToStamp(time: String) = dateToStamp(time, TIME_FORMAT_DEFAULT)

    /**
     * 将时间转换为时间戳
     */
    fun dateToStamp(time: String, format: String): Long {
        val simpleDateFormat = if (TextUtils.isEmpty(format)) {
            SimpleDateFormat(TIME_FORMAT_DEFAULT)
        } else {
            SimpleDateFormat(format)
        }
        val date: Date = simpleDateFormat.parse(time)
        return date.time
    }

    fun stampToDate() = stampToDate(System.currentTimeMillis(), TIME_FORMAT_DEFAULT)

    fun stampToDate(time: Long) = stampToDate(time, TIME_FORMAT_DEFAULT)

    fun stampToDate(format: String) = stampToDate(System.currentTimeMillis(), format)

    /**
     * 时间戳转换为时间
     */
    fun stampToDate(time: Long, format: String): String {
        val simpleDateFormat = if (TextUtils.isEmpty(format)) {
            SimpleDateFormat(TIME_FORMAT_DEFAULT)
        } else {
            SimpleDateFormat(format)
        }
        val date = Date(time)
        return simpleDateFormat.format(date)
    }

    fun formatDynamicTime(stamp: Long): String {
        if (stamp <= 0) return ""
        return formatDynamicTime(stampToDate(stamp, TIME_FORMAT_DEFAULT), TIME_FORMAT_DEFAULT)
    }

    fun formatDynamicTime(time: String?, format: String): String {
        if (TextUtils.isEmpty(time)) return ""

        val timeStamp = dateToStamp(time!!, format)

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"))
        val toYear = calendar.get(Calendar.YEAR)
        val toMonth = calendar.get(Calendar.MONTH)
        val toDay = calendar.get(Calendar.DAY_OF_MONTH)

        val today0ClockTimeStamp = dateToStamp("$toYear-${toMonth + 1}-$toDay 00:00:00")

        val isInside24Hour = (today0ClockTimeStamp - timeStamp) in 0..TIME_HOUR_24

        val date = Date(timeStamp)
        val year = date.year + 1900
        val month = date.month
        val day = date.date

        val timeFormat = if (isInside24Hour) {
            TIME_FORMAT_YESTERDAY_HOUR
        } else {
            if (year == toYear) {  // 今年
                if (month == toMonth) {     // 本月
                    if (day == toDay) {     // 当天
                        TIME_FORMAT_TODAY_HOUR
                    } else {
                        TIME_FORMAT_MONTH_TIME
                    }
                } else {
                    TIME_FORMAT_MONTH_TIME
                }
            } else {
                TIME_FORMAT_YEAR_TIME
            }
        }

        val format = SimpleDateFormat(timeFormat)
        return format.format(date)
    }

    /**
     * 获取订单剩余时间
     */
    fun getRemainTime(remainTime: Long, separatorHour: String? = ":", separatorMin: String? = ":"): String {
        var sec = remainTime / 1000     // 秒
        var min = sec / 60      // 分
        sec %= 60   // 格式化后的 秒
        val hour = min / 60
        return if (hour > 0) {
            min %= 60   // 格式化后的 分
            String.format("%02d$separatorHour%02d$separatorMin%02d", hour, min, sec)
        } else {
            String.format("%02d$separatorMin%02d", min, sec)
        }
    }

    fun getRemainTimeWithMills(remainTime: Long): String {
        val mills = remainTime % 1000   // 毫秒
        var sec = remainTime / 1000     // 秒
//        var min = sec / 60      // 分
//        sec %= 60   // 格式化后的 秒
//        val hour = min / 60
//        return if (hour > 0) {
//            min %= 60   // 格式化后的 分
//            String.format("%02d:%02d:%02d.%03d", hour, min, sec, mills)
//        } else {
            return String.format("%02d.%03d", sec, mills)
//        }
    }

}