package com.pulse.widgets

import android.content.Context
import android.widget.FrameLayout
import android.widget.RemoteViews

/**
 * Smooth (30 fps) second-hand sweep relies on a framework method of AnalogClock that not every
 * Android / ColorOS build exposes. We test it once by applying a RemoteViews locally; if the
 * framework rejects it we never send it to the launcher (which could break the widget).
 */
object Smooth {
    const val FPS = 30

    fun supported(ctx: Context): Boolean {
        when (Prefs.smoothState(ctx)) {
            1 -> return true
            2 -> return false
        }
        val ok = try {
            val rv = RemoteViews(ctx.packageName, R.layout.w_analog_xs)
            rv.setInt(R.id.analog, "setSecondsHandFps", FPS)
            rv.apply(ctx, FrameLayout(ctx))
            true
        } catch (e: Throwable) {
            false
        }
        Prefs.setSmoothState(ctx, if (ok) 1 else 2)
        return ok
    }
}
