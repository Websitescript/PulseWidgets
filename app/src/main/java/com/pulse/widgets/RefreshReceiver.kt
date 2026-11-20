package com.pulse.widgets

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class RefreshReceiver : BroadcastReceiver() {
    override fun onReceive(ctx: Context, intent: Intent) {
        if (intent.action == ACTION_NAV) {
            val id = intent.getIntExtra(EXTRA_ID, -1)
            val delta = intent.getIntExtra(EXTRA_DELTA, 0)
            if (id != -1) {
                val cur = Prefs.monthOffset(ctx, id)
                Prefs.setMonthOffset(ctx, id, if (delta == 0) 0 else cur + delta)
                AppWidgetManager.getInstance(ctx)
                    .updateAppWidget(id, WidgetBuilder.build(ctx, WidgetKind.CAL_MONTH_4X4, id))
            }
        } else {
            WidgetUpdater.updateAll(ctx)
        }
        MidnightScheduler.schedule(ctx)
    }

    companion object {
        const val ACTION_REFRESH = "com.pulse.widgets.REFRESH"
        const val ACTION_NAV = "com.pulse.widgets.NAV"
        const val EXTRA_ID = "id"
        const val EXTRA_DELTA = "delta"
    }
}
