package com.pulse.widgets

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

object MidnightScheduler {
    fun schedule(ctx: Context) {
        val am = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val next = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 5)
            set(Calendar.MILLISECOND, 0)
        }
        val pi = PendingIntent.getBroadcast(
            ctx, 7,
            Intent(ctx, RefreshReceiver::class.java).setAction(RefreshReceiver.ACTION_REFRESH),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        am.set(AlarmManager.RTC, next.timeInMillis, pi) // inexact: no special permission needed
    }
}
