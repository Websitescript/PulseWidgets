package com.pulse.widgets

import com.pulse.widgets.widgets.AllWidget
import com.pulse.widgets.widgets.AnalogWidget
import com.pulse.widgets.widgets.CalendarWidget
import com.pulse.widgets.widgets.DigitalWidget

enum class WidgetKind(val title: String, val provider: Class<*>) {
    ANALOG("Analog clock", AnalogWidget::class.java),
    DIGITAL("Digital clock", DigitalWidget::class.java),
    ALL("Clock + day + weather", AllWidget::class.java),
    CALENDAR("Calendar", CalendarWidget::class.java),
}

/** A layout plus which dynamic data blocks it contains. */
class Variant(val layout: Int, val wx: Boolean = false, val week: Boolean = false, val month: Boolean = false)

object Variants {
    fun of(kind: WidgetKind, sc: SizeClass): Variant = when (kind) {
        WidgetKind.ANALOG -> when (sc) {
            SizeClass.XS -> Variant(R.layout.w_analog_xs) // 1x1: clock only, no date
            SizeClass.TALL, SizeClass.M -> Variant(R.layout.w_analog_m)
            SizeClass.WIDE -> Variant(R.layout.w_analog_wide)
            SizeClass.L -> Variant(R.layout.w_analog_l, wx = true)
            SizeClass.XL -> Variant(R.layout.w_analog_xl, wx = true)
        }
        WidgetKind.DIGITAL -> when (sc) {
            SizeClass.XS -> Variant(R.layout.w_digital_xs) // 1x1: time only, no date
            SizeClass.TALL -> Variant(R.layout.w_digital_tall)
            SizeClass.WIDE -> Variant(R.layout.w_digital_wide)
            SizeClass.M -> Variant(R.layout.w_digital_m, wx = true)
            SizeClass.L -> Variant(R.layout.w_digital_l, wx = true)
            SizeClass.XL -> Variant(R.layout.w_digital_xl, wx = true)
        }
        WidgetKind.ALL -> when (sc) {
            SizeClass.XS -> Variant(R.layout.w_digital_xs)
            SizeClass.TALL -> Variant(R.layout.w_digital_tall)
            SizeClass.WIDE -> Variant(R.layout.w_all_wide, wx = true)
            SizeClass.M -> Variant(R.layout.w_all_m, wx = true)
            SizeClass.L -> Variant(R.layout.w_all_l, wx = true, week = true)
            SizeClass.XL -> Variant(R.layout.w_all_xl, wx = true, week = true)
        }
        WidgetKind.CALENDAR -> when (sc) {
            SizeClass.XS, SizeClass.TALL -> Variant(R.layout.w_cal_tile)
            SizeClass.WIDE -> Variant(R.layout.w_cal_strip)
            SizeClass.M -> Variant(R.layout.w_cal_card)
            SizeClass.L -> Variant(R.layout.w_cal_week, week = true)
            SizeClass.XL -> Variant(R.layout.w_cal_month, month = true)
        }
    }
}
