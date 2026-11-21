package com.pulse.widgets.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.os.Bundle
import com.pulse.widgets.Prefs
import com.pulse.widgets.Scheduler
import com.pulse.widgets.WidgetKind
import com.pulse.widgets.WidgetUpdater

abstract class BaseWidget(private val kind: WidgetKind) : AppWidgetProvider() {

    override fun onUpdate(context: Context, mgr: AppWidgetManager, ids: IntArray) {
        for (id in ids) WidgetUpdater.update(context, mgr, kind, id)
        Scheduler.schedule(context)
        WidgetUpdater.refreshWeatherThenUpdate(context, goAsync(), null)
    }

    // called when the user resizes the widget: swap to the layout that fits the new size
    override fun onAppWidgetOptionsChanged(context: Context, mgr: AppWidgetManager, id: Int, newOptions: Bundle) {
        WidgetUpdater.update(context, mgr, kind, id)
    }

    override fun onEnabled(context: Context) {
        Scheduler.schedule(context)
    }

    override fun onDeleted(context: Context, ids: IntArray) {
        for (id in ids) Prefs.clearMonth(context, id)
    }
}
