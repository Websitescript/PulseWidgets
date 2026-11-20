package com.pulse.widgets

import android.content.Context
import java.util.Calendar

object Prefs {
    private fun sp(c: Context) = c.getSharedPreferences("pulse", Context.MODE_PRIVATE)

    fun theme(c: Context): PulseTheme = PulseTheme.fromKey(sp(c).getString("theme", "teal"))
    fun setTheme(c: Context, t: PulseTheme) = sp(c).edit().putString("theme", t.key).apply()

    fun animBg(c: Context) = sp(c).getBoolean("anim_bg", true)
    fun setAnimBg(c: Context, v: Boolean) = sp(c).edit().putBoolean("anim_bg", v).apply()

    fun animRing(c: Context) = sp(c).getBoolean("anim_ring", true)
    fun setAnimRing(c: Context, v: Boolean) = sp(c).edit().putBoolean("anim_ring", v).apply()

    /** 0 = follow locale, 1 = Sunday, 2 = Monday */
    fun weekStart(c: Context) = sp(c).getInt("week_start", 0)
    fun setWeekStart(c: Context, v: Int) = sp(c).edit().putInt("week_start", v).apply()

    fun firstDow(c: Context): Int = when (weekStart(c)) {
        1 -> Calendar.SUNDAY
        2 -> Calendar.MONDAY
        else -> Calendar.getInstance().firstDayOfWeek
    }

    fun monthOffset(c: Context, id: Int) = sp(c).getInt("mo_$id", 0)
    fun setMonthOffset(c: Context, id: Int, v: Int) = sp(c).edit().putInt("mo_$id", v).apply()
    fun clearMonth(c: Context, id: Int) = sp(c).edit().remove("mo_$id").apply()
}
