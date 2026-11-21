package com.pulse.widgets

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color

/**
 * Everything the builder needs to paint one widget.
 * text1 / text2 / accent are null when the layout's own (live, night-aware) colours should be kept.
 */
class Look(
    val baseRes: Int,
    val altRes: Int,
    val baseBmp: Bitmap?,
    val altBmp: Bitmap?,
    val alpha: Int,
    val glass: Boolean,
    val animate: Boolean,
    val text1: Int?,
    val text2: Int?,
    val accent: Int?,
    val accentInt: Int,
    val onAccent: Int,
)

object Looks {
    fun isNight(ctx: Context): Boolean =
        (ctx.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

    fun resolve(ctx: Context, wDp: Int, hDp: Int): Look {
        val src = Prefs.source(ctx)
        val app = Prefs.appearance(ctx)
        val dark = when (app) {
            1 -> false
            2 -> true
            else -> isNight(ctx)
        }
        val style = Prefs.bgStyle(ctx)
        val glass = style == 2
        val opacity = if (glass) Prefs.glassOpacity(ctx) else Prefs.opacity(ctx)
        val alpha = (opacity * 255 / 100).coerceIn(30, 255)
        val animate = style == 1 && Prefs.animGradient(ctx)
        val forced1 = if (dark) Palettes.TEXT1_DARK else Palettes.TEXT1_LIGHT
        val forced2 = if (dark) Palettes.TEXT2_DARK else Palettes.TEXT2_LIGHT

        if (src == Prefs.SRC_WALLPAPER) {
            val wc = WallpaperBg.colors(ctx, dark)
            val bmps = WallpaperBg.render(ctx, wDp, hDp, wc, style != 1)
            return Look(0, 0, bmps.first, bmps.second, alpha, glass, animate, forced1, forced2, wc.accent, wc.accent, onAccent(wc.accent))
        }

        val palette: Palettes.P? = if (src >= 2) Palettes.ALL.getOrNull(src - 2) else null
        val name = palette?.key ?: "sys"
        val mode = if (app == 0) "auto" else if (dark) "dark" else "light"
        val baseKind = if (style == 1) "ga" else "solid"
        val base = drawable(ctx, "bg_" + name + "_" + mode + "_" + baseKind)
        val alt = drawable(ctx, "bg_" + name + "_" + mode + "_gb")
        val live = palette == null && app == 0 // fully system driven: let the layout colours follow dark/light by themselves
        val accentInt: Int = when {
            palette != null -> if (dark) palette.darkAccent else palette.lightAccent
            app == 0 -> ctx.getColor(R.color.w_accent)
            dark -> ctx.getColor(R.color.sysd_accent)
            else -> ctx.getColor(R.color.sysl_accent)
        }
        return Look(
            base, alt, null, null, alpha, glass, animate,
            if (app == 0) null else forced1,
            if (app == 0) null else forced2,
            if (live) null else accentInt,
            accentInt, onAccent(accentInt)
        )
    }

    private fun drawable(ctx: Context, name: String): Int {
        val id = ctx.resources.getIdentifier(name, "drawable", ctx.packageName)
        return if (id == 0) R.drawable.bg_sys_auto_ga else id
    }

    fun onAccent(c: Int): Int {
        val l = (0.299 * Color.red(c) + 0.587 * Color.green(c) + 0.114 * Color.blue(c)) / 255.0
        return if (l > 0.6) 0xFF0B1220.toInt() else 0xFFFFFFFF.toInt()
    }
}
