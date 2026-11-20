package com.pulse.widgets

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context

object WidgetUpdater {
    fun updateAll(ctx: Context) {
        val mgr = AppWidgetManager.getInstance(ctx)
        for (kind in WidgetKind.values()) {
            val ids = mgr.getAppWidgetIds(ComponentName(ctx, kind.provider))
            for (id in ids) mgr.updateAppWidget(id, WidgetBuilder.build(ctx, kind, id))
        }
    }
}
