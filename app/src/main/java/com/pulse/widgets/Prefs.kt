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

    /** 0 = gradient, 1 = frosted glass */
    fun bgStyle(c: Context) = sp(c).getInt("bg_style", 0)
    fun setBgStyle(c: Context, v: Int) = sp(c).edit().putInt("bg_style", v).apply()

    /** glass opacity slider, 0..100 */
    fun glassLevel(c: Context) = sp(c).getInt("glass_level", 55)
    fun setGlassLevel(c: Context, v: Int) = sp(c).edit().putInt("glass_level", v).apply()
    fun glassAlpha(c: Context) = 60 + (glassLevel(c) * 1.7f).toInt()

    fun smoothHand(c: Context) = sp(c).getBoolean("smooth_hand", true)
    fun setSmoothHand(c: Context, v: Boolean) = sp(c).edit().putBoolean("smooth_hand", v).apply()

    /** 0 = unknown, 1 = supported, 2 = not supported */
    fun smoothState(c: Context) = sp(c).getInt("smooth_state", 0)
    fun setSmoothState(c: Context, v: Int) = sp(c).edit().putInt("smooth_state", v).apply()

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
