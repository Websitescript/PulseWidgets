package com.pulse.widgets

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.AlarmClock
import android.view.View
import android.widget.RemoteViews
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object WidgetBuilder {

    private val TP = intArrayOf(R.id.tp_1, R.id.tp_2)
    private val TS = intArrayOf(R.id.ts_1, R.id.ts_2, R.id.ts_3)
    private val TA = intArrayOf(R.id.ta_1, R.id.ta_2, R.id.ta_3)

    fun build(ctx: Context, kind: WidgetKind, widgetId: Int): RemoteViews {
        val rv = RemoteViews(ctx.packageName, kind.layout)
        val t = Prefs.theme(ctx)
        paint(ctx, rv, t)
        when (kind) {
            WidgetKind.CAL_CARD_2X2 -> yearProgress(rv)
            WidgetKind.CAL_WEEK_4X2 -> week(ctx, rv, t)
            WidgetKind.CAL_MONTH_4X4 -> month(ctx, rv, t, widgetId)
            else -> Unit
        }
        rv.setOnClickPendingIntent(R.id.root, if (kind.isClock) clockPi(ctx) else calendarPi(ctx))
        return rv
    }

    // ---------------------------------------------------------------- theme
    private fun paint(ctx: Context, rv: RemoteViews, t: PulseTheme) {
        rv.setImageViewResource(R.id.bg_base, t.bgBase)
        rv.setImageViewResource(R.id.bg_alt, t.bgAlt)
        rv.setViewVisibility(R.id.bg_flip, if (Prefs.animBg(ctx)) View.VISIBLE else View.GONE)
        TP.forEach { rv.setTextColor(it, t.primary) }
        TS.forEach { rv.setTextColor(it, t.secondary) }
        TA.forEach { rv.setTextColor(it, t.accent) }
        rv.setInt(R.id.bar_1, "setBackgroundColor", t.accent)
        val ringOn = Prefs.animRing(ctx)
        for (th in PulseTheme.values()) {
            rv.setViewVisibility(th.ringId, if (ringOn && th == t) View.VISIBLE else View.GONE)
            rv.setViewVisibility(th.barId, if (th == t) View.VISIBLE else View.GONE)
        }
    }

    // ---------------------------------------------------------------- calendar card
    private fun yearProgress(rv: RemoteViews) {
        val cal = Calendar.getInstance()
        val day = cal.get(Calendar.DAY_OF_YEAR)
        val total = cal.getActualMaximum(Calendar.DAY_OF_YEAR)
        for (th in PulseTheme.values()) rv.setProgressBar(th.barId, total, day, false)
        rv.setTextViewText(R.id.year_txt, "DAY $day / $total")
    }

    // ---------------------------------------------------------------- week strip
    private fun week(ctx: Context, rv: RemoteViews, t: PulseTheme) {
        val first = Prefs.firstDow(ctx)
        val today = Calendar.getInstance()
        val cal = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 12) }
        cal.add(Calendar.DAY_OF_MONTH, -((cal.get(Calendar.DAY_OF_WEEK) - first + 7) % 7))
        val names = DateFormatSymbols.getInstance().shortWeekdays
        for (i in 0..6) {
            val dow = cal.get(Calendar.DAY_OF_WEEK)
            val isToday = sameDay(cal, today)
            val weekend = dow == Calendar.SATURDAY || dow == Calendar.SUNDAY
            val name = rid(ctx, "wk_name_$i")
            val num = rid(ctx, "wk_num_$i")
            val hl = rid(ctx, "wk_hl_$i")
            rv.setTextViewText(name, names[dow].take(3).uppercase(Locale.getDefault()))
            rv.setTextColor(name, if (isToday) t.accent else t.secondary)
            rv.setTextViewText(num, cal.get(Calendar.DAY_OF_MONTH).toString())
            rv.setTextColor(num, if (isToday) t.onAccent else if (weekend) t.accent else t.primary)
            rv.setViewVisibility(hl, if (isToday) View.VISIBLE else View.GONE)
            rv.setImageViewResource(rid(ctx, "wk_hl_${i}_a"), t.circle)
            rv.setImageViewResource(rid(ctx, "wk_hl_${i}_b"), t.halo)
            rv.setOnClickPendingIntent(num, dayPi(ctx, 200 + i, cal.timeInMillis))
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    // ---------------------------------------------------------------- month grid
    private fun month(ctx: Context, rv: RemoteViews, t: PulseTheme, wid: Int) {
        val off = Prefs.monthOffset(ctx, wid)
        val first = Prefs.firstDow(ctx)
        val today = Calendar.getInstance()
        val cal = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 12)
            add(Calendar.MONTH, off)
        }
        rv.setTextViewText(R.id.month_title, SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(cal.time))
        rv.setTextColor(R.id.month_title, if (off == 0) t.primary else t.accent)
        rv.setTextColor(R.id.nav_prev, t.accent)
        rv.setTextColor(R.id.nav_next, t.accent)
        rv.setOnClickPendingIntent(R.id.nav_prev, navPi(ctx, wid, -1))
        rv.setOnClickPendingIntent(R.id.nav_next, navPi(ctx, wid, 1))
        rv.setOnClickPendingIntent(R.id.month_title, navPi(ctx, wid, 0))

        val names = DateFormatSymbols.getInstance().shortWeekdays
        for (i in 0..6) {
            val dow = (first - 1 + i) % 7 + 1
            val h = rid(ctx, "mh_$i")
            rv.setTextViewText(h, names[dow].take(3).uppercase(Locale.getDefault()))
            rv.setTextColor(h, t.secondary)
        }

        val monthIdx = cal.get(Calendar.MONTH)
        cal.add(Calendar.DAY_OF_MONTH, -((cal.get(Calendar.DAY_OF_WEEK) - first + 7) % 7))
        for (i in 0 until 42) {
            val inMonth = cal.get(Calendar.MONTH) == monthIdx
            val isToday = sameDay(cal, today)
            val dow = cal.get(Calendar.DAY_OF_WEEK)
            val weekend = dow == Calendar.SATURDAY || dow == Calendar.SUNDAY
            val d = rid(ctx, "d_$i")
            val hl = rid(ctx, "hl_$i")
            rv.setTextViewText(d, cal.get(Calendar.DAY_OF_MONTH).toString())
            rv.setTextColor(
                d, when {
                    isToday -> t.onAccent
                    !inMonth -> t.dim
                    weekend -> t.accent
                    else -> t.primary
                }
            )
            rv.setViewVisibility(hl, if (isToday) View.VISIBLE else View.GONE)
            rv.setImageViewResource(hl, t.circle)
            rv.setOnClickPendingIntent(rid(ctx, "c_$i"), dayPi(ctx, 300 + i, cal.timeInMillis))
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    // ---------------------------------------------------------------- helpers
    private fun rid(ctx: Context, name: String) = ctx.resources.getIdentifier(name, "id", ctx.packageName)

    private fun sameDay(a: Calendar, b: Calendar) =
        a.get(Calendar.YEAR) == b.get(Calendar.YEAR) && a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)

    private const val FLAGS = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

    private fun clockPi(ctx: Context): PendingIntent =
        PendingIntent.getActivity(ctx, 1, Intent(AlarmClock.ACTION_SHOW_ALARMS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), FLAGS)

    private fun calendarPi(ctx: Context): PendingIntent =
        PendingIntent.getActivity(
            ctx, 2,
            Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_CALENDAR)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), FLAGS
        )

    private fun dayPi(ctx: Context, code: Int, millis: Long): PendingIntent =
        PendingIntent.getActivity(
            ctx, code,
            Intent(Intent.ACTION_VIEW, Uri.parse("content://com.android.calendar/time/$millis"))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), FLAGS
        )

    private fun navPi(ctx: Context, wid: Int, delta: Int): PendingIntent =
        PendingIntent.getBroadcast(
            ctx, 0,
            Intent(ctx, RefreshReceiver::class.java)
                .setAction(RefreshReceiver.ACTION_NAV)
                .setData(Uri.parse("pulse://nav/$wid/$delta"))
                .putExtra(RefreshReceiver.EXTRA_ID, wid)
                .putExtra(RefreshReceiver.EXTRA_DELTA, delta), FLAGS
        )
}
