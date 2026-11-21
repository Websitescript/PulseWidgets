package com.pulse.widgets

import android.content.Context
import java.util.Calendar

object Prefs {
    const val SRC_SYSTEM = 0
    const val SRC_WALLPAPER = 1 // palettes start at 2

    private fun sp(c: Context) = c.getSharedPreferences("pulse", Context.MODE_PRIVATE)

    fun source(c: Context) = sp(c).getInt("source", SRC_SYSTEM)
    fun setSource(c: Context, v: Int) = sp(c).edit().putInt("source", v).apply()

    /** 0 = follow system, 1 = light, 2 = dark */
    fun appearance(c: Context) = sp(c).getInt("appearance", 0)
    fun setAppearance(c: Context, v: Int) = sp(c).edit().putInt("appearance", v).apply()

    /** 0 = solid, 1 = gradient, 2 = frosted glass */
    fun bgStyle(c: Context) = sp(c).getInt("bg_style", 1)
    fun setBgStyle(c: Context, v: Int) = sp(c).edit().putInt("bg_style", v).apply()

    fun opacity(c: Context) = sp(c).getInt("opacity", 100)
    fun setOpacity(c: Context, v: Int) = sp(c).edit().putInt("opacity", v).apply()
    fun glassOpacity(c: Context) = sp(c).getInt("glass_opacity", 55)
    fun setGlassOpacity(c: Context, v: Int) = sp(c).edit().putInt("glass_opacity", v).apply()

    fun animGradient(c: Context) = sp(c).getBoolean("anim_gradient", true)
    fun setAnimGradient(c: Context, v: Boolean) = sp(c).edit().putBoolean("anim_gradient", v).apply()

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

    // ---- weather
    fun city(c: Context): String = sp(c).getString("wx_city", "Panvel") ?: "Panvel"
    fun lat(c: Context) = sp(c).getFloat("wx_lat", 18.9894f)
    fun lon(c: Context) = sp(c).getFloat("wx_lon", 73.1175f)
    fun setPlace(c: Context, name: String, lat: Float, lon: Float) =
        sp(c).edit().putString("wx_city", name).putFloat("wx_lat", lat).putFloat("wx_lon", lon).putLong("wx_time", 0L).apply()

    fun fahrenheit(c: Context) = sp(c).getBoolean("wx_f", false)
    fun setFahrenheit(c: Context, v: Boolean) = sp(c).edit().putBoolean("wx_f", v).apply()

    fun wxTime(c: Context) = sp(c).getLong("wx_time", 0L)
    fun saveWeather(c: Context, temp: Float, code: Int, day: Boolean, hi: Float, lo: Float) =
        sp(c).edit().putFloat("wx_temp", temp).putInt("wx_code", code).putBoolean("wx_day", day)
            .putFloat("wx_hi", hi).putFloat("wx_lo", lo).putLong("wx_time", System.currentTimeMillis()).apply()
    fun wxTemp(c: Context) = sp(c).getFloat("wx_temp", 0f)
    fun wxCode(c: Context) = sp(c).getInt("wx_code", 0)
    fun wxDay(c: Context) = sp(c).getBoolean("wx_day", true)
    fun wxHi(c: Context) = sp(c).getFloat("wx_hi", 0f)
    fun wxLo(c: Context) = sp(c).getFloat("wx_lo", 0f)
}
