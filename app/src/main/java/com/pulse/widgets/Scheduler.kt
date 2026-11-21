package com.pulse.widgets

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

object Scheduler {
    fun schedule(ctx: Context) {
        val app = ctx.applicationContext
        val am = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val flags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        val refresh = Intent(app, RefreshReceiver::class.java).setAction(RefreshReceiver.ACTION_REFRESH)

        // date rollover just after midnight
        val next = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 5)
            set(Calendar.MILLISECOND, 0)
        }
        am.set(AlarmManager.RTC, next.timeInMillis, PendingIntent.getBroadcast(app, 7, refresh, flags))

        // light periodic refresh: weather, wallpaper colours, light/dark flips
        am.setInexactRepeating(
            AlarmManager.RTC,
            System.currentTimeMillis() + AlarmManager.INTERVAL_FIFTEEN_MINUTES,
            AlarmManager.INTERVAL_FIFTEEN_MINUTES,
            PendingIntent.getBroadcast(app, 8, refresh, flags)
        )
    }
}
