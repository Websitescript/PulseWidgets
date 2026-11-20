package com.pulse.widgets.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import com.pulse.widgets.MidnightScheduler
import com.pulse.widgets.Prefs
import com.pulse.widgets.WidgetBuilder
import com.pulse.widgets.WidgetKind

abstract class BaseWidget(private val kind: WidgetKind) : AppWidgetProvider() {

    override fun onUpdate(context: Context, mgr: AppWidgetManager, ids: IntArray) {
        for (id in ids) mgr.updateAppWidget(id, WidgetBuilder.build(context, kind, id))
        MidnightScheduler.schedule(context)
    }

    override fun onEnabled(context: Context) = MidnightScheduler.schedule(context)

    override fun onDeleted(context: Context, ids: IntArray) {
        for (id in ids) Prefs.clearMonth(context, id)
    }
}
