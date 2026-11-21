package com.pulse.widgets

import android.content.Context
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.Locale
import kotlin.math.roundToInt

/** Weather from open-meteo.com (free, no API key). */
object Weather {
    class Data(val temp: Float, val code: Int, val day: Boolean, val hi: Float, val lo: Float)
    class Place(val name: String, val lat: Float, val lon: Float)

    private const val MAX_AGE_MS = 30L * 60L * 1000L

    fun cached(ctx: Context): Data? {
        if (Prefs.wxTime(ctx) == 0L) return null
        return Data(Prefs.wxTemp(ctx), Prefs.wxCode(ctx), Prefs.wxDay(ctx), Prefs.wxHi(ctx), Prefs.wxLo(ctx))
    }

    fun isStale(ctx: Context): Boolean = System.currentTimeMillis() - Prefs.wxTime(ctx) > MAX_AGE_MS

    /** Blocking network call: run off the main thread. */
    fun refresh(ctx: Context): Boolean {
        return try {
            val url = "https://api.open-meteo.com/v1/forecast?latitude=" + f4(Prefs.lat(ctx)) + "&longitude=" + f4(Prefs.lon(ctx)) +
                "&current=temperature_2m,weather_code,is_day&daily=temperature_2m_max,temperature_2m_min&timezone=auto&forecast_days=1"
            val j = JSONObject(get(url))
            val cur = j.getJSONObject("current")
            val daily = j.getJSONObject("daily")
            Prefs.saveWeather(
                ctx,
                cur.getDouble("temperature_2m").toFloat(),
                cur.getInt("weather_code"),
                cur.optInt("is_day", 1) == 1,
                daily.getJSONArray("temperature_2m_max").getDouble(0).toFloat(),
                daily.getJSONArray("temperature_2m_min").getDouble(0).toFloat()
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    /** Blocking: look up a city name. */
    fun geocode(query: String): Place? {
        return try {
            val url = "https://geocoding-api.open-meteo.com/v1/search?name=" + URLEncoder.encode(query, "UTF-8") + "&count=1&language=en&format=json"
            val arr = JSONObject(get(url)).optJSONArray("results")
            if (arr == null || arr.length() == 0) {
                null
            } else {
                val o = arr.getJSONObject(0)
                Place(o.getString("name"), o.getDouble("latitude").toFloat(), o.getDouble("longitude").toFloat())
            }
        } catch (e: Exception) {
            null
        }
    }

    fun fmt(ctx: Context, celsius: Float): String {
        val v = if (Prefs.fahrenheit(ctx)) celsius * 9f / 5f + 32f else celsius
        return v.roundToInt().toString() + "°"
    }

    /** WMO weather code -> (emoji, short description). */
    fun describe(code: Int, day: Boolean): Pair<String, String> = when (code) {
        0 -> Pair(if (day) "☀️" else "🌙", "Clear")
        1 -> Pair(if (day) "🌤️" else "🌙", "Mostly clear")
        2 -> Pair("⛅", "Partly cloudy")
        3 -> Pair("☁️", "Overcast")
        45, 48 -> Pair("🌫️", "Fog")
        51, 53, 55, 56, 57 -> Pair("🌦️", "Drizzle")
        61, 63, 65, 66, 67 -> Pair("🌧️", "Rain")
        71, 73, 75, 77 -> Pair("❄️", "Snow")
        80, 81, 82 -> Pair("🌦️", "Showers")
        85, 86 -> Pair("🌨️", "Snow showers")
        95, 96, 99 -> Pair("⛈️", "Thunderstorm")
        else -> Pair("🌡️", "Weather")
    }

    private fun f4(v: Float): String = String.format(Locale.US, "%.4f", v)

    private fun get(url: String): String {
        val conn = URL(url).openConnection() as HttpURLConnection
        conn.connectTimeout = 8000
        conn.readTimeout = 8000
        try {
            return conn.inputStream.bufferedReader().use { it.readText() }
        } finally {
            conn.disconnect()
        }
    }
}
