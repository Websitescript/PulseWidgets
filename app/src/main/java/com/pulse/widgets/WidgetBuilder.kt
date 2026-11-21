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

    private val T1 = intArrayOf(R.id.tp_1, R.id.tp_2, R.id.wx_temp, R.id.wx_icon)
    private val T2 = intArrayOf(R.id.ts_1, R.id.ts_2, R.id.ts_3, R.id.wx_desc, R.id.wx_range)
    private val TA = intArrayOf(R.id.ta_1, R.id.ta_2)

    /** Builds the RemoteViews for a widget of the given size (dp). The layout is chosen from the size. */
    fun build(ctx: Context, kind: WidgetKind, wDp: Int, hDp: Int, widgetId: Int): RemoteViews {
        val sc = Sizing.classOf(ctx, wDp, hDp)
        val v = Variants.of(kind, sc)
        val rv = RemoteViews(ctx.packageName, v.layout)
        val look = Looks.resolve(ctx, wDp, hDp)
        paint(ctx, rv, look)
        if (v.wx) weather(ctx, rv)
        if (v.week) week(ctx, rv, look)
        if (v.month) month(ctx, rv, look, widgetId)
        rv.setOnClickPendingIntent(R.id.root, if (kind == WidgetKind.CALENDAR) calendarPi(ctx) else clockPi(ctx))
        return rv
    }

    // ---------------------------------------------------------------- background + colours
    private fun paint(ctx: Context, rv: RemoteViews, look: Look) {
        val bb = look.baseBmp
        if (bb != null) {
            rv.setImageViewBitmap(R.id.bg_base, bb)
            val ab = look.altBmp
            if (ab != null) rv.setImageViewBitmap(R.id.bg_alt, ab)
        } else {
            rv.setImageViewResource(R.id.bg_base, look.baseRes)
            rv.setImageViewResource(R.id.bg_alt, look.altRes)
        }
        rv.setInt(R.id.bg_base, "setImageAlpha", look.alpha)
        rv.setInt(R.id.bg_alt, "setImageAlpha", look.alpha)
        rv.setViewVisibility(R.id.bg_flip, if (look.animate) View.VISIBLE else View.GONE)
        rv.setViewVisibility(R.id.bg_glass, if (look.glass) View.VISIBLE else View.GONE)

        val t1 = look.text1
        if (t1 != null) for (id in T1) rv.setTextColor(id, t1)
        val t2 = look.text2
        if (t2 != null) for (id in T2) rv.setTextColor(id, t2)
        val ac = look.accent
        if (ac != null) for (id in TA) rv.setTextColor(id, ac)
        rv.setInt(R.id.bar_1, "setBackgroundColor", look.accentInt)

        if (Prefs.smoothHand(ctx) && Smooth.supported(ctx)) rv.setInt(R.id.analog, "setSecondsHandFps", Smooth.FPS)
    }

    // ---------------------------------------------------------------- weather
    private fun weather(ctx: Context, rv: RemoteViews) {
        val d = Weather.cached(ctx)
        if (d == null) {
            rv.setTextViewText(R.id.wx_icon, "🌡️")
            rv.setTextViewText(R.id.wx_temp, "--°")
            rv.setTextViewText(R.id.wx_desc, Prefs.city(ctx))
            rv.setTextViewText(R.id.wx_range, "")
            return
        }
        val dsc = Weather.describe(d.code, d.day)
        rv.setTextViewText(R.id.wx_icon, dsc.first)
        rv.setTextViewText(R.id.wx_temp, Weather.fmt(ctx, d.temp))
        rv.setTextViewText(R.id.wx_desc, dsc.second)
        rv.setTextViewText(R.id.wx_range, "H " + Weather.fmt(ctx, d.hi) + "  L " + Weather.fmt(ctx, d.lo))
    }

    // ---------------------------------------------------------------- week strip
    private fun week(ctx: Context, rv: RemoteViews, look: Look) {
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
            val nw = rid(ctx, "wk_nw_$i")
            val nt = rid(ctx, "wk_nt_$i")
            val hl = rid(ctx, "wk_hl_$i")
            val txt = cal.get(Calendar.DAY_OF_MONTH).toString()

            rv.setTextViewText(name, names[dow].take(3).uppercase(Locale.getDefault()))
            for (v in intArrayOf(num, nw, nt)) rv.setTextViewText(v, txt)
            rv.setViewVisibility(num, if (!isToday && !weekend) View.VISIBLE else View.GONE)
            rv.setViewVisibility(nw, if (!isToday && weekend) View.VISIBLE else View.GONE)
            rv.setViewVisibility(nt, if (isToday) View.VISIBLE else View.GONE)
            rv.setTextColor(nt, look.onAccent)
            val t1 = look.text1
            if (t1 != null) rv.setTextColor(num, t1)
            val t2 = look.text2
            if (t2 != null) rv.setTextColor(name, t2)
            val ac = look.accent
            if (ac != null) rv.setTextColor(nw, ac)

            rv.setViewVisibility(hl, if (isToday) View.VISIBLE else View.GONE)
            rv.setInt(rid(ctx, "wk_hl_${i}_a"), "setColorFilter", look.accentInt)
            rv.setInt(rid(ctx, "wk_hl_${i}_b"), "setColorFilter", look.accentInt)

            val pi = dayPi(ctx, 200 + i, cal.timeInMillis)
            for (v in intArrayOf(num, nw, nt)) rv.setOnClickPendingIntent(v, pi)
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    // ---------------------------------------------------------------- month grid
    private fun month(ctx: Context, rv: RemoteViews, look: Look, wid: Int) {
        val off = Prefs.monthOffset(ctx, wid)
        val first = Prefs.firstDow(ctx)
        val today = Calendar.getInstance()
        val cal = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 12)
            add(Calendar.MONTH, off)
        }
        rv.setTextViewText(R.id.month_title, SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(cal.time))
        val t1 = look.text1
        if (t1 != null) rv.setTextColor(R.id.month_title, t1)
        val ac = look.accent
        if (ac != null) {
            rv.setTextColor(R.id.nav_prev, ac)
            rv.setTextColor(R.id.nav_next, ac)
        }
        rv.setOnClickPendingIntent(R.id.nav_prev, navPi(ctx, wid, -1))
        rv.setOnClickPendingIntent(R.id.nav_next, navPi(ctx, wid, 1))
        rv.setOnClickPendingIntent(R.id.month_title, navPi(ctx, wid, 0))

        val names = DateFormatSymbols.getInstance().shortWeekdays
        for (i in 0..6) {
            val dow = (first - 1 + i) % 7 + 1
            val h = rid(ctx, "mh_$i")
            rv.setTextViewText(h, names[dow].take(3).uppercase(Locale.getDefault()))
            val t2 = look.text2
            if (t2 != null) rv.setTextColor(h, t2)
        }

        val monthIdx = cal.get(Calendar.MONTH)
        cal.add(Calendar.DAY_OF_MONTH, -((cal.get(Calendar.DAY_OF_WEEK) - first + 7) % 7))
        for (i in 0 until 42) {
            val inMonth = cal.get(Calendar.MONTH) == monthIdx
            val isToday = sameDay(cal, today)
            val dow = cal.get(Calendar.DAY_OF_WEEK)
            val weekend = dow == Calendar.SATURDAY || dow == Calendar.SUNDAY
            val d = rid(ctx, "d_$i")
            val dd = rid(ctx, "dd_$i")
            val dw = rid(ctx, "dw_$i")
            val dt = rid(ctx, "dt_$i")
            val txt = cal.get(Calendar.DAY_OF_MONTH).toString()
            for (v in intArrayOf(d, dd, dw, dt)) rv.setTextViewText(v, txt)

            rv.setViewVisibility(d, if (inMonth && !isToday && !weekend) View.VISIBLE else View.GONE)
            rv.setViewVisibility(dd, if (!inMonth && !isToday) View.VISIBLE else View.GONE)
            rv.setViewVisibility(dw, if (inMonth && !isToday && weekend) View.VISIBLE else View.GONE)
            rv.setViewVisibility(dt, if (isToday) View.VISIBLE else View.GONE)
            rv.setTextColor(dt, look.onAccent)
            if (t1 != null) rv.setTextColor(d, t1)
            val t2 = look.text2
            if (t2 != null) rv.setTextColor(dd, t2)
            if (ac != null) rv.setTextColor(dw, ac)

            val hl = rid(ctx, "hl_$i")
            rv.setViewVisibility(hl, if (isToday) View.VISIBLE else View.GONE)
            rv.setInt(hl, "setColorFilter", look.accentInt)
            rv.setOnClickPendingIntent(rid(ctx, "c_$i"), dayPi(ctx, 300 + i, cal.timeInMillis))
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    // ---------------------------------------------------------------- helpers
    private fun rid(ctx: Context, name: String): Int = ctx.resources.getIdentifier(name, "id", ctx.packageName)

    private fun sameDay(a: Calendar, b: Calendar): Boolean =
        a.get(Calendar.YEAR) == b.get(Calendar.YEAR) && a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)

    private const val FLAGS = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

    private fun clockPi(ctx: Context): PendingIntent =
        PendingIntent.getActivity(ctx, 1, Intent(AlarmClock.ACTION_SHOW_ALARMS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), FLAGS)

    private fun calendarPi(ctx: Context): PendingIntent =
        PendingIntent.getActivity(
            ctx, 2,
            Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_CALENDAR).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            FLAGS
        )

    private fun dayPi(ctx: Context, code: Int, millis: Long): PendingIntent =
        PendingIntent.getActivity(
            ctx, code,
            Intent(Intent.ACTION_VIEW, Uri.parse("content://com.android.calendar/time/$millis")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            FLAGS
        )

    private fun navPi(ctx: Context, wid: Int, delta: Int): PendingIntent =
        PendingIntent.getBroadcast(
            ctx, 0,
            Intent(ctx, RefreshReceiver::class.java)
                .setAction(RefreshReceiver.ACTION_NAV)
                .setData(Uri.parse("pulse://nav/$wid/$delta"))
                .putExtra(RefreshReceiver.EXTRA_ID, wid)
                .putExtra(RefreshReceiver.EXTRA_DELTA, delta),
            FLAGS
        )
}
