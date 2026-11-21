package com.pulse.widgets

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.res.Configuration

enum class SizeClass { XS, TALL, WIDE, M, L, XL }

object Sizing {
    /** Current widget size in dp as reported by the launcher. */
    fun sizeDp(ctx: Context, mgr: AppWidgetManager, id: Int): Pair<Int, Int> {
        val o = mgr.getAppWidgetOptions(id)
        val land = ctx.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val w = if (land) o.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH) else o.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
        val h = if (land) o.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT) else o.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT)
        return if (w <= 0 || h <= 0) Pair(180, 180) else Pair(w, h)
    }

    /** Estimates the grid cells from dp size (assumes a 4-column home grid) and buckets them. */
    fun classOf(ctx: Context, wDp: Int, hDp: Int): SizeClass {
        val sw = ctx.resources.configuration.screenWidthDp.coerceAtLeast(320)
        val cell = (sw - 32) / 4f
        val c = (wDp / cell + 0.35f).toInt().coerceAtLeast(1)
        val r = (hDp / (cell * 1.15f) + 0.35f).toInt().coerceAtLeast(1)
        return when {
            c <= 1 && r <= 1 -> SizeClass.XS
            c <= 1 -> SizeClass.TALL
            r <= 1 -> SizeClass.WIDE
            c >= 4 && r == 2 -> SizeClass.L
            c >= 3 && r >= 3 -> SizeClass.XL
            else -> SizeClass.M
        }
    }
}
