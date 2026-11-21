package com.pulse.widgets

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader

class WpColors(val a1: Int, val a2: Int, val b1: Int, val b2: Int, val solid: Int, val accent: Int)

/** Builds widget backgrounds from the colours of the current wallpaper (works on every Android 8.1+ phone). */
object WallpaperBg {

    fun colors(ctx: Context, dark: Boolean): WpColors {
        var p = 0xFF3B82F6.toInt()
        var s = p
        var t = p
        try {
            val wc = WallpaperManager.getInstance(ctx).getWallpaperColors(WallpaperManager.FLAG_SYSTEM)
            if (wc != null) {
                p = wc.primaryColor.toArgb()
                s = wc.secondaryColor?.toArgb() ?: p
                t = wc.tertiaryColor?.toArgb() ?: s
            }
        } catch (e: Exception) {
            // keep defaults
        }
        val hp = hue(p)
        val hs = hue(s)
        val ht = hue(t)
        val sp = sat(p)
        val ss = sat(s)
        val st = sat(t)
        return if (dark) {
            val a1 = hsv(hp, sp.coerceIn(0.35f, 0.7f), 0.17f)
            val a2 = hsv(hs, ss.coerceIn(0.35f, 0.7f), 0.30f)
            WpColors(a1, a2, a2, hsv(ht, st.coerceIn(0.35f, 0.7f), 0.22f), hsv(hp, sp.coerceIn(0.25f, 0.6f), 0.15f), hsv(hp, 0.55f, 0.95f))
        } else {
            val a1 = hsv(hp, sp.coerceIn(0.08f, 0.2f), 0.97f)
            val a2 = hsv(hs, ss.coerceIn(0.12f, 0.3f), 0.90f)
            WpColors(a1, a2, a2, hsv(ht, st.coerceIn(0.1f, 0.25f), 0.95f), hsv(hp, sp.coerceIn(0.05f, 0.15f), 0.96f), hsv(hp, 0.85f, 0.45f))
        }
    }

    /** Returns (base, alt). solid = flat colour for the base; alt is always the second gradient. */
    fun render(ctx: Context, wDp: Int, hDp: Int, wc: WpColors, solid: Boolean): Pair<Bitmap, Bitmap> {
        val d = ctx.resources.displayMetrics.density
        val scale = 0.6f
        val w = (wDp * d * scale).toInt().coerceIn(48, 700)
        val h = (hDp * d * scale).toInt().coerceIn(48, 700)
        val r = 26f * d * scale
        val base = if (solid) draw(w, h, r, wc.solid, wc.solid, false) else draw(w, h, r, wc.a1, wc.a2, false)
        val alt = draw(w, h, r, wc.b1, wc.b2, true)
        return Pair(base, alt)
    }

    private fun draw(w: Int, h: Int, r: Float, c1: Int, c2: Int, flip: Boolean): Bitmap {
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val cv = Canvas(bmp)
        val p = Paint(Paint.ANTI_ALIAS_FLAG)
        p.shader = if (flip) LinearGradient(w.toFloat(), 0f, 0f, h.toFloat(), c1, c2, Shader.TileMode.CLAMP)
        else LinearGradient(0f, 0f, w.toFloat(), h.toFloat(), c1, c2, Shader.TileMode.CLAMP)
        val rect = RectF(0f, 0f, w.toFloat(), h.toFloat())
        cv.drawRoundRect(rect, r, r, p)
        val s = Paint(Paint.ANTI_ALIAS_FLAG)
        s.style = Paint.Style.STROKE
        s.strokeWidth = 1.5f
        s.color = 0x26FFFFFF
        cv.drawRoundRect(RectF(0.75f, 0.75f, w - 0.75f, h - 0.75f), r, r, s)
        return bmp
    }

    private fun hue(c: Int): Float {
        val a = FloatArray(3)
        Color.colorToHSV(c, a)
        return a[0]
    }

    private fun sat(c: Int): Float {
        val a = FloatArray(3)
        Color.colorToHSV(c, a)
        return a[1]
    }

    private fun hsv(h: Float, s: Float, v: Float): Int =
        Color.HSVToColor(floatArrayOf(h, s.coerceIn(0f, 1f), v.coerceIn(0f, 1f)))
}
