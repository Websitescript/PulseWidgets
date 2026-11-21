package com.pulse.widgets

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context

object WidgetUpdater {
    fun update(ctx: Context, mgr: AppWidgetManager, kind: WidgetKind, id: Int) {
        try {
            val size = Sizing.sizeDp(ctx, mgr, id)
            mgr.updateAppWidget(id, WidgetBuilder.build(ctx, kind, size.first, size.second, id))
        } catch (e: Exception) {
            // never let one widget crash the receiver
        }
    }

    fun updateOne(ctx: Context, kind: WidgetKind, id: Int) =
        update(ctx, AppWidgetManager.getInstance(ctx), kind, id)

    fun updateAll(ctx: Context) {
        val mgr = AppWidgetManager.getInstance(ctx)
        for (kind in WidgetKind.values()) {
            val ids = mgr.getAppWidgetIds(ComponentName(ctx, kind.provider))
            for (id in ids) update(ctx, mgr, kind, id)
        }
    }

    /** Fetches weather in the background when the cache is old, then re-renders everything. */
    fun refreshWeatherThenUpdate(ctx: Context, pending: BroadcastReceiver.PendingResult?, onDone: (() -> Unit)?) {
        val app = ctx.applicationContext
        Thread {
            try {
                if (Weather.isStale(app) && Weather.refresh(app)) updateAll(app)
            } catch (e: Exception) {
                // ignore
            } finally {
                pending?.finish()
                onDone?.invoke()
            }
        }.start()
    }
}
